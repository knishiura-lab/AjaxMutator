package jp.gr.java_conf.daisy.ajax_mutator.sample.quizzy;

import com.google.common.collect.ImmutableSet;
import jp.gr.java_conf.daisy.ajax_mutator.JUnitExecutor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.MutationTestConductor;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryEventAttachmentDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryRequestDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.DOMSelectionSelectNearbyMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;


public class MutationAnalysisManager {
    private static final String PROPERTIES_FILE_PATH = "project.properties";
    private static final String TARGET_URL_KEY = "quizzy_url";
    private static final String JS_FILE_PATH = "quizzy_js_path";
    private static String targetURL;
    private static String pathToJsFile;

    public static void main(String[] args) {
        if (!readProperties()) {
            return;
        }
        MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
        builder.setRequestDetectors(ImmutableSet.of(new JQueryRequestDetector()));
        builder.setEventAttacherDetectors(ImmutableSet.<EventAttacherDetector>of(new JQueryEventAttachmentDetector()));
        MutateVisitor visitor = builder.build();

        MutationTestConductor conductor = new MutationTestConductor();
        conductor.setup(pathToJsFile, targetURL, visitor);
        Set<Mutator> mutators = ImmutableSet.<Mutator>of(
                new EventTargetRAMutator(visitor.getEventAttachments()),
                new EventTypeRAMutator(visitor.getEventAttachments()),
                new EventCallbackRAMutator(visitor.getEventAttachments()),
                new TimerEventDurationRAMutator(visitor.getTimerEventAttachmentExpressions()),
                new TimerEventCallbackRAMutator(visitor.getTimerEventAttachmentExpressions()),
                new AppendedDOMRAMutator(visitor.getDomAppendings()),
                new AttributeModificationTargetRAMutator(visitor.getAttributeModifications()),
                new AttributeModificationValueRAMutator(visitor.getAttributeModifications()),
                new DOMSelectionSelectNearbyMutator(),
                new RequestOnSuccessHandlerRAMutator(visitor.getRequests()),
                new RequestMethodRAMutator(visitor.getRequests()),
                new RequestUrlRAMutator(visitor.getRequests()));
        conductor.generateMutationsAndApplyTest(new JUnitExecutor(QuizzyTest.class), mutators);
        // Note: If you have already generated mutation files, and want to calculate mutation score,
        // You should use following method instead.
        // conductor.mutationAnalysisUsingExistingMutations(new JUnitExecutor(QuizzyTest.class));
    }

    /**
     * @return if successfully read properties file.
     */
    private static boolean readProperties() {
        try {
            Properties prop = new Properties();
            prop.load(ClassLoader.getSystemClassLoader().getResourceAsStream(PROPERTIES_FILE_PATH));
            targetURL = prop.getProperty(TARGET_URL_KEY);
            pathToJsFile = prop.getProperty(JS_FILE_PATH);
            if (targetURL == null || pathToJsFile == null) {
                System.err.println("Cannot read properties file's content. You MUST define "
                        + TARGET_URL_KEY + " and " + JS_FILE_PATH);
                return false;
            }
            return true;
        } catch (FileNotFoundException e) {
            System.err.println("Property file not found. You MUST create " + PROPERTIES_FILE_PATH
                    + " file and define " + TARGET_URL_KEY + " and " + JS_FILE_PATH);
        } catch (IOException e) {
            System.err.println("Cannot open properties file " + PROPERTIES_FILE_PATH);
        }
        return false;
    }
}
