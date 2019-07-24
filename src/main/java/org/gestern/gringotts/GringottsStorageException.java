package org.gestern.gringotts;

/**
 * The type Gringotts storage exception.
 */
public class GringottsStorageException extends RuntimeException {

    private static final long serialVersionUID = -7762154730712697492L;

    /**
     * Instantiates a new Gringotts storage exception.
     */
    public GringottsStorageException() {
        super();
    }

    /**
     * Instantiates a new Gringotts storage exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public GringottsStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Gringotts storage exception.
     *
     * @param message the message
     */
    public GringottsStorageException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Gringotts storage exception.
     *
     * @param cause the cause
     */
    public GringottsStorageException(Throwable cause) {
        super(cause);
    }
}
