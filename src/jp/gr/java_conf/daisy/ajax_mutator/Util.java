package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Util {
	public static boolean writeToFile(String pathToFile, String content) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(pathToFile));
			writer.write(content);
		} catch (IOException e) {
			System.err.println("IOException" + e.getMessage());
			return false;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				System.err.println("Fail to close source file" + e.getMessage());
				return false;
			}
		}
		return true;
	}
}
