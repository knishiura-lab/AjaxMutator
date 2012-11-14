package jp.gr.java_conf.daisy.ajax_mutator;

/**
 * Wrapper of Math.random. When conducting test, we can set testMode flat true,
 * and give values as future return values of this randomizer.
 *
 * @author Kazuki Nishiura
 */
public class Randomizer {
	private static boolean testMode = false;

	// These two members only used in test mode to return predefined numbers
	// instead of randomly generated values
	private static double[] values;
	private static int index = 0;

	private Randomizer() {};

	static public void setTestMode(boolean testMode) {
		Randomizer.testMode = testMode;
	}

	/**
	 * set values to be returned from Randomizer and make it test mode.
	 */
	static public void setValues(double[] values) {
		Randomizer.values = values;
		setTestMode(true);
	}

	/**
	 * @return if not testMode, behaves as Math.random(), if testMode, returns
	 *         predefined values given by {@code setValues}.
	 */
	static public double getDouble() {
		if (testMode)
			return values[index++];
		else
			return Math.random();
	}

	/**
	 * @return random integer, which is bigger than or equals to zero and less
	 *         than upperBound
	 */
	static public int getInt(int upperBound) {
		if (testMode)
			return (int) values[index++];
		else
			return (int) Math.floor(Math.random() * upperBound);
	}
}
