package ch.supsi.view.info;

import ch.supsi.model.about.IAboutModel;

public interface IAboutView {

    void show();

    void setModel(IAboutModel model);
    void build();
}
