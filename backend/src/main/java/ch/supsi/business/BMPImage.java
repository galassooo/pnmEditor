package ch.supsi.business;

public class BMPImage {
    private int width;
    private int height;
    private int[][] pixels;  // Matrice di pixel ARGB

    public BMPImage(int width, int height, int[][] pixels) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[][] getPixels() {
        return pixels;
    }
}
