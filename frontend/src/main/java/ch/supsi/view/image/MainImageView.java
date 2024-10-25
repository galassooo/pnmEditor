package ch.supsi.view.image;

import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.model.image.IImageModel;
import ch.supsi.model.image.ImageModel;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class MainImageView implements IImageView{
    @FXML
    private ImageView image;

    private static final IImageModel model;

    static{
        model = ImageModel.getInstance();
    }

    @FXML
    void initialize() {
    }

    public void update() {
        ImageBusinessInterface i  = model.getImage();
        int width = i.getWidth();
        int height = i.getHeight();
        long[][] pixels = i.getPixels();

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
