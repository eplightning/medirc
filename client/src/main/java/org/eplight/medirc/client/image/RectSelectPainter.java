package org.eplight.medirc.client.image;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class RectSelectPainter implements SelectPainter {

    @Override
    public void paint(GraphicsContext gc, Point2D start, Point2D end, Paint defaultPaint) {
        gc.setStroke(defaultPaint);
        gc.setLineWidth(1);

        double x = start.getX() > end.getX() ? end.getX() : start.getX();
        double y = start.getY() > end.getY() ? end.getY() : start.getY();
        Point2D distance = start.subtract(end);

        if (defaultPaint instanceof Color) {
            Color c = (Color) defaultPaint;

            gc.setFill(Color.color(c.getRed(), c.getGreen(), c.getBlue(), 0.1));
            gc.fillRect(x, y, Math.abs(distance.getX()), Math.abs(distance.getY()));
        }

        gc.strokeRect(x, y, Math.abs(distance.getX()), Math.abs(distance.getY()));
    }
}
