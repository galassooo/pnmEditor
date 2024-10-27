package ch.supsi.controller.about;

import ch.supsi.model.about.AboutModel;
import ch.supsi.model.about.IAboutModel;
import ch.supsi.model.translations.ITranslationsModel;
import ch.supsi.model.translations.TranslationModel;
import ch.supsi.view.info.AboutView;
import ch.supsi.view.info.IAboutView;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class AboutController implements IAboutController{

    private static AboutController myself;

    private final ITranslationsModel translationsModel = TranslationModel.getInstance();
    private final IAboutModel model = AboutModel.getInstance();

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
            e.printStackTrace();
        }
        readBuildInfo();
    }

    private void readBuildInfo() {
        try {
            InputStream manifestStream = getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
            if (manifestStream != null) {
                Manifest manifest = new Manifest(manifestStream);
                Attributes attributes = manifest.getMainAttributes();

                model.setDate(attributes.getValue("Build-Time"));
                model.setVersion(attributes.getValue("Build-Version"));
                model.setDeveloper(attributes.getValue("Developer"));
            }
        } catch (IOException e) {
            return;
        }
    }

    public void showPopup(){
        view.build();
        view.show();
    }
}
