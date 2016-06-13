package org.eplight.medirc.client.data;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBufInputStream;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.eplight.medirc.client.image.ImageFragment;
import org.eplight.medirc.client.image.RectImageFragment;
import org.eplight.medirc.protocol.SessionBasic;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eplightning on 09.05.16.
 */
public class SessionImage {

    private int id;
    private String name;
    private Image img;
    private Color color;
    private double zoom;
    private int focusX;
    private int focusY;
    private List<ImageFragment> fragmentList;

    public SessionImage(SessionImage other) {
        this.id = other.id;
        this.name = other.name;
        this.img = other.img;
        this.color = other.color;
        this.zoom = other.zoom;
        this.focusX = other.focusX;
        this.focusY = other.focusY;
        this.fragmentList = other.getFragments();
    }

    public SessionImage(int id, String name) {
        this.id = id;
        this.name = name;
        this.color = Color.BLACK;
        this.fragmentList = new ArrayList<>();
    }

    public SessionImage(int id, ByteString data, String name, Color color, SessionBasic.ImageTransformations t,
                        List<ImageFragment> list) {
        this.id = id;
        this.name = name;
        this.img = new Image(data.newInput());

        this.color = color;
        this.zoom = t.getZoom();
        this.focusX = t.getFocusX();
        this.focusY = t.getFocusY();
        this.fragmentList = list;
    }

    public SessionImage(int id, byte[] data, String name, Color color) {
        this.id = id;
        this.name = name;
        this.img = new Image(new ByteArrayInputStream(data));

        this.color = color;
        this.zoom = 1.0;
        this.focusX = 0;
        this.focusY = 0;
        this.fragmentList = new ArrayList<>();
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

    public List<ImageFragment> getFragments() {
        return fragmentList;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public void setFragments(List<ImageFragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    public int getFocusX() {
        return focusX;
    }

    public void setFocusX(int focusX) {
        this.focusX = focusX;
    }

    public int getFocusY() {
        return focusY;
    }

    public void setFocusY(int focusY) {
        this.focusY = focusY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionImage that = (SessionImage) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
