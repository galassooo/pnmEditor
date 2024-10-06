package ch.supsi.model;

import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.Objects;


public class CustomCell {
    private HBox hbox = new HBox();
    private Label label = new Label();
    private int id;
    private static int idCnt = 0;

    public CustomCell(String text) {
        this();
        label.setText(text);
    }

    public CustomCell() {
        // Imposta layout e stili
        hbox.setSpacing(10);
        id = idCnt++;
        // Crea icona di accensione/spegnimento
        ImageView toggleIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/buttons/listCell/img.png"))));
        toggleIcon.setFitWidth(16);
        toggleIcon.setFitHeight(16);
        // Per l'icona di accensione/spegnimento
        Button buttonToggle = new Button();
        buttonToggle.setGraphic(toggleIcon);

        // Crea icona per opzioni
        ImageView moreOptionsIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/buttons/listCell/img1.png"))));
        moreOptionsIcon.setFitWidth(16);
        moreOptionsIcon.setFitHeight(16);
        // Per l'icona delle opzioni

        buttonToggle.getStyleClass().add("list-button");
        
        Pane pane = new Pane();
        pane.setPrefSize(20,20);
        //pane.getStyleClass().add("pane-hand-hover");

        StackPane sp = new StackPane(pane, moreOptionsIcon);


        // Aggiungi gli elementi alla cella
        hbox.getChildren().addAll(buttonToggle, label, sp);
    }

    public HBox getHbox() {
        return hbox;
    }

    public int getId() {
        return id;
    }

    public String getText(){
        return label.getText();
    }
}

