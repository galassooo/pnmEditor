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
        return pixel == ARGB_BLACK ? 1 : 0;
    }

}



