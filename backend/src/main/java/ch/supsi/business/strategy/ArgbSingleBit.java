package ch.supsi.business.strategy;

public class ArgbSingleBit implements ArgbConvertStrategy {

    private static final int ARGB_WHITE = 0xFFFFFFFF; // Bianco opaco
    private static final int ARGB_BLACK = 0xFF000000; // Nero opaco


    @Override
    public int toArgb(int pixel, int maxValue) {
        return pixel == 1 ? ARGB_BLACK : ARGB_WHITE;
    }
}



