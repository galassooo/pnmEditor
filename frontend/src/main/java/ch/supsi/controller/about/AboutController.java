package ch.supsi.controller.about;

import ch.supsi.model.about.AboutModel;
import ch.supsi.model.about.IAboutModel;
import ch.supsi.model.info.ILoggerModel;
import ch.supsi.model.info.LoggerModel;
import ch.supsi.model.translations.ITranslationsModel;
import ch.supsi.model.translations.TranslationModel;
import ch.supsi.view.info.AboutView;
import ch.supsi.view.info.IAboutView;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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
        try {
            InputStream manifestStream = getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
            if (manifestStream != null) {
                Manifest manifest = new Manifest(manifestStream);
                Attributes attributes = manifest.getMainAttributes();

                String buildTimeString = attributes.getValue("Build-Time");

                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                LocalDateTime buildTime = LocalDateTime.parse(buildTimeString, inputFormatter);


                Locale locale = translationsModel.getLocale();
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss", locale);
                String formattedDate = buildTime.format(outputFormatter);

                model.setDate(formattedDate);
                model.setVersion(attributes.getValue("Build-Version"));
                model.setDeveloper(attributes.getValue("Developer"));
                loggerModel.addDebug("ui_manifest_parsed");

            }
        } catch (IOException e) {
            loggerModel.addDebug("ui_manifest_missing");
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
