package org.gestern.gringotts;

/**
 * The type Gringotts configuration exception.
 */
class GringottsConfigurationException extends RuntimeException {

    private static final long serialVersionUID = -2916461691910235253L;

    /**
     * Instantiates a new Gringotts configuration exception.
     */
    public GringottsConfigurationException() {
        super();
    }

    /**
     * Instantiates a new Gringotts configuration exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public GringottsConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Gringotts configuration exception.
     *
     * @param message the message
     */
    public GringottsConfigurationException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Gringotts configuration exception.
     *
     * @param cause the cause
     */
    public GringottsConfigurationException(Throwable cause) {
        super(cause);
    }
}
