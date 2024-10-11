package ch.supsi.business.Image;

import ch.supsi.business.strategy.ArgbConvertStrategy;

public class ImageBusiness{

    private int[][] argbPixels;
    private int width;
    private int height;

    public ImageBusiness(int[][] original, int width, int height, ArgbConvertStrategy strategy) {
        this.argbPixels = strategy.toArgb(original);
        this.width = width;
        this.height = height;
    }

    public int[][] getPixels() {
        return argbPixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }




    /*tutte le immagini hanno h,w e matrice di pixel PER FORZA.
    essendo che le immagini DEVONO essere per forza aperte dall'utente attraverso il
    file system, ergo non viene generata un immagine dalla GUI avrebbe senso salvare qui anche il
    path all'immagine in quanto E' UN ATTRIBUTO DELL IMMAGINE. inoltre questo mi permetterebbe di avere
    nel reader Interface un metodo write(Image image) e poi la factory se lo smazza in qualche modo:

    frontend:
    SaveImage(Image imageToBeSaved){
        imageBackendController.write(imageToBeSaved);
    }

    ImageBackendController:
    write(Image imageToBeSaved){
        imageToBeSaved.persist(); //delego
    }

    ImageBusiness (questa classe):
    persist(){
        Reader r = RegisterFactory.getReader(this.path); //ottengo il reader giusto
        r.write(this);
    }

     */

    /*
    PROBLEMA: ogni immagine ha un numero massimo del valore in pixel: ad esempio PGM: 0-255
    Per il negativo serve sapere il valore massimo del pixel
     */

    /*
    SCARTA TEMPLATE PATTERN PERCHE':
        si basa sull'avere un algoritmo unico che va diviso in substeps diversi, includi
        ogni substep in una classe figlia (puoi usarlo nel reader, leggi -scrivi header padre(parte
        comune a tutti i formati) e fai la specializzazione della lettura/scrittura nelle subclass)

    SCARTA DECORATOR PATTERN PERCHE':
        wrappa l'oggetto inserendo nuovi comportamenti, ovvero permette di presentare l'oggetto in
        modi diversi, ma devo rappresentare l oggetto in un unico modo (argb), cambia il
        comportamento della conversione

    USA STRATEGY PATTERN PERCHE':
        differenzia le strategie di conversione per arrivare ad uno stesso risultato.
        dal reader quando costruisci immagine passi anche il convertor perchè:

        il reader si occupa dell accesso ai dati e di conseguenza è lui a sapere quale formato sta leggendo
        quindi si occupa di costruire un immagine.
        new ImageBusiness(path, w, h, pixels[][], new BooleanToARGBConv());
     */
}
