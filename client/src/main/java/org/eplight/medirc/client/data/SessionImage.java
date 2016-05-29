package org.eplight.medirc.client.data;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBufInputStream;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.eplight.medirc.protocol.SessionBasic;

import java.io.ByteArrayInputStream;

/**
 * Created by eplightning on 09.05.16.
 */
public class SessionImage {

    private int id;
    private String name;
    private Image img;
    private Color color;
    private SessionImageTransformations transformations;

    public SessionImage(int id, String name) {
        this.id = id;
        this.name = name;
        this.color = Color.BLACK;
    }

    public SessionImage(int id, ByteString data, String name, Color color, SessionBasic.ImageTransformations t) {
        this.id = id;
        this.name = name;
        this.img = new Image(data.newInput());

        this.color = color;
        this.transformations = new SessionImageTransformations(t);
    }

    public int getId() {
        return id;
    }

    public Image getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public SessionImageTransformations getTransformations() {
        return transformations;
    }

    public void setTransformations(SessionImageTransformations transformations) {
        this.transformations = transformations;
    }
}
