package ch.supsi.controller.about;

import ch.supsi.model.about.AboutModel;
import ch.supsi.model.about.IAboutModel;
import ch.supsi.model.info.ILoggerModel;
import ch.supsi.model.info.LoggerModel;
import ch.supsi.model.translations.ITranslationsModel;
import ch.supsi.model.translations.TranslationModel;
import ch.supsi.view.info.IAboutView;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class AboutController implements IAboutController{

    private static AboutController myself;

    private final ITranslationsModel translationsModel = TranslationModel.getInstance();
    private final IAboutModel model = AboutModel.getInstance();
    private final ILoggerModel loggerModel = LoggerModel.getInstance();

    private IAboutView view;

    public static AboutController getInstance(){
        if(myself==null){
            myself=new AboutController();
        }
        return myself;
    }

    protected AboutController(){
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

    public void showPopup(){
        loggerModel.addDebug("ui_start_popup_build");
        view.build();
        loggerModel.addDebug("ui_end_popup_build");
        view.show();
        loggerModel.addDebug("ui_popup_show");
    }
}
