package ch.supsi.business.filter.chain.command;

import ch.supsi.application.image.WritableImage;

public class RotateRightCommand implements FilterCommand {

    @Override
    public void execute(WritableImage img) {
        long[][] pixels = img.getPixels();
        if (pixels == null || pixels.length == 0 || pixels[0].length == 0) {
            return;
        }
        long[][] rotatedPixels = rotate(pixels);
        img.setPixels(rotatedPixels);
    }

    @Override
    public String getName() {
        return "Rotate_Right";
    }

    private long[][] rotate(long[][] pixels) {

        int width = pixels.length;
        int height = pixels[0].length;
        long[][] rotated = new long[height][width];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rotated[j][width - 1 - i] = pixels[i][j]; // Rotazione a destra

            }
        }

        return rotated;
    }
}
