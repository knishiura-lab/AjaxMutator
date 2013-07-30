package jp.gr.java_conf.daisy.ajax_mutator;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryEventAttachmentDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryRequestDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Mutatable;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.*;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.DOMSelectionSelectNearbyMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.*;
import jp.gr.java_conf.daisy.ajax_mutator.util.Util;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

import java.io.*;
import java.util.*;

import static org.junit.Assert.fail;

/**
 * High level test that checks if generated mutants are all syntactically valid.
 *
 * @author Kazuki Nishiura
 */
public class MutationValidityTest {
    UnifiedDiffGeneratorForTest diffGenerator
            = new UnifiedDiffGeneratorForTest(this.getClass().getResourceAsStream("/quizzy.js"));
    List<String> contentOfOriginalFile = readResourceFile("/quizzy.js");

    @BeforeClass
    public void normalizeLineBreak() {
        Util.normalizeLineBreak(
                new File(this.getClass().getResource("/quizzy.js").getFile()));
    }

    @Test
    public void testGeneratedMutantIsSyntacticallyValid() {
        MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
        builder.setRequestDetectors(ImmutableSet.of(new JQueryRequestDetector()));
        builder.setEventAttacherDetectors(ImmutableSet.<EventAttacherDetector>of(new JQueryEventAttachmentDetector()));
        MutateVisitor visitor = builder.build();

        ParserWithBrowser parser = ParserWithBrowser.getParser();
        AstRoot root = parser.parse(
                readResource("/quizzy.js"), "http://fake.dummy", 1);
        root.visit(visitor);

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
                new RequestUrlRAMutator(visitor.getRequests())
        );

        // Events
        generateMutationAndParse(visitor.getEventAttachments(), mutators);
        generateMutationAndParse(
                visitor.getTimerEventAttachmentExpressions(), mutators);
        // Asynchronous communications
        generateMutationAndParse(visitor.getRequests(), mutators);
        // DOM manipulations
        generateMutationAndParse(visitor.getDomCreations(), mutators);
        generateMutationAndParse(visitor.getDomAppendings(), mutators);
        generateMutationAndParse(visitor.getDomSelections(), mutators);
        generateMutationAndParse(visitor.getDomRemovals(), mutators);
    }

    private String readResource(String resource) {
        InputStream inputStream = this.getClass().getResourceAsStream(resource);
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(inputStream, writer);
        } catch (IOException e) {
            System.err.println(e);
        }
        return writer.toString();
    }

    private List<String> readResourceFile(String pathToResource) {
        InputStream stream = this.getClass().getResourceAsStream(pathToResource);
        List<String> contents = new ArrayList<String>();
        Scanner scanner = new Scanner(stream);
        while (scanner.hasNext()) {
            contents.add(scanner.nextLine());
        }
        return contents;
    }

    private void generateMutationAndParse(
            Set<? extends Mutatable> mutatables, Set<Mutator> mutators) {
        if (mutatables.size() == 0) {
            return;
        }

        Set<Mutator> applicableMutator = new HashSet<Mutator>();
        Mutatable aMutatable = Iterables.get(mutatables, 0);
        for (Mutator mutator: mutators) {
            if (mutator.isApplicable(aMutatable.getClass())) {
                applicableMutator.add(mutator);
            }
        }

        for (Mutator mutator: applicableMutator) {
            for (Mutatable mutatable: mutatables) {
                Mutation mutation = mutator.generateMutation(mutatable);
                if (mutation == null) {
                    continue;
                }

                String unifiedDiff = diffGenerator.generateUnifiedDiff(
                        mutation.getOriginalNode(),
                        Arrays.asList(mutation.getMutatingContent().split(System.lineSeparator())));

                Patch patch = DiffUtils.parseUnifiedDiff(splitByNewline(unifiedDiff));
                try {
                    String mutant = Joiner.on("").join(
                            patch.applyTo(contentOfOriginalFile));
                    ParserWithBrowser parser = ParserWithBrowser.getParser();
                    parser.parse(mutant, "http://mutant.dummy", 1);

                } catch (PatchFailedException e) {
                    fail("Mutant should be applicable");
                }
            }
        }
    }

    private List<String> splitByNewline(String str) {
        return Arrays.asList(str.split(System.lineSeparator()));
    }
}
