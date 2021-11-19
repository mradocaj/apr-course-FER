package hr.fer.zemris.linear.exceptions;

public class ReadOnlyException extends RuntimeException {

    public ReadOnlyException() {
    }

    public ReadOnlyException(String message) {
        super(message);
    }
}
