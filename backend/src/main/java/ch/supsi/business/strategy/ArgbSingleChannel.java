package ch.supsi.business.strategy;

public class ArgbSingleChannel implements ArgbConvertStrategy {

    @Override
    public int toArgb(int grayValue, int maxValue) {
        int normalizedGray = (int) ((grayValue / (double) maxValue) * 255);
        return (0xFF << 24) | (normalizedGray << 16) | (normalizedGray << 8) | normalizedGray;
    }
}

