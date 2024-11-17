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

/**
 * Represents a UI component for managing filters in the application.
 * Displays buttons for each available filter and an "Activate" button for applying them.
 * Handles the state and interactions for filters using model bindings.
 */
public class FilterLine {

    private static final String FILTER_IMAGE_PATH = "/images/buttons/top/%s.png";
    private final IStateModel stateModel;
    private final IFilterModel filterModel;
    private final EventPublisher publisher;

    @FXML
    public Button activate;

    @FXML
    public HBox hbox;

    /**
     * Constructs a new {@code FilterLine} and initializes required models and the event publisher.
     */
    private FilterLine(){
        stateModel = StateModel.getInstance();
        filterModel = FilterModel.getInstance();
        publisher = EventManager.getPublisher();
    }

    /**
     * Initializes the filter line UI.
     * Sets up the HBox layout and creates buttons for each filter.
     * Binds the "Activate" button's enabled state to the application's filter application capability.
     */
    @FXML
    public void initialize() {
        ITranslationsModel translationsModel = TranslationModel.getInstance();

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

    /**
     * Creates a button for a specific filter, including its tooltip and actions.
     *
     * @param filterKey        the key identifying the filter
     * @param translatedFilter the translated name of the filter
     * @param translationsModel the model for retrieving UI translations
     * @return an {@link Optional} containing the created {@link Button}, or {@code Optional.empty()} if the button couldn't be created
     */
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

    /**
     * Loads the image associated with a filter.
     *
     * @param filterName the name of the filter
     * @return an {@link Optional} containing the {@link ImageView} for the filter, or {@code Optional.empty()} if the image could not be loaded
     */
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

    /**
     * Configures the "Activate" button.
     * Sets its size and action to publish a filter execution request.
     */
    private void setupActivateButton() {
        if (activate != null) {
            activate.setPrefSize(30, 30);
            activate.setMinSize(30, 30);
            activate.setMaxSize(30, 30);

            activate.setOnAction(event -> publisher.publish(new FilterEvent.FilterExecutionRequested()));
        }
    }
}