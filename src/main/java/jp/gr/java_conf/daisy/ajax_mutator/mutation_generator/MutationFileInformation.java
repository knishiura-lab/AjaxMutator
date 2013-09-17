package jp.gr.java_conf.daisy.ajax_mutator.mutation_generator;

/**
 * Data class that represents generated mutation file.
 *
 * @author Kazuki Nishiura
 */
public class MutationFileInformation {
    protected static final String LIVE_STRING = "live";
    protected static final String KILLED_STRING = "killed";
    private final String fileName;
    private final String absolutePath;
    private boolean killed;

    public MutationFileInformation(String fileName, String absolutePath) {
        this(fileName, absolutePath, false);
    }

    public MutationFileInformation(
            String fileName, String absolutePath, boolean killed) {
        this.fileName = fileName;
        this.absolutePath = absolutePath;
        this.killed = killed;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public boolean isKilled() {
        return killed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    public String getKilledStatusAsString() {
        return killed ? KILLED_STRING : LIVE_STRING;
    }

    @Override
    public String toString() {
        return fileName + ":" + getKilledStatusAsString();
    }
}
