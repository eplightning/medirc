package org.eplight.medirc.client.data;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBufInputStream;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.ByteArrayInputStream;

/**
 * Created by eplightning on 09.05.16.
 */
public class SessionImage {

    private int id;
    private String name;
    private Image img;
    private Color color;

    public SessionImage(int id, String name) {
        this.id = id;
        this.name = name;
        this.color = Color.BLACK;
    }

    public SessionImage(int id, ByteString data, String name, int color) {
        this.id = id;
        this.name = name;
        this.img = new Image(data.newInput());

        int blue = color & 0xFF;
        int green = (color & 0xFF00) >> 8;
        int red = (color & 0xFF0000) >> 16;

        this.color = Color.rgb(red, green, blue, 1.0);
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
}
