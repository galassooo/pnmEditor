package org.supsi.view.info;

import org.supsi.model.errors.IErrorModel;

public interface IErrorPopUp {

    void build();
    void show();
    void setModel(IErrorModel model);
}
