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

public class AboutController implements IAboutController{

    private static AboutController myself;
    private final ITranslationsModel translationsModel;
    private final IAboutModel model;
    private final ILoggerModel loggerModel;
    private IView<IAboutModel> view;

    protected AboutController(){
        translationsModel = TranslationModel.getMyself();
        model = AboutModel.getInstance();
        loggerModel = LoggerModel.getInstance();

        URL fxmlUrl = getClass().getResource("/layout/About.fxml");
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

    public static AboutController getInstance(){
        if(myself==null){
            myself=new AboutController();
        }
        return myself;
    }

    private void readBuildInfo() {
        try (InputStream propertiesStream = getClass().getResourceAsStream("/build.properties")) {
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

    @Override
    public void showPopup(){
        loggerModel.addDebug("ui_start_popup_build");
        view.build();
        loggerModel.addDebug("ui_end_popup_build");
        view.show();
        loggerModel.addDebug("ui_popup_show");
    }
}
