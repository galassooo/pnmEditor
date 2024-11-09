package ch.supsi.business.filter.command;

import ch.supsi.application.image.ImageBusinessInterface;

public class NegativeCommand implements FilterCommand {
    @Override
    public void execute(ImageBusinessInterface img) {
        long[][] pixels = img.getPixels();

        if (pixels == null || pixels.length == 0 || pixels[0].length == 0) {
            return;
        }

        int maxValue = 0xFFFFFF;

        for(int i = 0; i < pixels.length; i++){
            for(int j = 0; j < pixels[i].length; j++){
                pixels[i][j] ^= maxValue;
            }
        }

        img.setPixels(pixels);
    }

    @Override
    public String getName() {
        return "Negative";
    }
}