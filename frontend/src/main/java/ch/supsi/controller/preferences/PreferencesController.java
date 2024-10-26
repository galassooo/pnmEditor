package ch.supsi.controller.preferences;

import ch.supsi.model.preferences.IPreferencesModel;
import ch.supsi.model.preferences.PreferencesModel;
import ch.supsi.view.preferences.IPreferencesView;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

public class PreferencesController implements IPreferencesController{

    private static PreferencesController mySelf;

    private IPreferencesView view;

    private IPreferencesModel model = PreferencesModel.getInstance();

    public static PreferencesController getInstance() {
        if (mySelf == null) {
            mySelf = new PreferencesController();
        }
        return mySelf;
    }

    public PreferencesController() {
            URL fxmlUrl = getClass().getResource("/layout/PreferencesPopup.fxml");
            if (fxmlUrl == null) {
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            try {
                loader.load();
                view = loader.getController();
                view.setModel(model);

            } catch (IOException ignored) {

            }
    }

    @Override
    public void showPreferencesPopup(){
        view.build();
        view.show();
    }
}
