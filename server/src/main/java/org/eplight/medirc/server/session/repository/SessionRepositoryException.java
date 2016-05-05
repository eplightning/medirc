package org.eplight.medirc.server.session.repository;

public class SessionRepositoryException extends Exception {

    public SessionRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionRepositoryException() {
    }

    public SessionRepositoryException(String message) {
        super(message);
    }
}
