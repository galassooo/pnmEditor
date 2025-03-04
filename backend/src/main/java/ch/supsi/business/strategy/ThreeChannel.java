package ch.supsi.business.strategy;

import java.util.Objects;

public class ThreeChannel implements ConvertStrategy {

    private final int maxValue;

    public ThreeChannel(int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long toArgb(long pixel) {
        long red;
        long green;
        long blue;
        if (maxValue > 255) {
            //nel caso dei 16 bit per canale va normalizzato a 8 poichè
            //l'argb impone 8 bit per canale + alpha
            red = (pixel >> 32) & 0xFFFF; //extract 16 bit values
            green = (pixel >> 16) & 0xFFFF;
            blue = pixel & 0xFFFF;

        } else {
            red = (pixel >> 16) & 0xFF;
            green = (pixel >> 8) & 0xFF;
            blue = pixel & 0xFF;

        }
        // normalize to 8-bit range for ARGB
        red = (long) ((red / (double) maxValue) * 255);
        green = (long) ((green / (double) maxValue) * 255);
        blue = (long) ((blue / (double) maxValue) * 255);

        return (0xFFL << 24) | (red << 16) | (green << 8) | blue;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long ArgbToOriginal(long pixel) {
        long red = (pixel >> 16) & 0xFF;
        long green = (pixel >> 8) & 0xFF;
        long blue = pixel & 0xFF;

        if (maxValue > 255) {
            red = (long) ((red / 255.0) * maxValue);
            green = (long) ((green / 255.0) * maxValue);
            blue = (long) ((blue / 255.0) * maxValue);

            return red << 32 | green << 16 | blue;
        } else {
            return red << 16 | green << 8 | blue;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThreeChannel that)) return false;
        return maxValue == that.maxValue;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maxValue);
    }
}
