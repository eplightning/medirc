package org.eplight.medirc.server.image.transformations;

import com.google.protobuf.Message;
import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.protocol.SessionBasic.ImageFragment.FragCase;

import java.util.ArrayList;
import java.util.List;

public class ImageTransformations {

    private double zoom;
    private List<ImageFragment> fragments;

    public ImageTransformations() {
        this.zoom = 1.0;
        this.fragments = new ArrayList<>();
    }

    public ImageTransformations(double zoom) {
        this.zoom = zoom;
        this.fragments = new ArrayList<>();
    }

    public ImageTransformations(SessionBasic.ImageTransformations msg) {
        this.zoom = msg.getZoom();
        this.fragments = new ArrayList<>();

        msg.getFragmentsList().forEach(f -> {
            switch (f.getFragCase()) {
                case RECT:
                    RectImageFragment f2 = new RectImageFragment();
                    f2.fromProtobuf(f.getRect());
                    this.fragments.add(f2);
                    break;
            }
        });
    }

    public SessionBasic.ImageTransformations toProtobuf() {
        SessionBasic.ImageTransformations.Builder b = SessionBasic.ImageTransformations.newBuilder();

        b.setZoom(zoom);

        fragments.forEach(f -> b.addFragments(f.toProtobuf()));

        return b.build();
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public List<ImageFragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<ImageFragment> fragments) {
        this.fragments = fragments;
    }
}
