package ch.supsi.business.image;

import java.util.Arrays;
import java.util.Objects;

public class ImageBuilder implements ImageBuilderInterface{
    private long[][] pixels;
    private String filePath;
    private String magicNumber;
    private ImageAdapterInterface imageAdapter;

    @Override
    public long[][] getPixels() {
        return pixels;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public String getMagicNumber() {
        return magicNumber;
    }


    public ImageBuilder withPixels(long[][] pixels) {
        this.pixels = pixels;
        return this;
    }

    public ImageBuilder withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public ImageBuilder withMagicNumber(String magicNumber) {
        this.magicNumber = magicNumber;
        return this;
    }

    public ImageBuilder withImageAdapter(ImageAdapterInterface imageAdapter) {
        this.imageAdapter = imageAdapter;
        return this;
    }

    public ImageBuilder build() {
        // Validazione
        if (pixels == null) {
            pixels = new long[0][0];
        }
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalStateException("FilePath is required");
        }
        if (magicNumber == null || magicNumber.isEmpty()) {
            throw new IllegalStateException("MagicNumber is required");
        }

        if(imageAdapter!= null){
            pixels = imageAdapter.rawToArgb(pixels);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageBuilder that)) return false;
        return Objects.deepEquals(getPixels(), that.getPixels()) && Objects.equals(getFilePath(), that.getFilePath()) && Objects.equals(getMagicNumber(), that.getMagicNumber()) && Objects.equals(imageAdapter, that.imageAdapter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(getPixels()), getFilePath(), getMagicNumber(), imageAdapter);
    }
}