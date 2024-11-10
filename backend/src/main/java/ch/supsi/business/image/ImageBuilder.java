package ch.supsi.business.image;

import ch.supsi.application.image.ImageBusinessInterface;

public class ImageBuilder {
    private long[][] pixels;
    private String filePath;
    private String magicNumber;
    private ImageAdapterInterface imageAdapter;

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

    public ImageBusinessInterface build() {
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
            long[][] adaptedPixels = imageAdapter.rawToArgb(pixels);
            return new ImageBusiness(adaptedPixels, filePath, magicNumber);
        }
        return new ImageBusiness(pixels, filePath, magicNumber);
    }
}