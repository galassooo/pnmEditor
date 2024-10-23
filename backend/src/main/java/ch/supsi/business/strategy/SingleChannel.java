package ch.supsi.business.strategy;

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
        long gray = pixel & 0xFF;
        return (long) ((gray / 255.0) * maxValue);
    }
}

