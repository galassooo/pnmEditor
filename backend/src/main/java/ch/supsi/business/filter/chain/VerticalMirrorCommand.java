package ch.supsi.business.filter.chain;

import ch.supsi.application.image.ImageBusinessInterface;

public class VerticalMirrorCommand extends FilterChainLink {
    @Override
    public void executeFilter(ImageBusinessInterface img) {
        long[][] pixels = img.getPixels();

        // Controllo validità dell'array di pixel
        if (pixels == null || pixels.length == 0 || pixels[0].length == 0) {
            return;
        }

        int width = pixels[0].length;
        int height = pixels.length;

        // Scambia i pixel verticalmente
        // Itera solo fino a metà altezza per evitare di scambiare due volte
        for (int i = 0; i < height / 2; i++) {
            for (int j = 0; j < width; j++) {
                // Scambia il pixel nella riga superiore con quello nella riga inferiore corrispondente
                long temp = pixels[i][j];
                pixels[i][j] = pixels[height - i - 1][j];
                pixels[height - i - 1][j] = temp;
            }
        }

        img.setPixels(pixels);
    }

    @Override
    public String getName() {
        return "Mirror_Vertical";
    }
}