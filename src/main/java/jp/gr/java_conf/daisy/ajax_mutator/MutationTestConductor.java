package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.*;

import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationFileInformation;

import com.google.common.collect.Iterables;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Mutatable;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.*;

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

    MutationFileWriter mutationFileWriter;
    MutationListManager mutationListManager;
    private Context context = Context.INSTANCE;
    private boolean setup = false;
    private ParserWithBrowser parser;
    private AstRoot astRoot;
    private boolean conducting;
    private MutateVisitor visitor;
    private String pathToJsFile;

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
        this.pathToJsFile = pathToJSFile;
        File jsFile = new File(pathToJSFile);
        Util.normalizeLineBreak(jsFile);
        mutationFileWriter = new MutationFileWriter(jsFile);
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
            FileReader fileReader = new FileReader(jsFile);
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
        this.visitor = visitor;
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
        double startTimeMillis = System.currentTimeMillis();

        mutationListManager = new MutationListManager(
                mutationFileWriter.getDestinationDirectory());
        generateMutationFiles(visitor, mutators);
        mutationListManager.generateMutationListFile();

        // applying it.
        int numberOfAppliedMutation = 0;
        int numberOfMaxMutants
                = mutationListManager.getNumberOfUnkilledMutants();
        conducting = true;
        Thread commandReceiver = new Thread(new CommandReceiver());
        commandReceiver.start();
        List<String> original = Util.readFromFile(pathToJsFile);
        Map<String, List<MutationFileInformation>> mutationFiles
                = mutationListManager.getMutationFileInformationList();
        for (String mutationDescription: mutationFiles.keySet()) {
            LOGGER.info("Start applying {}", mutationDescription);
            for (MutationFileInformation mutationFileInformation:
                    mutationFiles.get(mutationDescription)) {
                if (mutationFileInformation.isKilled()
                        || !applyMutationFile(original, mutationFileInformation)) {
                    continue;
                }
                numberOfAppliedMutation++;
                if (testExecutor.execute()) { // This mutants cannot be killed
                    LOGGER.info("mutant {} is not be killed", mutationDescription);
                } else {
                    mutationFileInformation.setKilled(true);
                }
                String message = testExecutor.getMessageOnLastExecution();
                if (message != null) {
                    LOGGER.info(message);
                }
                logProgress(numberOfAppliedMutation, numberOfMaxMutants);
            }
            // execution can be canceled from outside.
            if (!conducting) {
                break;
            }
        }
        if (conducting) {
            commandReceiver.interrupt();
            conducting = false;
        }
        LOGGER.info("Updating mutation list file...");
        mutationListManager.generateMutationListFile();

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

        LOGGER.info(detailedInfo.toString());
        LOGGER.info(numberOfUnkilledMutatns + " unkilled mutants "
                + " among " + numberOfAppliedMutation + ", kill score is "
                + Math.floor((1.0 - (1.0 * numberOfUnkilledMutatns / numberOfMaxMutants)) * 100) / 100);
        LOGGER.info("restoring backup file...");
        Util.copyFile(pathToBackupFile(), context.getJsPath());
        LOGGER.info("finished! "
                + (finishTimeMillis - startTimeMillis) / 1000.0 + " sec.");
    }

    private void generateMutationFiles(
            MutateVisitor visitor, Set<Mutator> mutators) {
        // Events
        generateMutationFiles(visitor.getEventAttachments(), mutators);
        generateMutationFiles(
                visitor.getTimerEventAttachmentExpressions(), mutators);
        // Asynchronous communications
        generateMutationFiles(visitor.getRequests(), mutators);
        // DOM manipulations
        generateMutationFiles(visitor.getDomCreations(), mutators);
        generateMutationFiles(visitor.getDomAppendings(), mutators);
        generateMutationFiles(visitor.getDomSelections(), mutators);
        generateMutationFiles(visitor.getDomRemovals(), mutators);

        LOGGER.debug("Random values used for generating mutations: "
                + Arrays.toString(Randomizer.getReturnedValues()));
    }

    private void generateMutationFiles(
            Set<? extends Mutatable> mutatables, Set<Mutator> mutators) {
        if (mutatables.size() == 0) {
            return;
        }

        Set<Mutator> applicableMutator = new HashSet<Mutator>();
        Mutatable aMutatable = Iterables.get(mutatables, 0);
        LOGGER.info("try to create mutations for {}. {} elements exist.",
                aMutatable.getClass().getSimpleName(), mutatables.size());
        for (Mutator mutator: mutators) {
            if (mutator.isApplicable(aMutatable.getClass())) {
                applicableMutator.add(mutator);
            }
        }

        for (Mutator mutator: applicableMutator) {
            LOGGER.info("using {}", mutator.mutationName());
            for (Mutatable mutatable: mutatables) {
                Mutation mutation = mutator.generateMutation(mutatable);
                if (mutation == null) {
                    LOGGER.info("Cannot create mutation for {} by using {}",
                            mutatable, mutator.mutationName());
                    continue;
                }
                File generatedFile = mutationFileWriter.writeToFile(mutation);
                if (generatedFile == null) {
                    LOGGER.error("failed to generate mutation file");
                    continue;
                }
                mutationListManager.addMutationFileInformation(
                        mutator.mutationName(),
                        new MutationFileInformation(
                                generatedFile.getName(),
                                generatedFile.getAbsolutePath())
                );
            }
        }
    }

    private void logProgress(int finished, int total) {
        LOGGER.info("{} in {} finished: {}%", finished, total,
                Math.floor(finished / total * 1000) / 10);
    }

    /**
     * @return if successfully file is wrote.
     */
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

    public void conductWithJunit4(Set<Mutator> mutators, Class<?>... classes) {
        conduct(new JUnitExecutor(classes), mutators);
    }

    private void checkIfSetuped() {
        if (!setup)
            throw new IllegalStateException(
                    "You 'must' call setup method before you use.");
    }

    private class CommandReceiver implements Runnable {
        @Override
        public void run() {
            BufferedReader reader
                = new BufferedReader(new InputStreamReader(System.in));
            LOGGER.info("You can stop execution any time by entering 'q'");
            while (true) {
                try {
                    while (conducting && !reader.ready()) {
                        Thread.sleep(300);
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
