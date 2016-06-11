package org.eplight.medirc.server.image.fragments;

import com.sun.media.sound.InvalidFormatException;
import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.server.image.ImageColor;
import org.eplight.medirc.server.user.User;

public class RectImageFragment implements ImageFragment {

    private int id;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private ImageColor color;
    private double zoom;
    private User user;

    public RectImageFragment(int id, User user) {
        this.id = id;
        this.color = new ImageColor();
        this.user = user;
    }

    public void fromProtobuf(SessionBasic.RectFragment frag) throws InvalidFormatException {
        if (frag.getColorB() > 1.0 || frag.getColorG() > 1.0 || frag.getColorG() > 1.0 || frag.getColorG() < 0.0 ||
                frag.getColorB() < 0.0 || frag.getColorR() < 0.0) {
            throw new InvalidFormatException("Invalid fragment color");
        }

        if (frag.getZoom() < 0.1) {
            throw new InvalidFormatException("Invalid fragment zoom");
        }

        setX1(frag.getX1());
        setX2(frag.getX2());
        setY1(frag.getY1());
        setY2(frag.getY2());
        setColor(new ImageColor(frag.getColorR(), frag.getColorG(), frag.getColorB()));
        setZoom(frag.getZoom());
    }

    @Override
    public SessionBasic.ImageFragment toProtobuf() {
        SessionBasic.RectFragment b = SessionBasic.RectFragment.newBuilder()
                .setColorB(color.getB())
                .setColorG(color.getG())
                .setColorR(color.getR())
                .setX1(x1)
                .setX2(x2)
                .setY1(y1)
                .setY2(y2)
                .setZoom(zoom)
                .build();

        return SessionBasic.ImageFragment.newBuilder().setRect(b).build();
    }

    @Override
    public int getId() {
        return id;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public ImageColor getColor() {
        return color;
    }

    public void setColor(ImageColor color) {
        this.color = color;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    @Override
    public User getUser() {
        return user;
    }
}
