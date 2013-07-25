package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationFileInformation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.util.Randomizer;
import jp.gr.java_conf.daisy.ajax_mutator.util.Util;

import org.mozilla.javascript.ast.AstRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executor to apply mutation testing to target applications. <br>
 * Note: Currently we assume that mutation target is single JavaScript file.
 *
 * @author Kazuki Nishiura
 */
public class MutationTestConductor {
    private static final Logger LOGGER
            = LoggerFactory.getLogger(MutationTestConductor.class);

    private Context context = Context.INSTANCE;
    private boolean setup = false;
    private ParserWithBrowser parser;
    private AstRoot astRoot;
    private boolean conducting;
    private String pathToJsFile;
    private int[] skipCount;

    /**
     * Setting information required for mutation testing. This method MUST be
     * called before conducting mutation testing.
     *
     * @return if setup is successfully finished.
     */
    public boolean setup(
            final String pathToJSFile, String targetURL, MutateVisitor visitor) {
        setup = false;
        this.pathToJsFile = pathToJSFile;
        context.registerJsPath(pathToJSFile);
        // create backup file
        Util.copyFile(pathToJSFile, pathToBackupFile());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // restore backup
                Util.copyFile(pathToBackupFile(), pathToJSFile);
                LOGGER.info("backup file restored");
            }
        });
        parser = ParserWithBrowser.getParser();
        try {
            FileReader fileReader = new FileReader(new File(pathToJSFile));
            astRoot = parser.parse(fileReader, targetURL, 1);
        } catch (IOException e) {
            LOGGER.error("IOException: cannot parse AST.");
            return false;
        }

        if (astRoot != null) {
            astRoot.visit(visitor);
            setup = true;
        } else {
            LOGGER.error("Cannot parse AST.");
        }
        return setup;
    }

    /**
     * Apply next mutation testing.
     * <ol>
     * <li>Apply mutation operator to target applications</li>
     * <li>Execute test by using testExecutor passed in arguments</li>
     * <li>Repair last applied mutation</li>
     * <li>Repeat until all possible mutation operation executed</li>
     * </ol>
     */
    public void conduct(TestExecutor testExecutor, Set<Mutator> mutators) {
        Map<String, List<String>> unkilledMutantsInfo
            = new HashMap<String, List<String>>();
        checkIfSetuped();
        int numberOfMutants = 0;

        int numberOfMaxMutants = calcMaxNumOfMutations(mutators);

        conducting = true;
        Thread commandReceiver = new Thread(new CommandReceiver());
        commandReceiver.start();
        long startTimeMillis = System.currentTimeMillis();
        int trialId = -1;
        int[] skipLog = new int[numberOfMaxMutants];
        Arrays.fill(skipLog, -1);
        int[] numberOfRandomizerCalled = new int[numberOfMaxMutants];
        for (Mutator mutator : mutators) {
            String mutationName = mutator.mutationName();
            LOGGER.info(mutationName + " ----------");
            while (!mutator.isFinished() && conducting) {
                boolean mutantsUnkilled = false;
                trialId++;
                LOGGER.info("[" + trialId + "]");
                if (skipCount != null && skipCount[trialId] >= 0) {
                    skipLog[trialId] = skipCount[trialId];
                    Randomizer.increaseIndex(skipCount[trialId]);
                    numberOfMutants++;
                    mutator.skipMutation();
                    LOGGER.info("skiped");
                    continue;
                }
                String mutationInformation = mutator.applyMutation();
                if (mutationInformation != null) {
                    Util.writeToFile(context.getJsPath(), astRoot.toSource());
                    if (testExecutor.execute()) { // This mutants cannot be killed
                        if (unkilledMutantsInfo.containsKey(mutationName)) {
                            unkilledMutantsInfo.get(mutationName).add(mutationInformation);
                        } else {
                            List<String> info = new ArrayList<String>();
                            info.add(mutationInformation);
                            unkilledMutantsInfo.put(mutationName, info);
                        }
                        mutantsUnkilled = true;
                    }
                    String message = testExecutor.getMessageOnLastExecution();
                    if (message != null)
                        LOGGER.info(message);
                    mutator.undoMutation();
                    numberOfMutants++;
                    LOGGER.info((trialId + 1) + "/" + numberOfMaxMutants
                            + "|" + Math.floor(10000 * (trialId + 1) / numberOfMaxMutants) / 100
                            + "%");
                }
                numberOfRandomizerCalled[trialId] = Randomizer.getNumberOfCalled();
                if (!mutantsUnkilled)
                    skipLog[trialId]
                            = numberOfRandomizerCalled[trialId]
                                    - ((trialId == 0) ? 0 : numberOfRandomizerCalled[trialId - 1]);
            }
            // execution can be canceled from outside.
            if (!conducting)
                break;
        }
        if (conducting) {
            commandReceiver.interrupt();
            conducting = false;
        }
        long finishTimeMillis = System.currentTimeMillis();
        LOGGER.info("---------------------------------------------");
        StringBuilder detailedInfo = new StringBuilder();
        int numberOfUnkilledMutatns = 0;
        for (Map.Entry<String, List<String>> unkilledMutantsInfoEntry
                : unkilledMutantsInfo.entrySet()) {
            numberOfUnkilledMutatns
                += unkilledMutantsInfoEntry.getValue().size();
            detailedInfo.append(unkilledMutantsInfoEntry.getKey()).append(": ")
                .append(unkilledMutantsInfoEntry.getValue().size()).append('\n');
            for (String info: unkilledMutantsInfoEntry.getValue()){
                detailedInfo.append(info).append('\n');
            }
            detailedInfo.append('\n');
        }

        LOGGER.info(numberOfUnkilledMutatns + " unkilled mutants "
                + " among " + numberOfMutants + ", kill score is "
                + Math.floor((1.0 - (1.0 * numberOfUnkilledMutatns / numberOfMaxMutants)) * 100) / 100);

        LOGGER.info(detailedInfo.toString());

        // restore backup
        Util.copyFile(pathToBackupFile(), context.getJsPath());
        LOGGER.info("Randomizer log: "
                + Arrays.toString(Randomizer.getReturnedValues()));
        LOGGER.info("skip log: " + Arrays.toString(skipLog));
        LOGGER.info("finished! "
                + (finishTimeMillis - startTimeMillis) / 1000.0 + " sec.");
    }

    private boolean applyMutationFile(
            List<String> original, MutationFileInformation fileInfo) {
        Patch patch = DiffUtils.parseUnifiedDiff(
                Util.readFromFile(fileInfo.getAbsolutePath()));
        try {
            List mutated = patch.applyTo(original);
            Util.writeToFile(pathToJsFile,
                    Util.join((String[]) mutated.toArray(new String[0]),
                            System.lineSeparator()));
        } catch (PatchFailedException e) {
            LOGGER.error("Applying mutation file '{}' failed.",
                    fileInfo.getFileName());
            return false;
        }
        return true;
    }

    private int calcMaxNumOfMutations(Set<Mutator> mutators) {
        int numberOfMaxMutants = 0;
        LOGGER.info("-------Number of mutations------");
        for (Mutator mutator: mutators) {
            numberOfMaxMutants += mutator.numberOfMutation();
            LOGGER.info(
                    mutator.mutationName() + ": " + mutator.numberOfMutation());
        }
        LOGGER.info("Total: " + numberOfMaxMutants);
        return numberOfMaxMutants;
    }

    public void conductWithJunit4(Set<Mutator> mutators, Class<?>... classes) {
        conduct(new JUnitExecutor(classes), mutators);
    }

    private void checkIfSetuped() {
        if (!setup)
            throw new IllegalStateException(
                    "You 'must' call setup method before you use.");
    }

    /**
     * client can pass skip count to this class, so that this class can avoid
     * executing tests against mutants which have been already killed in past.
     */
    public void setSkipCount(int[] skipCount) {
        this.skipCount = skipCount;
    }

    private class CommandReceiver implements Runnable {
        @Override
        public void run() {
            BufferedReader reader
                = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    while (conducting && !reader.ready()) {
                        Thread.sleep(200);
                    }
                    if (!conducting || isQuitCommand(reader.readLine()))
                        break;
                } catch (InterruptedException e) {
                    LOGGER.info("I/O thread interrupt, "
                            + "which may mean program successfully finished");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            conducting = false;
            LOGGER.info("thread finish");
        }

        private boolean isQuitCommand(String command) {
            if (null == command || "q".equals(command))
                return true;

            LOGGER.info(command);
            return false;
        }
    }

    private String pathToBackupFile() {
        return context.getJsPath() + ".backup";
    }
}
