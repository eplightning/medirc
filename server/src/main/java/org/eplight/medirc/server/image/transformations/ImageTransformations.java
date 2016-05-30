package org.eplight.medirc.server.image.transformations;

import com.google.protobuf.Message;
import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.protocol.SessionBasic.ImageFragment.FragCase;

import java.util.ArrayList;
import java.util.List;

public class ImageTransformations {

    private double zoom;

    public ImageTransformations() {
        this.zoom = 1.0;
    }

    public ImageTransformations(double zoom) {
        this.zoom = zoom;
    }

    public ImageTransformations(SessionBasic.ImageTransformations msg) {
        this.zoom = msg.getZoom();
    }

    public SessionBasic.ImageTransformations toProtobuf() {
        SessionBasic.ImageTransformations.Builder b = SessionBasic.ImageTransformations.newBuilder();

        b.setZoom(zoom);

        return b.build();
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }
}
