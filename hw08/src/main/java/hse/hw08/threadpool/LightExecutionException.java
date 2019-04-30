package hse.hw08.threadpool;

public class LightExecutionException extends Exception {

    public LightExecutionException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}