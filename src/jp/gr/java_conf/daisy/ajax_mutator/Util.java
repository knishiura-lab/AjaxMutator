package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.mozilla.javascript.ast.AstNode;

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
	
	public static String oneLineStringOf(AstNode node) {
		if (node == null)
			return "";
		String[] str = node.toSource().split("\n", 2);
		if (str.length == 1)
			return str[0];
		else
			return str[0] + "... ";
	}
	
	public static String join(String[] arrayOfString) {
		return join(arrayOfString, null);
	}
	
	public static String join(String[] arrayOfString, String separator) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < arrayOfString.length; i++) {
			builder.append(arrayOfString[i]);
			if (separator != null && i == arrayOfString.length - 1)
				builder.append(separator);
		}
		return builder.toString();
	}
}
