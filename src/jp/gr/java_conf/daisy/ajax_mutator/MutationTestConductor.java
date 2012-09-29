package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventListenerDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AttachEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventTargetMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;

import org.mozilla.javascript.ast.AstRoot;

/**
 * Executor to apply mutation testing to target applications.
 * Note: Currently we assume that mutation target is single JavaScript file.
 * 
 * @author Kazuki Nishiura
 */
public class MutationTestConductor {
	private boolean setup;
	private ParserWithBrowser parser;
	private String pathToJSFile;
	private AstRoot astRoot;
	private Set<EventAttachment> eventAttachments;
	private List<Mutator> mutators;
	
	public MutationTestConductor() {
		setup = false;
	}
	
	/**
	 * @return if setup is successfully finished.
	 */
	public boolean setup(String pathToJSFile, String targetURL) {
		this.pathToJSFile = pathToJSFile;
		parser = ParserWithBrowser.getParser();
		try {
			FileReader fileReader = new FileReader(new File(pathToJSFile));
			astRoot = parser.parse(fileReader, targetURL, 1);
		} catch (IOException e) {
			System.err.println("IOException: cannot parse AST.");
			return false;
		}
		if (astRoot != null) {
			EventAttacherDetector[] attahcerDetectorArray 
				= {new AddEventDetector()};
			Set<EventAttacherDetector> attacherDetector 
				= new HashSet<EventAttacherDetector>(Arrays.asList(attahcerDetectorArray));
			MutateVisitor visitor = new MutateVisitor(attacherDetector);
			astRoot.visit(visitor);
			eventAttachments = visitor.getEventAttachments();
			Mutator[] mutatorsArray = {new EventTargetMutator(eventAttachments)};
			mutators = Arrays.asList(mutatorsArray);
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
		for (int i = 0; i < mutators.size(); i++) {
			Mutator mutator = mutators.get(i);
			while (!mutator.isFinished()) {
				mutator.applyMutation();
				FileWriter writer = null;
				try {
					writer = new FileWriter(new File(pathToJSFile));
					writer.write(astRoot.toSource());
				} catch (IOException e) {
					System.err.println("IOException" + e.getMessage());
				} finally {
					try {
						writer.close();
					} catch (IOException e) {
						System.err.println("Fail to close source file" + e.getMessage());
					}
				}
				
				String result = testExecutor.execute();
				System.out.println(result);
				mutator.undoMutation();
			}
		}
	}
	
	public void conductWithJunit4(Class<?>... classes) {
		conduct(new JUnitExecutor(classes));
	}
	
	private void checkIfSetuped() {
		if (!setup)
			throw new IllegalStateException("You 'must' call setup method before you use.");
	}
}
