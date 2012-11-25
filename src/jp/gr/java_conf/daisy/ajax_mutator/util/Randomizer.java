package jp.gr.java_conf.daisy.ajax_mutator.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Doubles;

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

	// This member is used only when testMode is false.
	private static List<Double> returnedValueLog = new ArrayList<Double>();

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
	 * @return values that returned from this class since start of the program
	 * 	       to this method call.
	 */
	static public double[] getReturnedValues() {
		return Doubles.toArray(returnedValueLog);
	}

	/**
	 * @return integer that indicates how many time this rondomizer is called.
	 */
	static public int getNumberOfCalled() {
		return index != 0 ? index : returnedValueLog.size();
	}

	/**
	 * @return index which is used internally to return preset values.
	 */
	static public void increaseIndex(int delta) {
		index += delta;
	}

	/**
	 * @return if not testMode, behaves as Math.random(), if testMode, returns
	 *         predefined values given by {@code setValues}.
	 */
	static public double getDouble() {
		if (testMode)
			return values[index++];
		else {
			double ret = Math.random();
			returnedValueLog.add(ret);
			return ret;
		}
	}

	/**
	 * @return random integer, which is bigger than or equals to zero and less
	 *         than upperBound
	 */
	static public int getInt(int upperBound) {
		if (testMode)
			return (int) values[index++];
		else {
			double ret = Math.floor(Math.random() * upperBound);
			returnedValueLog.add(ret);
			return (int) ret;
		}
	}
}
