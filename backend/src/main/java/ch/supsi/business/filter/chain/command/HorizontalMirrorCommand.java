package ch.supsi.business.filter.chain.command;

import ch.supsi.application.image.WritableImage;

public class HorizontalMirrorCommand implements FilterCommand {

    @Override
    public void execute(WritableImage img) {
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
    public String getName() {
        return "Mirror_Horizontal";
    }
}