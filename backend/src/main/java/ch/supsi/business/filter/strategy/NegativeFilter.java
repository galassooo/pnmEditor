package ch.supsi.business.filter.strategy;

import ch.supsi.application.image.ImageBusinessInterface;

public class NegativeFilter implements NamedFilterStrategy {
    @Override
    public void applyFilter(ImageBusinessInterface img) {
        long[][] pixels = img.getPixels();

        if (pixels == null || pixels.length == 0 || pixels[0].length == 0) {
            return;
        }

        int maxValue = 0xFFFFFF; //solo canali no alfa

        for(int i = 0; i < pixels.length; i++){
            for(int j = 0; j < pixels[i].length; j++){
                pixels[i][j] ^= maxValue; //XOR
            }
        }
    }

    @Override
    public String getCode() {
        return "Negative";
    }
}
