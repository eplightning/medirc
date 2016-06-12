package org.eplight.medirc.server.image.repo;

import org.eplight.medirc.server.image.Image;

import java.util.Set;

/**
 * Created by EpLightning on 07.05.2016.
 */
public interface ImageRepository {

    Image create(int session);

    Image findById(int id);
    Image findById(int id, boolean includeHidden);

    Set<Image> findSessionImages(int sess);
    Set<Image> findSessionImages(int sess, boolean includeHidden);

    void persist(Image img) throws ImageRepositoryException;

    void remove(Image img);
}
