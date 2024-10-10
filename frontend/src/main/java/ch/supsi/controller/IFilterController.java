package ch.supsi.controller;

import ch.supsi.view.filter.IFilteredListView;

public interface IFilterController {
    void addRotationLeft();
    void addRotationRight();
    void mirror();
    void negative();

    void addEventPublisher(IFilteredListView view);
}
