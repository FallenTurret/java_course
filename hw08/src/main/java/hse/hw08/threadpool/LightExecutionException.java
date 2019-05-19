package hse.hw08.threadpool;

/**
 * Class for exceptions, which occurred in task execution
 */
public class LightExecutionException extends Exception {

    /**
     * Exception constructor
     * @param errorMessage exception's error message
     * @param err exception's cause
     */
    public LightExecutionException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}