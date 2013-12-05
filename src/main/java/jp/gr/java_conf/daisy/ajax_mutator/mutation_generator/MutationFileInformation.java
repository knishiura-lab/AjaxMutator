package jp.gr.java_conf.daisy.ajax_mutator.mutation_generator;

/**
 * Data class that represents generated mutation file.
 *
 * @author Kazuki Nishiura
 */
public class MutationFileInformation {
    private final String fileName;
    private final String absolutePath;
    private State state;

    public enum State {
        NON_EQUIVALENT_LIVE("non-equivalent live"), EQUIVALENT("equivalent"), KILLED("killed");

        private final String stringExpression;
        private State(String stringExpression) {
            this.stringExpression = stringExpression;
        }

        public static State fromString(String key) {
            key = key.trim();
            for (int i = 0; i < values().length; i++) {
                if (values()[i].stringExpression.equals(key)) {
                    return values()[i];
                }
            }
            return null;
        }
    }

    public MutationFileInformation(String fileName, String absolutePath) {
        this(fileName, absolutePath, State.NON_EQUIVALENT_LIVE);
    }

    public MutationFileInformation(
            String fileName, String absolutePath, State state) {
        this.fileName = fileName;
        this.absolutePath = absolutePath;
        this.state = state;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public boolean canBeSkipped() {
        return state != State.NON_EQUIVALENT_LIVE;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public String getKilledStatusAsString() {
        return state.stringExpression;
    }

    @Override
    public String toString() {
        return fileName + ":" + getKilledStatusAsString();
    }
}
