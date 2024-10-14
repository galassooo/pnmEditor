package ch.supsi.business.filter;

import ch.supsi.application.Image.ImageBusinessInterface;

public class Rotate90 implements FilterStrategy {
    private final boolean right;

    public Rotate90(boolean right) {
        this.right = right;
    }

    @Override
    public void applyFilter(ImageBusinessInterface img) {
        if (img == null) {
            throw new IllegalArgumentException("Cannot rotate a null image");
        }
        long[][] pixels = img.getPixels();
        long[][] rotatedPixels = rotate(pixels, right);
        img.setPixels(rotatedPixels);
    }

    private long[][] rotate(long[][] pixels, boolean isRight) {
        if (pixels == null) {
            throw new IllegalArgumentException("Cannot rotate an empty image");
        }

        if(pixels.length == 0){
            return new long[0][0];
        }

        int width = pixels.length;
        int height = pixels[0].length;
        long[][] rotated = new long[height][width];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (isRight) {
                    rotated[j][width - 1 - i] = pixels[i][j]; // Rotazione a destra
                } else {
                    rotated[height - 1 - j][i] = pixels[i][j]; // Rotazione a sinistra
                }
            }
        }

        return rotated;
    }
}
