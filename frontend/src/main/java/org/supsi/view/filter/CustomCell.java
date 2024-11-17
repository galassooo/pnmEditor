package org.supsi.view.filter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class CustomCell {

    private static int idCnt;
    private final Label label;
    private final ImageView arrowIcon;
    private final int id;
    private final StackPane stackPane;

    static {
        idCnt = 0;
    }

    /**
     * constructor that creates a CustomCell with specified text.
     *
     * @param text The text to display in the cell label.
     */
    public CustomCell( String text) {
        this();  //calls the private no args constructor
        label.setText(text);
    }

    /**
     * protected constructor for internal setup, initializing the label,
     * arrow icon, and layout structure.
     */
    protected CustomCell() {
        label = new Label();
        stackPane = new StackPane();
        arrowIcon = new ImageView();

        // horizontal box containing arrow icon and label
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        id = idCnt++;  // assigns a unique ID to this cell instance
        arrowIcon.setFitWidth(16);
        arrowIcon.setFitHeight(16);

        // More options icon setup
        ImageView moreOptionsIcon = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/images/listCell/img1.png"))
        ));
        moreOptionsIcon.setFitWidth(16);
        moreOptionsIcon.setFitHeight(16);

        // Horizontal box for more options icon, aligned to the right
        HBox hbox2 = new HBox();
        hbox2.setSpacing(10);
        hbox2.setAlignment(Pos.CENTER_RIGHT);
        hbox2.setPadding(new Insets(0, 20, 0, 0));  // right padding
        hbox2.getChildren().add(moreOptionsIcon);

        // arrow icon and label set to the primary HBox layout
        hbox.getChildren().addAll(arrowIcon, label);

        //adds both HBoxes to the StackPane, with hbox2 positioned to the right
        stackPane.getChildren().addAll(hbox, hbox2);
    }


    /**
     * gets the root node of the CustomCell, which includes the layout structure and
     * graphical elements.
     *
     * @return The StackPane containing the cell's UI components.
     */
    public Node getRootNode() {
        return stackPane;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return label.getText();
    }

    public void setArrowImage(Image image) {
        arrowIcon.setImage(image);
    }
}
