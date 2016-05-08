package org.eplight.medirc.server.image.repo;

import org.eplight.medirc.server.image.Image;

import java.util.Set;

/**
 * Created by EpLightning on 07.05.2016.
 */
public interface ImageRepository {

    Image create(int session);

    Image findById(int id);
    Set<Image> findSessionImages(int sess);

    void persist(Image img) throws ImageRepositoryException;

    void remove(Image img);
}
