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

    private String pathToJsFile;

    private Context() { }

    public void registerJsPath(String pathToJsFile) {
        this.pathToJsFile = pathToJsFile;
    }

    public String getJsPath() {
        return pathToJsFile;
    }
}
