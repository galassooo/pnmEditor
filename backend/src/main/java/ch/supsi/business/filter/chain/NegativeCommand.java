package ch.supsi.business.filter.chain;

import ch.supsi.application.image.ImageBusinessInterface;

public class NegativeCommand extends FilterChainLink {
    @Override
    public void executeFilter(ImageBusinessInterface img) {
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