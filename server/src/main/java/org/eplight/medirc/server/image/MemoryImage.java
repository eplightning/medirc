package org.eplight.medirc.server.image;

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
    public void setColor(int color) {
        this.color = color;
    }
}
