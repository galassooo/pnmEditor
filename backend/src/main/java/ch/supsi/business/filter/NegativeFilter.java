package ch.supsi.business.filter;

import ch.supsi.application.Image.ImageBusinessInterface;

public class NegativeFilter implements FilterStrategy {
    @Override
    public void applyFilter(ImageBusinessInterface img) {
        long[][] pixels = img.getPixels();

        int maxValue = 0xFFFFFF; //solo canali no alfa

        for(int i = 0; i < pixels.length; i++){
            for(int j = 0; j < pixels[i].length; j++){
                pixels[i][j] ^= maxValue; //XOR
            }
        }
    }
}
