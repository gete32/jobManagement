package exception;

public class JobStatusException extends Exception {

    public JobStatusException() {
        super("Wrong Job status");
    }
}
