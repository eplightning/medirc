package org.eplight.medirc.server.image.repo;

/**
 * Created by EpLightning on 08.05.2016.
 */
public class ImageRepositoryException extends Exception {

    public ImageRepositoryException() {
    }

    public ImageRepositoryException(String message) {
        super(message);
    }

    public ImageRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
