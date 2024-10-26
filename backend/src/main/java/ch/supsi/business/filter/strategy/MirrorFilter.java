package ch.supsi.business.filter.strategy;

import ch.supsi.application.image.ImageBusinessInterface;

public class MirrorFilter implements NamedFilterStrategy {
    @Override
    public void applyFilter(ImageBusinessInterface img) {
        long[][] pixels = img.getPixels();

        if (pixels == null || pixels.length == 0 || pixels[0].length == 0) {
            return;
        }

        int width = pixels[0].length;
        int height = pixels.length;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width / 2; j++) {
                long temp = pixels[i][j];
                pixels[i][j] = pixels[i][width - j - 1];
                pixels[i][width - j - 1] = temp;
            }
        }

        img.setPixels(pixels);
    }

    @Override
    public String getCode() {
        return "Mirror";
    }
}
