package ch.supsi.business.image;

import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.strategy.ConvertStrategy;
import java.util.function.Function;

/*
Non implementa il pattern adapter, perchè non abbiamo un'incompatibilità tra i tipi
in quanto abbiamo un image come input e un image come output.
 */
public class ImageAdapter implements ImageAdapterInterface{
    private final ConvertStrategy strategy;

    public ImageAdapter(ConvertStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public long[][] rawToArgb(long[][] rawImage){
        var function = new Function<Long, Long>() {
            @Override
            public Long apply(Long aLong) {
                return strategy.toArgb(aLong);
            }
        };
        return process(rawImage, function);
    }

    @Override
    public long[][] argbToRaw(long[][] rawImage){
        var function = new Function<Long, Long>() {
            @Override
            public Long apply(Long aLong) {
                return strategy.ArgbToOriginal(aLong);
            }
        };
        return process(rawImage, function);
    }

    private long[][] process(long[][] pixels, Function<Long, Long> function){
        if(pixels == null)
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
}
