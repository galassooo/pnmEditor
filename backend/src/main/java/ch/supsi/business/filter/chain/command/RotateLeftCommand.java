package ch.supsi.business.filter.chain.command;

import ch.supsi.application.image.WritableImage;

public class RotateLeftCommand implements FilterCommand {

    /**
     * {@inheritDoc}
     * @param image the {@link WritableImage} to process
     */
    @Override
    public void execute(WritableImage image) {

        long[][] pixels = image.getPixels();
        if (pixels == null || pixels.length == 0 || pixels[0].length == 0) {
            return;
        }
        long[][] rotatedPixels = rotate(pixels);
        image.setPixels(rotatedPixels);
    }

    /**
     * {@inheritDoc}
     * @return name
     */
    @Override
    public String getName() {
        return "Rotate_Left";
    }

    /**
     * apply rotation function on the given pixels
     * @param pixels a 2D array of {@link Long} containing pixels
     * @return another array of the rotated pixels
     */
    private long[][] rotate(long[][] pixels) {

        int width = pixels.length;
        int height = pixels[0].length;
        long[][] rotated = new long[height][width];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rotated[height - 1 - j][i] = pixels[i][j]; // Rotazione a sinistra
            }
        }
        return rotated;
    }
}
