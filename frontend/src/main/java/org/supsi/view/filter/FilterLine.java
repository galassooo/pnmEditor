package org.supsi.view.filter;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.supsi.model.event.EventManager;
import org.supsi.model.event.EventPublisher;
import org.supsi.model.filters.FilterModel;
import org.supsi.model.filters.IFilterModel;
import org.supsi.model.state.IStateModel;
import org.supsi.model.state.StateModel;
import org.supsi.model.translations.ITranslationsModel;
import org.supsi.model.translations.TranslationModel;

import java.util.Optional;

public class FilterLine {

    private static final String FILTER_IMAGE_PATH = "/images/buttons/top/%s.png";
    private final IStateModel stateModel;
    private final IFilterModel filterModel;
    private final EventPublisher publisher;

    @FXML
    public Button activate;

    @FXML
    public HBox hbox;

    private FilterLine(){
        stateModel = StateModel.getInstance();
        filterModel = FilterModel.getInstance();
        publisher = EventManager.getPublisher();
    }

    @FXML
    public void initialize() {
        ITranslationsModel translationsModel = TranslationModel.getMyself();

        hbox.setSpacing(5);
        hbox.setPadding(new Insets(5));
        hbox.getStyleClass().add("filter-hbox");

        filterModel.getFiltersKeyValues().forEach((filterKey, translatedFilter) ->
                createFilterButton(filterKey, translatedFilter, translationsModel)
                        .ifPresentOrElse(button -> hbox.getChildren().add(button),
                                () -> System.out.println("[\u001B[33mWARNING\u001B[0m] Found a filter without an associated image - available in menu bar only"))
        );

        activate.disableProperty().bind(stateModel.canApplyFiltersProperty().not());
        setupActivateButton();
    }

    private Optional<Button> createFilterButton(String filterKey, String translatedFilter, ITranslationsModel translationsModel) {
        return loadImage(filterKey)
                .map(imageView -> {
                    Button button = new Button();

                    imageView.setPreserveRatio(true);
                    imageView.setFitHeight(25);
                    imageView.setFitWidth(25);
                    button.setGraphic(imageView);

                    button.setTooltip(new Tooltip(translationsModel.translate("ui_apply") + " " + translatedFilter));
                    button.disableProperty().bind(stateModel.canApplyFiltersProperty().not());

                    button.setPrefSize(25, 25);
                    button.setMinSize(25, 25);
                    button.setMaxSize(25, 25);

                    button.getStyleClass().add("filter-button");
                    HBox.setMargin(button, new Insets(0, 10, 0, 10));

                    button.setOnAction(event -> publisher.publish(new FilterEvent.FilterAddRequested(filterKey)));

                    return button;
                });
    }

    private Optional<ImageView> loadImage(String filterName) {
        try {
            Image image = new Image(String.format(FILTER_IMAGE_PATH, filterName));
            if (image.isError() || image.getWidth() == 0) {
                return Optional.empty();
            }
            return Optional.of(new ImageView(image));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void setupActivateButton() {
        if (activate != null) {
            activate.setPrefSize(30, 30);
            activate.setMinSize(30, 30);
            activate.setMaxSize(30, 30);

            activate.setOnAction(event -> publisher.publish(new FilterEvent.FilterExecutionRequested()));
        }
    }
}