package org.supsi.view.info;

import org.supsi.model.about.IAboutModel;

public interface IAboutView {

    void show();

    void setModel(IAboutModel model);
    void build();
}
