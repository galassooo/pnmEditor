package ch.supsi.view.filter;

import ch.supsi.model.CustomCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

public class FilterListView {

    @FXML
    private javafx.scene.control.ListView<CustomCell> list;

    private ObservableList<CustomCell> items;
    private CustomCell copiedItem;
    private CustomCell draggedItem;


    @FXML
    void initialize() {
        items = FXCollections.observableArrayList(new CustomCell("a"), new CustomCell("b"), new CustomCell("c"));

        list.setItems(items);
        setupDragAndDrop();
        setupKeyBindings();
        list.setCellFactory(new Callback<>() {
            @Override
            public ListCell<CustomCell> call(ListView<CustomCell> param) {
                ListCell<CustomCell> cell = new ListCell<CustomCell>() {
                    @Override
                    protected void updateItem(CustomCell item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            setGraphic(item.getHbox());
                        }
                    }
                };

                // Gestione del drag per ogni cella
                cell.setOnDragDetected(event -> {

                    if (!cell.isEmpty()) {
                        Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent content = new ClipboardContent();
                        // Memorizza l'ID dell'elemento trascinato nel Dragboard
                        content.putString(String.valueOf(cell.getItem().getId()));
                        db.setContent(content);

                        //crea l'effetto ombra quando prendo l'oggetto
                        Image dragViewImage = cell.snapshot(null, null);
                        db.setDragView(dragViewImage);
                        cell.getScene().getRoot().getStyleClass().add("dragging-hand");

                        draggedItem = cell.getItem();  // Salva l'elemento che viene trascinato
                        event.consume();
                    }
                });

                // Consenti il drag sopra altre celle
                cell.setOnDragOver(event -> {
                    if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    // Ottieni la posizione del mouse all'interno della cella
                    double cellHeight = cell.getHeight();
                    double mouseY = event.getY();

                    if(!cell.isEmpty()) {
                        // Se il mouse è nella metà superiore della cella, evidenzia il bordo superiore
                        if (mouseY < cellHeight / 2) {
                            cell.setStyle("-fx-border-color: white; -fx-border-width: 3 0 0 0;"); // Bordo superiore
                        }
                        // Se il mouse è nella metà inferiore della cella, evidenzia il bordo inferiore
                        else {
                            cell.setStyle("-fx-border-color: white; -fx-border-width: 0 0 3 0;"); // Bordo inferiore
                        }
                    }
                    event.consume();
                });
                cell.setOnDragExited(event -> {
                    if (!cell.isEmpty()) {
                        // Rimuovi lo stile quando il mouse esce dalla cella
                        cell.setStyle("");  // Ripristina lo stile originale
                        event.consume();
                    }
                });

                // Gestione del rilascio dell'elemento
                cell.setOnDragDropped(event -> {
                            Dragboard db = event.getDragboard();
                            boolean success = false;

                            if (db.hasString()) {
                                int draggedId = Integer.parseInt(db.getString());
                                CustomCell draggedData = findElementById(draggedId);
                                // Cerca l'elemento trascinato usando l'ID memorizzato


                                int draggedIndex = items.indexOf(draggedData);  // Indice dell'elemento trascinato
                                int targetIndex = items.indexOf(cell.getItem());  // Indice dell'elemento target

                                if (draggedIndex != targetIndex && targetIndex >= 0) {
                                    // Rimuovi dalla posizione originale e aggiungi nella nuova
                                    items.remove(draggedData);
                                    items.add(targetIndex, draggedData);
                                    success = true;
                                }
                            }

                            event.setDropCompleted(success);
                            event.consume();
                        }
                );
                cell.setOnDragDone(event -> {
                    // Rimuovi la classe CSS per ripristinare il cursore normale
                    cell.getScene().getRoot().getStyleClass().remove("dragging-hand");
                    event.consume();
                });
                return cell;
            }

        });
    }

    private void setupDragAndDrop() {
        // Gestisce il drop fuori dalle celle per aggiungere alla fine della lista
        list.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String draggedDataId = db.getString();
                // Cerca l'elemento trascinato usando l'ID memorizzato
                CustomCell draggedData = findElementById(Integer.parseInt(draggedDataId));

                if (!items.contains(draggedData)) {
                    // Se l'elemento non è già presente, aggiungilo alla fine
                    items.add(draggedData);
                }
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void setupKeyBindings() {
        list.setOnKeyPressed(event -> {
            // Verifica se il tasto Control (per Windows/Linux) o Command (per macOS) è premuto
            boolean isControlDown = event.isControlDown() || event.isMetaDown();

            switch (event.getCode()) {
                case C: // Copia
                    if (isControlDown && list.getSelectionModel().getSelectedItem() != null) {
                        copiedItem = list.getSelectionModel().getSelectedItem();
                    }
                    break;

                case V: // Incolla
                    if (isControlDown && copiedItem != null) {
                        items.add(new CustomCell(copiedItem.getText()));  // Aggiungi una nuova copia dell'elemento
                    }
                    break;
            }
        });
    }


    private CustomCell findElementById(int id) {
        // Cerca un elemento per ID nella lista
        return items.stream()
                .filter(element -> element.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
