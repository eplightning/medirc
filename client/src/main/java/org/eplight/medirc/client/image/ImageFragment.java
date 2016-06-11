package org.eplight.medirc.client.image;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public interface ImageFragment {
    void paint(GraphicsContext gc, double zoom);
}
