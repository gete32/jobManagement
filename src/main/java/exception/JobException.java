package exception;

public class JobException extends Exception {

    public JobException() {
        super("Job cannot be null");
    }
}
