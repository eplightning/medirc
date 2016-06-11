package org.eplight.medirc.server.image;

import org.eplight.medirc.server.image.fragments.ImageFragment;

import java.io.IOException;
import java.util.List;

/**
 * Created by EpLightning on 07.05.2016.
 */
public interface Image {

    byte[] getData();
    String getName();
    int getSessionId();
    int getId();
    ImageColor getColor();
    ImageTransformations getTransformations();
    List<ImageFragment> getFragments();

    int getHeight();
    int getWidth();

    void setData(byte[] data, int width, int height);
    void importImage(byte[] input) throws IOException;
    void setName(String name);
    void setColor(ImageColor color);
    void setTransformations(ImageTransformations transformations);

    void updateFragments();
}
