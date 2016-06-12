package org.eplight.medirc.server.image.repo;

import org.eplight.medirc.server.image.Image;
import org.eplight.medirc.server.image.MemoryImage;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by EpLightning on 07.05.2016.
 */
public class NullImageRepository implements ImageRepository {

    private int lastId = 1;

    @Override
    public Image create(int session) {
        return new MemoryImage(session, lastId++);
    }

    @Override
    public Image findById(int id) {
        return null;
    }

    @Override
    public Image findById(int id, boolean includeHidden) {
        return null;
    }

    @Override
    public Set<Image> findSessionImages(int sess) {
        return new HashSet<>();
    }

    @Override
    public Set<Image> findSessionImages(int sess, boolean includeHidden) {
        return new HashSet<>();
    }

    @Override
    public void persist(Image img) {

    }

    @Override
    public void remove(Image img) {

    }
}
