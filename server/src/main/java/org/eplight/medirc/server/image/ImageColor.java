package org.eplight.medirc.server.image;

public class ImageColor {

    private double r;
    private double g;
    private double b;

    public ImageColor(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ImageColor(int color) {
        int blue = color & 0xFF;
        int green = (color & 0xFF00) >> 8;
        int red = (color & 0xFF0000) >> 16;

        r = red / 255.0;
        g = green / 255.0;
        b = blue / 255.0;
    }

    public ImageColor() {

    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }
}
