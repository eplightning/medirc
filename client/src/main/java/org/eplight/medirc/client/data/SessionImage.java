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
    private List<ImageFragment> fragmentList;

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
        this.fragmentList = list;
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
