package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.AttributeAssignmentDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.CreateElementDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.DOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.RemoveChildDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventListenerDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AttachEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.TimerEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryAttributeModificationDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryDOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryEventAttachmentDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryRequestDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMCreation;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMRemoval;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;

import org.mozilla.javascript.ast.AstRoot;

import com.google.common.collect.ImmutableSet;

public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException{
		ParserWithBrowser parser = ParserWithBrowser.getParser();
		AstRoot ast = parser.parse(new FileReader("data/tetris/tetris.js"), "test_target_URI", 1);
		if (ast != null) {
			EventAttacherDetector[] attahcerDetectorArray
				= {new AddEventListenerDetector(), new AttachEventDetector(), new JQueryEventAttachmentDetector()};
			Set<EventAttacherDetector> attacherDetector
				= new HashSet<EventAttacherDetector>(Arrays.asList(attahcerDetectorArray));
			Set<TimerEventDetector> timerDetector = ImmutableSet.of(new TimerEventDetector());
			Set<? extends AbstractDetector<DOMCreation>> creationDetector
					= ImmutableSet.of(new CreateElementDetector());
			Set<? extends AbstractDetector<AttributeModification>> modificationDetector
					= ImmutableSet.of(new AttributeAssignmentDetector(),
							new JQueryAttributeModificationDetector());
			Set<? extends AbstractDetector<DOMRemoval>> removalDetector
					= ImmutableSet.of(new RemoveChildDetector());
			Set<? extends AbstractDetector<DOMSelection>> selectionDetector
					= ImmutableSet.of(new DOMSelectionDetector(), new JQueryDOMSelectionDetector());
			Set<? extends AbstractDetector<Request>> requestDetector
					= ImmutableSet.of(new JQueryRequestDetector());
			MutateVisitor visitor = new MutateVisitor(attacherDetector, timerDetector,
					creationDetector, modificationDetector, removalDetector,
					selectionDetector, requestDetector);
			ast.visit(visitor);
			System.out.println(visitor.MutatablesInfo());
		} else {
			System.err.println("Cannot parse AST.");
		}
	}
}
