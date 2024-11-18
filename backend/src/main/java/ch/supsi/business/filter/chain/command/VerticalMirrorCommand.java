package ch.supsi.business.filter.chain.command;

import ch.supsi.application.image.WritableImage;

public class VerticalMirrorCommand implements FilterCommand {

    /**
     * {@inheritDoc}
     * @param img the {@link WritableImage} to process
     */
    @Override
    public void execute(WritableImage img) {
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

    /**
     * {@inheritDoc}
     * @return name
     */
    @Override
    public String getName() {
        return "Mirror_Vertical";
    }
}