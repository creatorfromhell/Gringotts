package org.gestern.gringotts;

/**
 * The type Gringotts exception.
 */
public class GringottsException extends RuntimeException {

    private static final long serialVersionUID = 476895381491480536L;

    /**
     * Instantiates a new Gringotts exception.
     */
    public GringottsException() {
        super();
    }

    /**
     * Instantiates a new Gringotts exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public GringottsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Gringotts exception.
     *
     * @param message the message
     */
    public GringottsException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Gringotts exception.
     *
     * @param cause the cause
     */
    public GringottsException(Throwable cause) {
        super(cause);
    }
}
