package org.eplight.medirc.client.components.session;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.eplight.medirc.client.image.ImageFragment;
import org.eplight.medirc.client.image.RectSelectPainter;
import org.eplight.medirc.client.image.SelectPainter;

import java.util.ArrayList;
import java.util.List;

public class ImageEditor extends Group {

    private Image image;
    private Canvas canvas;
    private Canvas canvasSelection;
    private Color defaultColor;
    private List<ZoomEventHandler> zoomHandlers;
    private List<SelectionEventHandler> selectionHandlers;

    private IntegerProperty width;
    private IntegerProperty height;
    private DoubleProperty zoom;
    private DoubleProperty maxZoom;
    private ObjectProperty<Point2D> selectStart;
    private ObjectProperty<Point2D> selectCurrent;
    private BooleanProperty selectInProgress;
    private BooleanProperty selectPaint;
    private BooleanProperty editable;
    private ObjectProperty<SelectPainter> selectPainter;
    private ObservableList<ImageFragment> fragmentList;

    public ImageEditor() {
        width = new SimpleIntegerProperty();
        height = new SimpleIntegerProperty();
        zoom = new SimpleDoubleProperty(1.0);
        maxZoom = new SimpleDoubleProperty(2.0);
        selectStart = new SimpleObjectProperty<>(new Point2D(0, 0));
        selectCurrent = new SimpleObjectProperty<>(new Point2D(0, 0));
        selectInProgress = new SimpleBooleanProperty(false);
        selectPaint = new SimpleBooleanProperty(true);
        selectPainter = new SimpleObjectProperty<>(new RectSelectPainter());
        fragmentList = FXCollections.observableArrayList();
        editable = new SimpleBooleanProperty(false);

        canvas = new Canvas();
        canvasSelection = new Canvas();
        defaultColor = Color.WHITE;
        zoomHandlers = new ArrayList<>();
        selectionHandlers = new ArrayList<>();

        canvas.widthProperty().bind(width);
        canvas.heightProperty().bind(height);
        canvasSelection.widthProperty().bind(width);
        canvasSelection.heightProperty().bind(height);

        getChildren().addAll(canvas, canvasSelection);

        canvasSelection.toFront();

        selectCurrent.addListener((observable, oldValue, newValue) -> {
            repaintSelect();
        });

        selectPaint.addListener((observable, oldValue, newValue) -> {
            repaintSelect();
        });

        selectPainter.addListener((observable, oldValue, newValue) -> {
            repaintSelect();
        });

        fragmentList.addListener((ListChangeListener<ImageFragment>) c -> {
            repaintCanvas();
        });

        canvasSelection.setOnScroll(event -> {
            double delta = event.getDeltaX() == 0 ? event.getDeltaY() : event.getDeltaX();

            if (event.isShiftDown() && editable.get()) {
                int direction = delta > 0 ? -1 : 1;
                double power = Math.abs(delta);

                if (power > 1.0)
                    power = Math.sqrt(power);

                power /= 20.0;

                adjustZoomUser(direction * Math.min(0.03, power));
                event.consume();
            }
        });

        canvasSelection.setOnMousePressed(event -> {
            if (!editable.get())
                return;

            if (event.isSecondaryButtonDown())
                return;

            if (selectInProgress.get())
                return;

            selectStart.setValue(new Point2D(event.getX(), event.getY()));
            selectCurrent.setValue(new Point2D(event.getX(), event.getY()));
            selectInProgress.setValue(true);

            event.consume();
        });

        canvasSelection.setOnMouseDragged(event -> {
            if (!editable.get())
                return;

            if (event.isSecondaryButtonDown())
                return;

            if (selectInProgress.get()) {
                selectCurrent.setValue(new Point2D(event.getX(), event.getY()));
                event.consume();
            }
        });

        canvasSelection.setOnMouseReleased(event -> {
            if (!editable.get())
                return;

            if (event.isSecondaryButtonDown())
                return;

            if (selectInProgress.get()) {
                selectionHandlers.forEach(a -> a.handle(selectStart.get(), selectCurrent.get(), zoom.get(), defaultColor));
                selectInProgress.setValue(false);
                event.consume();
            }
        });
    }

    public void setEditable(boolean editable) {
        this.editable.setValue(editable);
    }

    public int getWidth() {
        return width.get();
    }

    public int getHeight() {
        return height.get();
    }

    public void setImage(Image img, Color color) {
        image = img;
        defaultColor = color.invert();

        width.set((int) Math.round(img.getWidth()));
        height.set((int) Math.round(img.getHeight()));
        zoom.set(1.0);
        maxZoom.set(Math.max(1.0, 4096.0 / Math.max(img.getWidth(), img.getHeight())));
        selectStart.setValue(new Point2D(0, 0));
        selectCurrent.setValue(new Point2D(0, 0));
        selectInProgress.setValue(false);

        repaintCanvas();
    }

    public void setDefaultColor(Color c) {
        defaultColor = c;
        repaintSelect();
    }

    public void changeZoom(double val) {
        adjustZoom(val - zoom.get());
    }

    public void adjustZoom(double val) {
        double z = zoom.get();

        z += val;

        if (z < 0.1) {
            z = 0.1;
        } else if (z > maxZoom.get()) {
            z = maxZoom.get();
        }

        double difference = z / zoom.get();

        zoom.set(z);

        width.set((int) Math.round(image.getWidth() * z));
        height.set((int) Math.round(image.getHeight() * z));

        selectStart.setValue(selectStart.get().multiply(difference));
        selectCurrent.setValue(selectCurrent.get().multiply(difference));

        repaintCanvas();
    }

    public void addSelectionHandler(SelectionEventHandler h) {
        selectionHandlers.add(h);
    }

    public void addZoomHandler(ZoomEventHandler h) {
        zoomHandlers.add(h);
    }

    public void enableSelectionPaint(boolean val) {
        selectPaint.setValue(val);
    }

    public void setSelectionPainter(SelectPainter painter) {
        selectPainter.setValue(painter);
    }

    public List<ImageFragment> getFragments() {
        return fragmentList;
    }

    public void clearSelection() {
        selectStart.setValue(new Point2D(0, 0));
        selectInProgress.setValue(false);
        selectCurrent.setValue(new Point2D(0 ,0));
    }

    private void adjustZoomUser(double val) {
        adjustZoom(val);
        zoomHandlers.forEach(a -> a.handle(zoom.get()));
    }

    private void repaintSelect() {
        GraphicsContext gc = canvasSelection.getGraphicsContext2D();

        gc.clearRect(0, 0, canvasSelection.getWidth(), canvasSelection.getHeight());

        if (!selectPaint.get())
            return;

        Point2D from = selectStart.get();
        Point2D to = selectCurrent.get();

        if (from.equals(to))
            return;

        gc.save();
        selectPainter.get().paint(gc, from, to, defaultColor);
        gc.restore();
    }

    private void repaintCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(image, 0, 0, width.get(), height.get());

        for (ImageFragment frag : fragmentList) {
            gc.save();
            frag.paint(gc, zoom.get());
            gc.restore();
        }
    }

    public interface SelectionEventHandler {
        void handle(Point2D start, Point2D end, double zoom, Color defaultColor);
    }

    public interface ZoomEventHandler {
        void handle(double zoom);
    }
}
