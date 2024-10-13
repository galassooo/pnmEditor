package ch.supsi.view.image;

import ch.supsi.application.Image.ImageBusinessInterface;
import ch.supsi.business.Image.ImageBusiness;
import ch.supsi.business.Image.ImageDataAccess;
import ch.supsi.dataaccess.PGMDataAccess;
import ch.supsi.dataaccess.PPMDataAccess;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.io.IOException;

public class MainImageView {
    @FXML
    private ImageView image;

    @FXML
    void initialize() throws IOException {


        //--------- PROVA CARICAMENTO ----- PASSA DAL MODEL E ELIMINA STA SCHIFEZZA :)
        ImageBusinessInterface bmpImage = PPMDataAccess.getInstance().read("/Users/marti/Desktop/16bit.byte.ppm");
        int width = bmpImage.getWidth();
        int height = bmpImage.getHeight();
        long[][] pixels = bmpImage.getPixels();

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                long argb = pixels[y][x];
                pixelWriter.setArgb(x, y, (int) argb);
            }
        }

        image.setImage(writableImage);

        PPMDataAccess.getInstance().write(bmpImage, "/Users/marti/Desktop/prova.ppm");
    }
}
