package ch.supsi.model.image;

public class ImageModel {

    private static ImageModel myself;

    public static ImageModel getInstance(){
        if(myself==null){
            myself=new ImageModel();
        }
        return myself;
    }

    protected ImageModel() {
    }
    //CAMBIARE L'ACCESSO USANDO APPLICATION LAYER MI SERVIVA PER TESTARE

}
