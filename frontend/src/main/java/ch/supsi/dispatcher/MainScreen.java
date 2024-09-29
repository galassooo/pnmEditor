package ch.supsi.dispatcher;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.imaging.Imaging;

import java.awt.image.BufferedImage;
import java.net.URL;

//TODO va creato un dispatcher e separati i metodi,
// è fatto per vedere se funziona tutto come deve
// prima di trovarmi a sistemare codice da 73819376436 parti diverse
public class MainScreen {
    @FXML
    private Label moduleOrder;

    @FXML
    private Label v3;

    @FXML
    private ImageView image;

    @FXML
    private ImageView lente;


    @FXML
    private Button button1;

    @FXML
    private Button button2;

    @FXML
    private Button button3;

    @FXML
    private Button button4;

    @FXML
    private Button button5;

    @FXML
    private Button button6;

    @FXML
    private Button button7;

    @FXML
    private Button button8;

    @FXML
    private Button bottomButton1;

    @FXML
    private Button bottomButton2;

    @FXML
    private ScrollPane scPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField searchBar;

    @FXML
    private Menu menu1;

    @FXML
    private void initialize() {
        button1.setOnAction(event -> {
            System.out.print("Bella");
        });


        searchBar.setPromptText("search modules by name or tag");


        URL imageUrl = getClass().getResource("/lente.png");
        if (imageUrl == null) {
            System.err.println("Error while loading lente image");
            return;
        }
        Image i = new Image(imageUrl.toExternalForm());
        lente.setImage(i);

        button1.setGraphic(loadButtonImages("/b1.png", 25));
        button2.setGraphic(loadButtonImages("/b2.png", 25));
        button3.setGraphic(loadButtonImages("/b3.png", 25));
        button4.setGraphic(loadButtonImages("/b4.png", 25));
        button5.setGraphic(loadButtonImages("/b5.png", 25));
        button6.setGraphic(loadButtonImages("/b6.png", 25));
        button7.setGraphic(loadButtonImages("/b7.png", 25));
        button8.setGraphic(loadButtonImages("/b8.png", 25));

        bottomButton1.setGraphic(loadButtonImages("/bb1.png", 20));
        bottomButton2.setGraphic(loadButtonImages("/bb2.png", 20));


        //-------- PROVA CARICAMENTO IMMAGINE -------
        BufferedImage img = null;
        try {
            URL file = getClass().getResource("/image.ppm");

            img =  Imaging.getBufferedImage(file.openStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(img!= null){

            Image i1 = SwingFXUtils.toFXImage(img, null);
            image.setImage(i1);
        }
    }

    private ImageView loadButtonImages(String path, int size){
        URL imageUrl = getClass().getResource(path);
        if (imageUrl == null) {
            System.err.println("Error while loading image");
            return new ImageView();
        }
        Image i = new Image(imageUrl.toExternalForm());
        ImageView iw = new ImageView(i);
        iw.setFitWidth(size);
        iw.setFitHeight(size);
        return iw;

    }
}
