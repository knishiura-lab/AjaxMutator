package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventListenerDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AttachEventDetector;

import org.mozilla.javascript.ast.AstRoot;

public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException{
		ParserWithBrowser parser = ParserWithBrowser.getParser();
		AstRoot ast = parser.parse(new FileReader("data/tetris/tetris.js"), "test_target_URI", 1);
		if (ast != null) {
			EventAttacherDetector[] attahcerDetectorArray 
				= {new AddEventListenerDetector(), new AttachEventDetector()};
			Set<EventAttacherDetector> attacherDetector 
				= new HashSet<EventAttacherDetector>(Arrays.asList(attahcerDetectorArray)); 
			ast.visit(new MutateVisitor(attacherDetector));
		} else {
			System.err.println("Cannot parse AST.");
		}
	}
}
