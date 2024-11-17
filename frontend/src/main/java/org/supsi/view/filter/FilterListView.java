package org.supsi.view.filter;

import org.supsi.model.event.EventManager;
import org.supsi.model.event.EventPublisher;
import org.supsi.model.filters.FilterModel;
import org.supsi.model.filters.IFilterModel;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * FilterListView is the class responsible for displaying and managing the filter
 * pipeline in the application.
 * This class implements the IFilteredListView interface, used to communicate with the listeners.
 * <p>
 * Main responsibilities of FilterListView include:
 * <p>- Initializing the ListView with items from the model's filter pipeline.
 * <p>- Monitoring changes in the filter pipeline to dynamically update the ListView.
 * <p>- Enabling drag-and-drop functionality for reordering filters, with the correct
 *   model indices calculated based on the reverse order between the ListView and
 *   the filter pipeline.
 * <p>- Supporting copy-paste operations for filters through keyboard shortcuts,
 *   notifying listeners of any filter addition.
 * <p>- Updating icon visuals for each filter item based on its position in the list.
 * <p>
 * The class also includes utility methods to register and deregister
 * FilterUpdateListener instances, which receive notifications on filter updates.
 */
public class FilterListView {

    /* FXML fields */
    @FXML
    private ListView<CustomCell> list;

    /* instance fields */
    private ObservableList<CustomCell> items;
    private CustomCell copiedItem;
    private IFilterModel model;
    private final EventPublisher publisher = EventManager.getPublisher();

    /**
     * Initializes the view, sets up list items, listeners, and drag-and-drop functionality.
     */
    @FXML
    void initialize() {
        model = FilterModel.getInstance();

        // Initialize cell items from the model filter pipeline
        items = FXCollections.observableArrayList();
        model.getFilterPipeline().forEach(filterName -> items.add(0, new CustomCell(filterName)));

        // Set items in the ListView only once
        list.setItems(items);

        // Add a listener to observe and show changes in the model's filter pipeline
        model.getFilterPipeline().addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved()) {
                    //!!!! since we use the list as a pipeline the first element should go on the bottom
                    //!!!! of the screen, so the listView order IS REVERSED!!!!!
                    List<CustomCell> updated = new ArrayList<>(model.getFilterPipeline().stream().map(CustomCell::new).toList());
                    Collections.reverse(updated); //reverse the list
                    items.setAll(updated);
                }
            }
            updateIcons(); //once the list has been loaded -> update icons
        });

        // set up  and key bindings for copy-paste operation
        setupKeyBindings();

        // Set cell factory for the ListView to control drag-and-drop behavior within each cell
        list.setCellFactory(new Callback<>() {
            @Override
            public ListCell<CustomCell> call(ListView<CustomCell> param) {
                ListCell<CustomCell> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(CustomCell item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null); //if we do not have item OR the list is empty:
                        } else { //otherwise set the right icons and set the graphic to the root node
                            updateIcons();
                            setGraphic(item.getRootNode());
                        }
                    }
                };

                // Set drag detection for cells
                cell.setOnDragDetected(event -> {
                    if (!cell.isEmpty()) {

                        //create a new Drag board to manage drag options and store the
                        //cell id into the drag board content
                        Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent content = new ClipboardContent();
                        content.putString(String.valueOf(cell.getItem().getId()));
                        db.setContent(content);

                        //create the transparent cell overlay effect
                        Image dragViewImage = cell.snapshot(null, null);
                        db.setDragView(dragViewImage);

                        //add the closed hand cursor while dragging cells
                        cell.getScene().getRoot().getStyleClass().add("dragging-hand");
                        event.consume();
                    }
                });

                // Handle drag over events to display where items can be dropped
                cell.setOnDragOver(event -> {
                    //this if makes sure to 'catch' the right drag event
                    if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }

                    //calculate the mouse hover
                    double cellHeight = cell.getHeight();
                    double mouseY = event.getY();

                    //highlight the cell border based on the mouse hover position
                    if (!cell.isEmpty()) {
                        if (mouseY < cellHeight / 2) {
                            cell.setStyle("-fx-border-color: white; -fx-border-width: 3 0 0 0;"); //TOP
                        } else {
                            cell.setStyle("-fx-border-color: white; -fx-border-width: 0 0 3 0;"); //BOTTOM
                        }
                    }
                    event.consume();
                });

                // clear drag indicator style when dragging exits the cell
                cell.setOnDragExited(event -> {
                    if (!cell.isEmpty()) {
                        cell.setStyle(""); //per il mouse
                        event.consume();
                    }
                });

                // Handle the drop event to rearrange items based on drag-and-drop actions
                cell.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;

                    if (db.hasString()) {
                        //retrieve the dragged element id by the drag board
                        //(the id has been stored in the db in setOnDragDetected method
                        int draggedId = Integer.parseInt(db.getString());
                        CustomCell draggedData = findElementById(draggedId);

                        //calculate indexes
                        int draggedIndex = items.indexOf(draggedData);
                        int targetIndex = items.indexOf(cell.getItem());

                        //if the indexes are valid
                        if (draggedIndex != targetIndex && targetIndex >= 0) {
                            //calculate the model indexes since the LISTVIEW ORDER IS REVERSED
                            int modelFromIndex = items.size() - 1 - draggedIndex;
                            int modelToIndex = items.size() - 1 - targetIndex;
                            success = true;
                            //notify listeners of the item movement
                            publisher.publish(new FilterEvent.FilterMoveRequested(modelFromIndex, modelToIndex));
                        }
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });

                // Handle the end of the drag-and-drop event
                cell.setOnDragDone(event -> {
                    cell.getScene().getRoot().getStyleClass().remove("dragging-hand");
                    event.consume();
                });


                ContextMenu contextMenu = new ContextMenu();
                MenuItem deleteItem = new MenuItem("Delete");

                deleteItem.setOnAction(event -> {
                    CustomCell selectedItem = cell.getItem();
                    if (selectedItem != null) {
                        int index = items.indexOf(selectedItem);
                        publisher.publish(new FilterEvent.FilterRemoveRequested(items.size() - 1 - index));
                        items.remove(selectedItem);
                    }
                });
                contextMenu.getItems().add(deleteItem);

                cell.setOnContextMenuRequested(event -> {
                    if (!cell.isEmpty()) {
                        contextMenu.show(cell, event.getScreenX(), event.getScreenY());
                    }
                });

                return cell;
            }
        });
    }

    /**
     * Sets up key bindings for copy and paste actions.
     */
    private void setupKeyBindings() {
        list.setOnKeyPressed(event -> {
            //check if the ctrl or command key is pressed (works with MacOS, windows and Linux)
            boolean isControlDown = event.isControlDown() || event.isMetaDown();

            //switch on the key pressed
            switch (event.getCode()) {
                case C:
                    //save the selected cell (if available) in the copiedItem variable
                    if (isControlDown && list.getSelectionModel().getSelectedItem() != null) {
                        copiedItem = list.getSelectionModel().getSelectedItem();
                    }
                    break;

                case V:
                    //it notifies all the listener that a paste operation has been requested for the item
                    if (isControlDown && copiedItem != null) {
                        // Notify listeners of a new filter being added
                        publisher.publish(new FilterEvent.FilterAddRequested(copiedItem.getText()));
                    }
                    break;
            }
        });
    }

    /**
     * Updates icons in the ListView's cells based on the position of each item.
     *
     */
    private void updateIcons() {
        for (int i = 0; i < items.size(); i++) {
            //for each cell
            CustomCell item = items.get(i);

            //select the right icon according to its position in the list
            String iconPath =
                    switch (items.size()) {
                        case 1 -> "/images/listCell/arrows/single.png";
                        default -> {
                            if (i == 0) yield "/images/listCell/arrows/top.png";
                            else if (i == items.size() - 1) yield "/images/listCell/arrows/bottom.png";
                            else yield "/images/listCell/arrows/middle.png";
                        }
                    };

            //set the new icon
            item.setArrowImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
        }
    }

    /**
     * Finds a CustomCell in the list by its ID.
     *
     * @param id The ID to search for.
     * @return The CustomCell with the specified ID, or null if not found.
     */
    private CustomCell findElementById(int id) {
        return items.stream()
                .filter(element -> element.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
