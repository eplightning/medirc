package org.eplight.medirc.server.image;

import java.io.IOException;

/**
 * Created by EpLightning on 07.05.2016.
 */
public interface Image {

    byte[] getData();
    String getName();
    int getSessionId();
    int getId();

    int getHeight();
    int getWidth();

    void setData(byte[] data, int width, int height);
    void importImage(byte[] input) throws IOException;
    void setName(String name);
}
