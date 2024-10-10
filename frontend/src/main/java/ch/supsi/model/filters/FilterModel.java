package ch.supsi.model.filters;

import ch.supsi.application.filters.FilterApplication;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class FilterModel implements IFilterModel{

    private static FilterModel mySelf;
    private final FilterApplication application = FilterApplication.getInstance();

    private final ObservableList<String> filterPipeline = FXCollections.observableArrayList();

    public static FilterModel getInstance(){
        if(mySelf==null){
            mySelf=new FilterModel();
        }
        return mySelf;

    }

    protected FilterModel(){

        //X debug rapido, da eliminare LASCIA IL COSTRUTTORE PROTECTED!!!!!
        filterPipeline.addListener((ListChangeListener<String>) change -> {
            System.out.println("filterPipeline updated: " + filterPipeline);
        });
    }
    @Override
    public void addRotationLeft() {
        application.addRotation(true);
        filterPipeline.add("Rotation Left");
    }

    @Override
    public ObservableList<String> getFilterPipeline() {
        return filterPipeline;
    }

    @Override
    public void mirror() {
        application.mirror();
        filterPipeline.add("Mirror");
    }

    @Override
    public void addRotationRight() {
        application.addRotation(false);
        filterPipeline.add("Rotation Right");
    }

    @Override
    public void negative() {
        application.addNegative();
        filterPipeline.add("Negative");
    }


}
