package org.supsi.view.image;

import org.supsi.model.image.IImageModel;
import org.supsi.model.image.ImageModel;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Represents the main view for displaying the current image in the application.
 * Updates the view based on changes in the image model by rendering the image pixel data.
 */
public class MainImageView implements IImageView {
    @FXML
    private ImageView image;

    private static final IImageModel model;

    static{
        model = ImageModel.getInstance();
    }

    /**
     * Updates the image view to reflect changes in the image model.
     * Converts the raw pixel data from the model into a {@link WritableImage} and displays it.
     */
    @Override
    public void update() {
        long[][] pixels = model.getImagePixels();
        int width = pixels[0].length;
        int height = pixels.length;


        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                long argb = pixels[y][x];
                pixelWriter.setArgb(x, y, (int) argb);
            }
        }

        image.setImage(writableImage);
    }
}
