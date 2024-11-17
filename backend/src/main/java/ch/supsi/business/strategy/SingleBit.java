package ch.supsi.business.strategy;

public class SingleBit implements ConvertStrategy {

    private static final long ARGB_WHITE = 0xFFFFFFFFL; // Bianco opaco
    private static final long ARGB_BLACK = 0xFF000000L; // Nero opaco

    @Override
    public long toArgb(long pixel) {
        return pixel == 1 ? ARGB_BLACK : ARGB_WHITE;
    }

    @Override
    public long ArgbToOriginal(long pixel) {
        // Estrai i componenti
        int r = (int) ((pixel >> 16) & 0xFF);
        int g = (int) ((pixel >> 8) & 0xFF);
        int b = (int) (pixel & 0xFF);

        // Se è un'immagine a colori, converti prima in grayscale
        if (r != g || g != b) {
            double grayValue = 0.299 * r + 0.587 * g + 0.114 * b;
            // Usa la metà (127.5) come threshold
            return grayValue > 127.5 ? 0 : 1;
        } else {
            // È già in scala di grigi, usa direttamente la threshold
            return b > 127 ? 0 : 1;
        }
    }
}



