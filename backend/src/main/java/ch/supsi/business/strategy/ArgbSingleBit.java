package ch.supsi.business.strategy;

public class ArgbSingleBit implements ArgbConvertStrategy {

    private static final long ARGB_WHITE = 0xFFFFFFFFL; // Bianco opaco
    private static final long ARGB_BLACK = 0xFF000000L; // Nero opaco


    @Override
    public long toArgb(long pixel, int maxValue) {
        return pixel == 1 ? ARGB_BLACK : ARGB_WHITE;
    }
}



