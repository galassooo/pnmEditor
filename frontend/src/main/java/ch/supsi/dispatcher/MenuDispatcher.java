package ch.supsi.dispatcher;

import ch.supsi.controller.image.IImageController;
import ch.supsi.controller.image.ImageController;

public class MenuDispatcher {
    private static IImageController imageController;

    static{
        imageController = ImageController.getInstance();
    }

    public void save(){
        imageController.save();
    }

    public void saveAs(){
        imageController.saveAs();
    }
    public void preferences(){
        //preferences ctrl
    }

    public void open(){
        imageController.open();
    }

    public void close(){
        //exit ctrl
    }

    public void about(){
        //about ctrl
    }

}
