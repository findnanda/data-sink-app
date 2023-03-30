package uk.co.data.sink.exception;

public class RecordNotSavedException extends RuntimeException {

    public RecordNotSavedException(final String message) {
        super(message);
    }
}
