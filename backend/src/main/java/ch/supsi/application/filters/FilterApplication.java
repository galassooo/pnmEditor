package ch.supsi.application.filters;

public class FilterApplication {

    private static FilterApplication myself;

    public static  FilterApplication getInstance(){
        if (myself==null){
            myself = new FilterApplication();
        }
        return myself;
    }

    public void addRotation(boolean left){

    }

    public void mirror() {

    }

    public void addNegative() {
    }
}
