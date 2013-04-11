package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.util.Randomizer;
import jp.gr.java_conf.daisy.ajax_mutator.util.Util;

import org.mozilla.javascript.ast.AstRoot;

/**
 * Executor to apply mutation testing to target applications. <br>
 * Note: Currently we assume that mutation target is single JavaScript file.
 *
 * @author Kazuki Nishiura
 */
public class MutationTestConductor {
    private boolean setup = false;
    private final PrintStream outputStream;
    private ParserWithBrowser parser;
    private String pathToJSFile;
    private AstRoot astRoot;
    private boolean conducting;
    private int[] skipCount;

    public MutationTestConductor() {
        this(System.out);
    }

    public MutationTestConductor(PrintStream output) {
        this.outputStream = output;
    }

    /**
     * Setting information required for mutation testing. This method MUST be
     * called before conducting mutation testing.
     *
     * @return if setup is successfully finished.
     */
    public boolean setup(
            final String pathToJSFile, String targetURL, MutateVisitor visitor) {
        setup = false;
        this.pathToJSFile = pathToJSFile;
        // create backup file
        Util.copyFile(pathToJSFile, pathToBackupFile());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // restore backup
                Util.copyFile(pathToBackupFile(), pathToJSFile);
                System.out.println("backup file restored");
            }
        });
        parser = ParserWithBrowser.getParser();
        try {
            FileReader fileReader = new FileReader(new File(pathToJSFile));
            astRoot = parser.parse(fileReader, targetURL, 1);
        } catch (IOException e) {
            System.err.println("IOException: cannot parse AST.");
            return false;
        }

        if (astRoot != null) {
            astRoot.visit(visitor);
            setup = true;
        } else {
            System.err.println("Cannot parse AST.");
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
            outputStream.println(mutationName + " ----------");
            while (!mutator.isFinished() && conducting) {
                boolean mutantsUnkilled = false;
                trialId++;
                outputStream.println("[" + trialId + "]");
                if (skipCount != null && skipCount[trialId] >= 0) {
                    skipLog[trialId] = skipCount[trialId];
                    Randomizer.increaseIndex(skipCount[trialId]);
                    numberOfMutants++;
                    mutator.skipMutation();
                    outputStream.println("skiped");
                    continue;
                }
                String mutationInformation = mutator.applyMutation();
                if (mutationInformation != null) {
                    Util.writeToFile(pathToJSFile, astRoot.toSource());
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
                        outputStream.println(message);
                    mutator.undoMutation();
                    numberOfMutants++;
                    System.out.println((trialId + 1) + "/" + numberOfMaxMutants
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
        outputStream.println();
        outputStream.println("---------------------------------------------");
        outputStream.println();
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

        outputStream.println(numberOfUnkilledMutatns + " unkilled mutants "
                + " among " + numberOfMutants + ", kill score is "
                + Math.floor((1.0 - (1.0 * numberOfUnkilledMutatns / numberOfMaxMutants)) * 100) / 100);

        outputStream.println(detailedInfo.toString());

        // restore backup
        Util.copyFile(pathToBackupFile(), pathToJSFile);
        System.out.println("Randomizer log: "
                + Arrays.toString(Randomizer.getReturnedValues()));
        System.out.println("skip log: " + Arrays.toString(skipLog));
        System.out.println("finished! "
                + (finishTimeMillis - startTimeMillis) / 1000.0 + " sec.");
    }

    private int calcMaxNumOfMutations(Set<Mutator> mutators) {
        int numberOfMaxMutants = 0;
        outputStream.println("-------Number of mutations------");
        for (Mutator mutator: mutators) {
            numberOfMaxMutants += mutator.numberOfMutation();
            outputStream.println(
                    mutator.mutationName() + ": " + mutator.numberOfMutation());
        }
        outputStream.println("Total: " + numberOfMaxMutants);
        outputStream.println();
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
                    System.out.println("I/O thread interrupt, "
                            + "which may mean program successfully finished");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            conducting = false;
            System.out.println("thread finish");
        }

        private boolean isQuitCommand(String command) {
            if (null == command || "q".equals(command))
                return true;

            System.out.println(command);
            return false;
        }
    }

    private String pathToBackupFile() {
        return pathToJSFile + ".backup";
    }
}
