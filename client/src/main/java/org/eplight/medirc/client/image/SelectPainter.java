package org.eplight.medirc.client.image;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public interface SelectPainter {
    void paint(GraphicsContext gc, Point2D start, Point2D end, Paint defaultPaint);
}
