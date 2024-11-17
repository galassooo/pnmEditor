package ch.supsi.business.strategy;

import java.util.Objects;

public class SingleChannel implements ConvertStrategy {

    private final int maxValue;

    public SingleChannel(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public long toArgb(long grayValue) {
        long normalizedGray = (long) ((grayValue / (double) maxValue) * 255);
        return (0xFFL << 24) | (normalizedGray << 16) | (normalizedGray << 8) | normalizedGray;
    }

    @Override
    public long ArgbToOriginal(long pixel) {
        int r = (int) ((pixel >> 16) & 0xFF);
        int g = (int) ((pixel >> 8) & 0xFF);
        int b = (int) (pixel & 0xFF);

        //è un idea geniale, non ho bisogno di componenti extra, e
        // l'informazione sul numero di canali originali è intrinseca nei dati,
        // quindi non mi servono flag, classi di supporto e bla bla bla
        // Single responsibility -> la stessa componente si occupa di gestire
        // le conversioni dei pixel
        // cioè a noi non interessa che il formato sia a 1, 2, 3, 361 mila canali,
        // questo metodo prende un immagine generica ARGB e la converte in single channel
        // scegliendo la strategia migliore in base al numero di canali
        // cioè è un'idea brillante ommioddioooooo

        // se i canali sono diversi, applica i pesi (sto esportando da piu canali a meno)
        if (r != g || g != b) {
            // È un pixel a colori, applica i pesi per la conversione
            return (long) (((0.299 * r + 0.587 * g + 0.114 * b) / 255.0) * maxValue);
        } else {
            // È già in scala di grigi, usa il valore diretto
            return (long) ((b / 255.0) * maxValue);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SingleChannel that)) return false;
        return maxValue == that.maxValue;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maxValue);
    }
}

