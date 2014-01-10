package jp.gr.java_conf.daisy.ajax_mutator;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Mutatable;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationFileInformation;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationFileWriter;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationListManager;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.util.Randomizer;
import jp.gr.java_conf.daisy.ajax_mutator.util.Util;
import org.mozilla.javascript.ast.AstRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Executor to apply mutation testing to target applications. <br>
 * Note: Currently we assume that mutation target is single JavaScript file.
 *
 * @author Kazuki Nishiura
 */
public class MutationTestConductor {
    private static final Logger LOGGER
            = LoggerFactory.getLogger(MutationTestConductor.class);

    private MutationFileWriter mutationFileWriter;
    private MutationListManager mutationListManager;
    private Multimap<String, String> unkilledMutantsInfo;
    private Context context = Context.INSTANCE;
    private boolean setup = false;
    private ParserWithBrowser parser;
    private AstRoot astRoot;
    private boolean conducting;
    private boolean dryRun;
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
     * Check how many mutants are generated and so on.
     *
     * @return Mapping of mutator classes and how many times it should be applied.
     */
    public Map<Mutator, Integer>  dryRun(Set<Mutator> mutators) {
        dryRun = true;
        checkIfSetuped();
        mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
        generateMutationFiles(visitor, mutators);
        return numOfMutation;
    }

    /**
     * Generate mutation files corresponding to given {@link Mutator}.
     */
    public void generateMutations(Set<Mutator> mutators) {
        unkilledMutantsInfo = ArrayListMultimap.create();
        checkIfSetuped();
        mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
        generateMutationFiles(visitor, mutators);
        mutationListManager.generateMutationListFile();
    }

    /**
     * Generate mutation files corresponding to given {@link Mutator}, and then running test.
     */
    public void generateMutationsAndApplyTest(TestExecutor testExecutor, Set<Mutator> mutators) {
        Stopwatch stopwatch = new Stopwatch().start();
        generateMutations(mutators);
        applyMutationAnalysis(testExecutor, stopwatch);
    }

    public void mutationAnalysisUsingExistingMutations(TestExecutor testExecutor) {
        mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
        mutationListManager.readExistingMutationListFile();
        unkilledMutantsInfo = ArrayListMultimap.create();

        checkIfSetuped();
        applyMutationAnalysis(testExecutor, new Stopwatch().start());
    }

    private Map<Mutator, Integer> numOfMutation;
    private Map<Mutator, Integer> generateMutationFiles(
            MutateVisitor visitor, Set<Mutator> mutators) {
        numOfMutation = new HashMap<Mutator, Integer>();

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
        generateMutationFiles(visitor.getAttributeModifications(), mutators);

        LOGGER.debug("Random values used for generating mutations: "
                + Arrays.toString(Randomizer.getReturnedValues()));
        return numOfMutation;
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
            numOfMutation.put(mutator, 0);
            LOGGER.info("using {}", mutator.mutationName());
            for (Mutatable mutatable: mutatables) {
                @SuppressWarnings("unchecked")
                Mutation mutation = mutator.generateMutation(mutatable);
                if (mutation == null) {
                    LOGGER.info("Cannot create mutation for {} by using {}",
                            mutatable, mutator.mutationName());
                    continue;
                }
                numOfMutation.put(mutator, numOfMutation.get(mutator) + 1);
                if (dryRun) {
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

    private void applyMutationAnalysis(TestExecutor testExecutor, Stopwatch runningStopwatch) {
        conducting = true;
        addShutdownHookToRestoreBackup();
        int numberOfAppliedMutation = applyMutationAnalysis(testExecutor);
        runningStopwatch.stop();
        LOGGER.info("Updating mutation list file...");
        mutationListManager.generateMutationListFile();

        logExecutionDetail(numberOfAppliedMutation);
        LOGGER.info("restoring backup file...");
        Util.copyFile(pathToBackupFile(), context.getJsPath());
        LOGGER.info("finished! " + runningStopwatch.elapsedMillis() / 1000.0 + " sec.");
    }

    private int applyMutationAnalysis(TestExecutor testExecutor) {
        int numberOfAppliedMutation = 0;
        int numberOfMaxMutants
                = mutationListManager.getNumberOfUnkilledMutants();
        Thread commandReceiver = new Thread(new CommandReceiver());
        commandReceiver.start();
        List<String> original = Util.readFromFile(pathToJsFile);
        List<String> nameOfMutations = mutationListManager.getListOfMutationName();
        for (String description: nameOfMutations) {
            LOGGER.info("Start applying {}", description);
            for (MutationFileInformation mutationFileInformation:
                    mutationListManager.getMutationFileInformationList(description)) {
                if (mutationFileInformation.canBeSkipped()
                        || !applyMutationFile(original, mutationFileInformation)) {
                    continue;
                }
                numberOfAppliedMutation++;
                if (testExecutor.execute()) { // This mutants cannot be killed
                    unkilledMutantsInfo.put(description, mutationFileInformation.toString());
                    LOGGER.info("mutant {} is not be killed", description);
                } else {
                    mutationFileInformation.setState(MutationFileInformation.State.KILLED);
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
        return numberOfAppliedMutation;
    }

    private void logProgress(int finished, int total) {
        LOGGER.info("{} in {} finished: {} %", finished, total,
                Math.floor(finished * 1000.0 / total) / 10);
    }

    /**
     * @return if successfully file is wrote.
     */
    private boolean applyMutationFile(
            List<String> original, MutationFileInformation fileInfo) {
        Patch patch = DiffUtils.parseUnifiedDiff(
                Util.readFromFile(fileInfo.getAbsolutePath()));
        try {
            List<?> mutated = patch.<String>applyTo(original);
            Util.writeToFile(pathToJsFile,
                    Util.join(mutated.toArray(new String[0]), System.lineSeparator()));
        } catch (PatchFailedException e) {
            LOGGER.error("Applying mutation file '{}' failed.",
                    fileInfo.getFileName(), e);
            return false;
        }
        return true;
    }

    private void checkIfSetuped() {
        if (!setup)
            throw new IllegalStateException(
                    "You 'must' call setup method before you use.");
    }

    private void logExecutionDetail(int numberOfAppliedMutation) {
        LOGGER.info("---------------------------------------------");
        StringBuilder detailedInfo = new StringBuilder();
        int numberOfUnkilledMutatns = 0;
        for (String key: unkilledMutantsInfo.keySet()) {
            numberOfUnkilledMutatns += unkilledMutantsInfo.get(key).size();
            detailedInfo.append(key).append(": ")
                    .append(unkilledMutantsInfo.get(key).size())
                    .append(System.lineSeparator());
            for (String info: unkilledMutantsInfo.get(key)) {
                detailedInfo.append(info).append(System.lineSeparator());
            }
            detailedInfo.append(System.lineSeparator());
        }

         int numberOfMaxMutants
                = mutationListManager.getNumberOfUnkilledMutants();
        LOGGER.info(detailedInfo.toString());
        LOGGER.info(numberOfUnkilledMutatns + " unkilled mutants "
                + " among " + numberOfAppliedMutation + ", kill score is "
                + Math.floor((1.0 - (1.0 * numberOfUnkilledMutatns / numberOfMaxMutants)) * 100) / 100);
    }

    private void addShutdownHookToRestoreBackup() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // restore backup
                Util.copyFile(pathToBackupFile(), pathToJsFile);
                System.out.println("backup file restored");
            }
        });
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
