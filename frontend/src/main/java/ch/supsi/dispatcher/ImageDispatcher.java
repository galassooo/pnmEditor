package ch.supsi.dispatcher;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.imaging.Imaging;

import java.awt.image.BufferedImage;
import java.net.URL;

public class ImageDispatcher {
    @FXML
    private ImageView image;

    @FXML
    void initialize() {
        //-------- PROVA CARICAMENTO IMMAGINE -------
        Image img = null;
        try {
            URL file = getClass().getResource("/images/TEST IMAGES - To be removed/prova.png"); //ppm, bmp, pmg

            assert file != null;
            img =  new Image(file.toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(img!= null){
            image.setImage(img);
        }
    }
}
