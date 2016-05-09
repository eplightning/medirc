package org.eplight.medirc.client.data;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBufInputStream;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;

/**
 * Created by eplightning on 09.05.16.
 */
public class SessionImage {

    private int id;
    private String name;
    private Image img;

    public SessionImage(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public SessionImage(int id, ByteString data, String name) {
        this.id = id;
        this.name = name;
        this.img = new Image(data.newInput());
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
}
