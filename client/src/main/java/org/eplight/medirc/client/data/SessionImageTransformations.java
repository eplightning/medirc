package org.eplight.medirc.client.data;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.eplight.medirc.client.image.ImageFragment;
import org.eplight.medirc.client.image.RectImageFragment;
import org.eplight.medirc.protocol.SessionBasic;

import java.util.ArrayList;
import java.util.List;

public class SessionImageTransformations {

    private double zoom;

    private List<ImageFragment> fragments;

    public SessionImageTransformations(SessionBasic.ImageTransformations msg) {
        this.zoom = msg.getZoom();
        this.fragments = new ArrayList<>();

        msg.getFragmentsList().forEach(f -> {
            switch (f.getFragCase()) {
                case RECT:
                    SessionBasic.RectFragment frag = f.getRect();

                    Color color = Color.color(frag.getColorR(), frag.getColorG(), frag.getColorB());

                    this.fragments.add(new RectImageFragment(new Point2D(frag.getX1(), frag.getY1()),
                            new Point2D(frag.getX2(), frag.getY2()), frag.getZoom(), color));

                    break;
            }
        });
    }

    public double getZoom() {
        return zoom;
    }

    public List<ImageFragment> getFragments() {
        return fragments;
    }
}
