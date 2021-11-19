package hr.fer.zemris.linear.exceptions;

public class IncompatibleOperandException extends RuntimeException {
    public IncompatibleOperandException() {
    }

    public IncompatibleOperandException(String message) {
        super(message);
    }
}
