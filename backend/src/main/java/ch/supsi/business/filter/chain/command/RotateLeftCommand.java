package ch.supsi.business.filter.chain.command;

import ch.supsi.application.image.WritableImage;

public class RotateLeftCommand implements FilterCommand {

    @Override
    public void execute(WritableImage image) {

            long[][] pixels = image.getPixels();
            if (pixels == null || pixels.length == 0 || pixels[0].length == 0) {
                return;
            }
            long[][] rotatedPixels = rotate(pixels);
            image.setPixels(rotatedPixels);
        }

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

    @Override
    public String getName() {
        return "Rotate_Left";
    }
}
