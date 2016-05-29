package org.eplight.medirc.client.image;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class RectSelectPainter implements SelectPainter {

    @Override
    public void paint(GraphicsContext gc, Point2D start, Point2D end, Paint defaultPaint) {
        gc.setFill(defaultPaint);

        double x = start.getX() > end.getX() ? end.getX() : start.getX();
        double y = start.getY() > end.getY() ? end.getY() : start.getY();
        Point2D distance = start.subtract(end);

        gc.fillRect(x, y, Math.abs(distance.getX()), Math.abs(distance.getY()));

        if (defaultPaint instanceof Color) {
            Color c = (Color) defaultPaint;

            gc.setLineWidth(1);
            gc.setStroke(Color.color(c.getRed(), c.getGreen(), c.getBlue(), 1.0));

            gc.strokeRect(x, y, Math.abs(distance.getX()), Math.abs(distance.getY()));
        }
    }
}
