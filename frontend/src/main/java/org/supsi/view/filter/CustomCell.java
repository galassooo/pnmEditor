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

/**
 * A specialized cell component for displaying filter items in a ListView with custom visual elements.
 * Each cell represents a filter in the pipeline and includes directional arrows indicating filter flow,
 * a label for the filter name, and interactive elements for user manipulation.
 *
 * <p>Visual Components:
 * <ul>
 *   <li>Direction arrow - Indicates filter position and flow in the pipeline</li>
 *   <li>Filter label - Displays the filter name</li>
 *   <li>Options icon - Provides access to additional actions</li>
 * </ul>
 *
 * <p>Key features:
 * <ul>
 *   <li>Unique identifier system for reliable cell tracking during drag operations</li>
 *   <li>Configurable layout with consistent spacing and padding</li>
 *   <li>Dynamic arrow indicators that update based on cell position</li>
 *   <li>Support for right-aligned additional options</li>
 * </ul>
 *
 * <p>Layout Structure:
 * <ul>
 *   <li>Root: StackPane containing overlaid horizontal layouts</li>
 *   <li>Primary HBox: Arrow icon and filter label</li>
 *   <li>Secondary HBox: Right-aligned options icon</li>
 * </ul>
 *
 * <p>The class uses a static ID counter to ensure each cell instance has a unique
 * identifier, which is crucial for drag-and-drop operations and cell state management.
 *
 * <p>Arrow icons are dynamically updated based on the cell's position in the list:
 * <ul>
 *   <li>Single arrow for lone items</li>
 *   <li>Top arrow for first item</li>
 *   <li>Middle arrow for intermediate items</li>
 *   <li>Bottom arrow for last item</li>
 * </ul>
 *
 * @see FilterListView
 */

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
