package org.eplight.medirc.server.image;

import org.eplight.medirc.server.image.fragments.ImageFragment;
import org.eplight.medirc.server.user.User;

/**
 * Created by EpLightning on 07.05.2016.
 */
public class MemoryImage extends AbstractImage {

    public MemoryImage(int sessionId, int id) {
        super(sessionId);

        this.id = id;
    }

    @Override
    public void setData(byte[] data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setColor(ImageColor color) {
        this.color = color;
    }

    @Override
    public void setTransformations(ImageTransformations transformations) {
        this.transformations = transformations;
    }

    @Override
    public void addFragment(ImageFragment frag) {
        this.imageFragments.add(frag);
    }

    @Override
    public void clearFragments(User user) {
        if (user != null) {
            this.imageFragments.removeIf(f -> f.getUser().equals(user));
        } else {
            this.imageFragments.clear();
        }
    }
}
