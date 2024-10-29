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
    public ImageBusinessInterface rawToArgb(ImageBusinessInterface rawImage){
        var function = new Function<Long, Long>() {
            @Override
            public Long apply(Long aLong) {
                return strategy.toArgb(aLong);
            }
        };
        return process(rawImage, function);
    }

    @Override
    public ImageBusinessInterface argbToRaw(ImageBusinessInterface rawImage){
        var function = new Function<Long, Long>() {
            @Override
            public Long apply(Long aLong) {
                return strategy.ArgbToOriginal(aLong);
            }
        };
        return process(rawImage, function);
    }

    private ImageBusinessInterface process(ImageBusinessInterface rawImage, Function<Long, Long> function){
        if(rawImage.getPixels() == null)
            return rawImage;
        int height = rawImage.getPixels().length;
        if (height == 0) {
            return rawImage;
        }
        int width = rawImage.getPixels()[0].length;

        long[][] argbMatrix = new long[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                argbMatrix[y][x] = function.apply(rawImage.getPixels()[y][x]);
            }
        }
        rawImage.setPixels(argbMatrix);
        return rawImage;
    }
}
