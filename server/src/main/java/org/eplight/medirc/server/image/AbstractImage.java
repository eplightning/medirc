package org.eplight.medirc.server.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by EpLightning on 07.05.2016.
 */
abstract public class AbstractImage implements Image {

    protected int height;
    protected int width;
    protected String name;
    protected byte[] data;
    protected int sessionId;
    protected int id;

    public AbstractImage(int sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void importImage(byte[] input) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(input));

        if (img == null)
            throw new IOException("Cannot read image");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        ImageIO.write(img, "jpg", stream);

        setData(stream.toByteArray(), img.getWidth(), img.getHeight());
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getSessionId() {
        return sessionId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o instanceof Image) return false;

        Image that = (Image) o;

        return id == that.getId();
    }

    @Override
    public int hashCode() {
        return id;
    }
}
