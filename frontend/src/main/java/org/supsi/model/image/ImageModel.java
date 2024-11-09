package org.supsi.model.image;

import ch.supsi.application.image.ImageApplication;

import java.io.IOException;

public class ImageModel implements IImageModel{

    private static ImageModel myself;

    private static final ImageApplication backendController;


    static {
        backendController = ImageApplication.getInstance();
    }

    public static ImageModel getInstance(){
        if(myself==null){
            myself=new ImageModel();
        }
        return myself;
    }

    protected ImageModel() {
    }

    public void readImage(String path) throws IOException, IllegalAccessException {
            backendController.read(path);
    }

    public String getImageName(){
        return backendController.getImageName();
    }

    @Override
    public long[][] getImagePixels() {
        return backendController.getImagePixels();
    }

    public void writeImage(String path) throws IOException, IllegalAccessException {
           backendController.persist(path);
    }
}
