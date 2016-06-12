package org.eplight.medirc.server.image;

import org.eplight.medirc.protocol.SessionBasic;

public class ImageTransformations {

    private double zoom;
    private int focusX;
    private int focusY;

    public ImageTransformations() {
        this.zoom = 1.0;
        this.focusX = 0;
        this.focusY = 0;
    }

    public ImageTransformations(double zoom, int focusX, int focusY) {
        this.zoom = zoom;
        this.focusX = focusX;
        this.focusY = focusY;
    }

    public ImageTransformations(SessionBasic.ImageTransformations msg) {
        this.zoom = msg.getZoom();
        this.focusX = msg.getFocusX();
        this.focusY = msg.getFocusY();
    }

    public SessionBasic.ImageTransformations toProtobuf() {
        SessionBasic.ImageTransformations.Builder b = SessionBasic.ImageTransformations.newBuilder();

        b.setZoom(zoom);
        b.setFocusX(focusX);
        b.setFocusY(focusY);

        return b.build();
    }

    public double getZoom() {
        return zoom;
    }

    public int getFocusX() {
        return focusX;
    }

    public int getFocusY() {
        return focusY;
    }
}
