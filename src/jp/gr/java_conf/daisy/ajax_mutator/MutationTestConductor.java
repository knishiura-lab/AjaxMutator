package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
	public boolean setup(final String pathToJSFile, String targetURL, MutateVisitor visitor) {
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
		List<String> unkilledMutatns = new ArrayList<String>();
		checkIfSetuped();
		int numberOfMutants = 0;
		conducting = true;
		Thread commandReceiver = new Thread(new CommandReceiver());
		commandReceiver.start();
		for (Mutator mutator: mutators) {
			while (!mutator.isFinished() && conducting) {
				String mutationInformation = mutator.applyMutation();
				if (mutationInformation == null)
					continue;
				Util.writeToFile(pathToJSFile, astRoot.toSource());
				if (testExecutor.execute())
					unkilledMutatns.add(mutationInformation);
				String message = testExecutor.getMessageOnLastExecution();
				if (message != null)
					outputStream.println(message);
				mutator.undoMutation();
				numberOfMutants++;
			}
			// execution can be canceled from outside.
			if (!conducting)
				break;
		}
		if (conducting) {
			commandReceiver.interrupt();
			conducting = false;
		}
		outputStream.println("unkilled mutants (" + unkilledMutatns.size()
				+ " among " + numberOfMutants + "):");
		for (String line: unkilledMutatns)
			outputStream.println(line);

		// restore backup
		Util.copyFile(pathToBackupFile(), pathToJSFile);
		System.out.println("finished!");
	}

	public void conductWithJunit4(Set<Mutator> mutators, Class<?>... classes) {
		conduct(new JUnitExecutor(classes), mutators);
	}

	private void checkIfSetuped() {
		if (!setup)
			throw new IllegalStateException("You 'must' call setup method before you use.");
	}

	private class CommandReceiver implements Runnable {
		@Override
		public void run() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				try {
					while (conducting && !reader.ready()) {
						Thread.sleep(200);
					}
					if (!conducting || isQuitCommand(reader.readLine()))
						break;
				} catch (InterruptedException e) {
					System.out.println(
							"I/O thread interrupt, which may mean program successfully finished");
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
