package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventTargetMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventTypeMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;

import org.mozilla.javascript.ast.AstRoot;

/**
 * Executor to apply mutation testing to target applications.
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
	private Set<EventAttachment> eventAttachments;
	private List<Mutator> mutators;
	private boolean conducting;

	public MutationTestConductor() {
		this(System.out);
	}

	public MutationTestConductor(PrintStream output) {
		this.outputStream = output;
	}

	/**
	 * Setting information required for mutation testing. This method MUST
	 * be called before conducting mutation testing.
	 *
	 * @return if setup is successfully finished.
	 */
	public boolean setup(String pathToJSFile, String targetURL) {
		setup = false;
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
			MutateVisitor visitor = new MutateVisitor(attacherDetector, null, null, null, null, null, null);
			astRoot.visit(visitor);
			eventAttachments = visitor.getEventAttachments();
			Mutator[] mutatorsArray
				= {new EventTargetMutator(outputStream, eventAttachments),
				   new EventTypeMutator(outputStream, eventAttachments)};
			mutators = Arrays.asList(mutatorsArray);
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
	public void conduct(TestExecutor testExecutor) {
		checkIfSetuped();
		conducting = true;
		Thread commandReceiver = new Thread(new CommandReceiver());
		commandReceiver.start();
		for (Mutator mutator: mutators) {
			while (!mutator.isFinished() && conducting) {
				mutator.applyMutation();
				Util.writeToFile(pathToJSFile, astRoot.toSource());
				String result = testExecutor.execute();
				outputStream.println(result);
				mutator.undoMutation();
			}
			if (!conducting)
				break;
		}
		if (conducting)
			commandReceiver.interrupt();
		System.out.println("finished!");
	}

	public void conductWithJunit4(Class<?>... classes) {
		conduct(new JUnitExecutor(classes));
	}

	private void checkIfSetuped() {
		if (!setup)
			throw new IllegalStateException("You 'must' call setup method before you use.");
	}

	private class CommandReceiver implements Runnable {
		@Override
		public void run() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while (conducting) {
				try {
					while (!reader.ready()) {
						Thread.sleep(200);
					}
					String command = reader.readLine();
					if (null == command)
						break;
					else if ("q".equals(command))
						break;

					System.out.println(command);
				} catch (IOException e) {
					break;
				} catch (InterruptedException e) {
					break;
				}
			}
			conducting = false;
			System.out.println("thread finish");
		}
	}
}
