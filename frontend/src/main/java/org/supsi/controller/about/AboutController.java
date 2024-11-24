package org.supsi.controller.about;

import org.supsi.model.about.AboutModel;
import org.supsi.model.about.IAboutModel;
import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.translations.ITranslationsModel;
import org.supsi.model.translations.TranslationModel;
import javafx.fxml.FXMLLoader;
import org.supsi.view.IView;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Properties;

/**
 * Controller for managing the "About" section of the application.
 * Handles the logic to retrieve build information, initialize the model, and display the "About" view.
 */
public class AboutController implements IAboutController{

    private static AboutController myself;
    private final ITranslationsModel translationsModel;
    private final IAboutModel model;
    private final ILoggerModel loggerModel;
    private IView<IAboutModel> view;

    /**
     * protected constructor to initialize the "About" controller.
     * Loads the FXML, sets up the model-view relationship, and reads build information.
     */
    protected AboutController(){
        translationsModel = TranslationModel.getInstance();
        model = AboutModel.getInstance();
        loggerModel = LoggerModel.getInstance();

        URL fxmlUrl = getResource("/layout/About.fxml");
        if (fxmlUrl == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(fxmlUrl,translationsModel.getUiBundle());
            loader.load();
            view = loader.getController();
            view.setModel(model);

        } catch (IOException e) {
            loggerModel.addError("ui_failed_to_load_component");
        }
        loggerModel.addDebug("ui_about_loaded");
        readBuildInfo();
    }

    /**
     * Retrieves the singleton instance of the {@link AboutController}.
     *
     * @return the instance of AboutController.
     */
    public static AboutController getInstance(){
        if(myself==null){
            myself=new AboutController();
        }
        return myself;
    }

    /**
     * Reads build information from a properties file written by maven and populates the model.
     * Retrieves build-time, version, and developer information from `build.properties`.
     */
    private void readBuildInfo() {
        try (InputStream propertiesStream = getResourceAsStream("/build.properties")) {
            if (propertiesStream != null) {

                Properties properties = new Properties();
                properties.load(propertiesStream);

                String buildTimeString = properties.getProperty("build-time");
                String buildVersion = properties.getProperty("build-version");
                String developer = properties.getProperty("developer");

                Locale locale = translationsModel.getLocale();

                ZonedDateTime buildTime = null;
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    buildTime = ZonedDateTime.parse(buildTimeString, formatter.withZone(ZoneId.of("UTC")));

                    buildTime = buildTime.withZoneSameInstant(ZoneId.of("Europe/Rome"));
                } catch (DateTimeParseException e) {
                    loggerModel.addDebug("ui_build_properties_date_parse_error");
                }

                String formattedDate = buildTime != null ? buildTime.format(DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT).withLocale(locale)) : "N/A";

                model.setDate(formattedDate);
                model.setVersion(buildVersion);
                model.setDeveloper(developer);

                loggerModel.addDebug("ui_build_properties_parsed");
            } else {
                loggerModel.addDebug("ui_build_properties_not_found");
            }
        } catch (IOException e) {
            loggerModel.addDebug("ui_build_properties_error");
        }
    }
    //used for test purpose (it doesn't alter or modify the code flow, structure or visibility in any way)
    protected InputStream getResourceAsStream(String resourceName) throws IOException {
        return getClass().getResourceAsStream(resourceName);
    }

    //used for test purpose
    protected URL getResource(String name) {
        return getClass().getResource(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showPopup(){
        loggerModel.addDebug("ui_start_popup_build");
        view.build();
        loggerModel.addDebug("ui_end_popup_build");
        view.show();
        loggerModel.addDebug("ui_popup_show");
    }
}
