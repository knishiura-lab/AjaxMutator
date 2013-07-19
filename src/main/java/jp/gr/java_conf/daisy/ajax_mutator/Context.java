package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Singleton class that contains various information used across classes.
 */
public class Context {
    public static Context INSTANCE = new Context();

    private List<String> contentOfJsFile;
    // number of chars for each line in js file, including newline code
    private List<Integer> numOfCharsForLines;
    private String pathToJsFile;

    private Context() { }

    public void registerJsPath(String pathToJsFile) {
        this.pathToJsFile = pathToJsFile;
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(pathToJsFile));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
        contentOfJsFile = new ArrayList<String>();
        numOfCharsForLines = new ArrayList<Integer>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            contentOfJsFile.add(line);
            numOfCharsForLines.add(
                    line.length() + System.lineSeparator().length());
        }
    }

    public String getJsPath() {
        return pathToJsFile;
    }
}
