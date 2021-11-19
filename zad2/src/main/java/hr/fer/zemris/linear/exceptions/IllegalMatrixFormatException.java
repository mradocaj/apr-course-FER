package hr.fer.zemris.linear.exceptions;

public class IllegalMatrixFormatException extends RuntimeException {

    public IllegalMatrixFormatException() {
    }

    public IllegalMatrixFormatException(String message) {
        super(message);
    }
}
