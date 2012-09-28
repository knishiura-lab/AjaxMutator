package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventListenerDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AttachEventDetector;

import org.mozilla.javascript.ast.AstRoot;

/**
 * Executor to apply mutation testing to target applications.
 * 
 * @author Kazuki Nishiura
 */
public class MutationTestConductor {
	private boolean setup;
	private ParserWithBrowser parser;
	private MutateVisitor visitor;
	
	public MutationTestConductor() {
		setup = false;
	}
	
	public boolean setup(FileReader fileReader, String targetURL) {
		parser = ParserWithBrowser.getParser();
		AstRoot ast = null;
		try {
			ast = parser.parse(fileReader, targetURL, 1);
		} catch (IOException e) {
			System.err.println("IOException: cannot parse AST.");
			return false;
		}
		if (ast != null) {
			EventAttacherDetector[] attahcerDetectorArray 
				= {new AddEventListenerDetector(), new AttachEventDetector()};
			Set<EventAttacherDetector> attacherDetector 
				= new HashSet<EventAttacherDetector>(Arrays.asList(attahcerDetectorArray));
			visitor = new MutateVisitor(attacherDetector);
			ast.visit(visitor);
			setup = true;
			return true;
		} else {
			System.err.println("Cannot parse AST.");
			return false;
		}
	}
	
	/**
	 * Apply next mutation testing. 
	 * <ul>
	 * <li>1. Apply mutation operator to target applications</li>
	 * <li>2. Execute test by using testExecutor passed in arguments</li>
	 * <li>3. Repair last applied mutation</li>
	 * <li>4. Repeat until all possible mutation operation executed</li>
	 * </ul>
	 */
	public void conduct(TestExecutor testExecutor) {
		checkIfSetuped();
		String result = testExecutor.execute();
		System.out.println(result);
	}
	
	public void conductWithJunit4(Class<?>... classes) {
		conduct(new JUnitExecutor(classes));
	}
	
	private void checkIfSetuped() {
		if (!setup)
			throw new IllegalStateException("You 'must' call setup method before you use.");
	}
}
