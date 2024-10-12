package ch.supsi.business.strategy;

public class ArgbSingleChannel implements ArgbConvertStrategy {

    @Override
    public long toArgb(long grayValue, int maxValue) {
        long normalizedGray = (long) ((grayValue / (double) maxValue) * 255);
        return (0xFFL << 24) | (normalizedGray << 16) | (normalizedGray << 8) | normalizedGray;
    }
}

