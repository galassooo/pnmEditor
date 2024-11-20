package ch.supsi.business.image;

import ch.supsi.business.strategy.ConvertStrategy;

import java.util.Objects;
import java.util.function.Function;

/*
Non implementa il pattern adapter, perchè non abbiamo un'incompatibilità tra i tipi
in quanto abbiamo un long[][] come input e un long[][] come output.
 */
public class ImageAdapter implements ImageAdapterInterface {

    private final ConvertStrategy strategy;

    public ImageAdapter(ConvertStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public long[][] rawToArgb(long[][] rawImage) {
        var function = new Function<Long, Long>() {
            @Override
            public Long apply(Long aLong) {
                return strategy.toArgb(aLong);
            }
        };
        return process(rawImage, function);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public long[][] argbToRaw(long[][] rawImage) {
        var function = new Function<Long, Long>() {
            @Override
            public Long apply(Long aLong) {
                return strategy.ArgbToOriginal(aLong);
            }
        };
        return process(rawImage, function);
    }

    /**
     * apply the conversion function to all pixels
     * @param pixels 2D array of long representing an image
     * @param function conversion function to be applied
     * @return a new array with converted pixels
     */
    private long[][] process(long[][] pixels, Function<Long, Long> function) {
        if (pixels == null)
            return new long[0][];
        int height = pixels.length;
        if (height == 0) {
            return new long[0][];
        }
        int width = pixels[0].length;

        long[][] argbMatrix = new long[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                argbMatrix[y][x] = function.apply(pixels[y][x]);
            }
        }
        return argbMatrix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageAdapter adapter)) return false;
        return Objects.equals(strategy, adapter.strategy);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(strategy);
    }
}
