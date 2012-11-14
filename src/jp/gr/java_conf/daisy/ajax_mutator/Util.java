package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;

public class Util {
	/**
	 * @return true if copy succeed.
	 */
	public static boolean copyFile(String srcPath, String destPath) {
		boolean success = false;
		FileChannel srcChannel = null;
		FileChannel destChannel = null;

		try {
			File dest = new File(destPath);
			if (dest.exists())
				dest.createNewFile();
			srcChannel = new FileInputStream(srcPath).getChannel();
			destChannel = new FileOutputStream(destPath).getChannel();

			srcChannel.transferTo(0, srcChannel.size(), destChannel);
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		} finally {
			try {
				if (srcChannel != null)
					srcChannel.close();
				if (destChannel != null)
					destChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return success;
	}

	/**
	 * read all string from specified file
	 *
	 * @return list of String, each element is a line in file. If some error
	 *         happen during reading, returns null.
	 */
	public static List<String> readFromFile(String pathToFile) {
		List<String> lines = new ArrayList<String>();
		boolean success = false;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(pathToFile)));
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success ? lines : null;
	}

	/**
	 * write content into a file with specified path. This method override
	 * existing file. Even if file is not exist, this method does not create new
	 * file.
	 *
	 * @return true if write is successfully finished.
	 */
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
				System.err.println(
						"Fail to close source file" + e.getMessage());
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
