package ch.supsi.business.Image;

import ch.supsi.application.Image.ImageBusinessInterface;
import ch.supsi.business.strategy.ArgbConvertStrategy;

public class ImageBusiness implements ImageBusinessInterface {

    /* instance field */
    private long[][] argbPixels;
    private int width;
    private int height;

    private final String filePath;

    // Tutti i formati di immagine
    // (PNG, BMP, GIF, JPEG, TIFF, ICO, PPM, PGM, PBM etc...)
    // hanno un magicNumber che permette al software di riconoscerne
    // il tipo senza basarsi sull'estensione. Di conseguenza va incluso
    // come attributo di un immagine generica
    // P + valore nei PNM è semplicemente codificato in ascii e non in byte
    private final String magicNumber;

    public ImageBusiness(long[][] original, String path, String magicNumber, int maxVal, ArgbConvertStrategy strategy) {
        this.argbPixels = createArgbMatrix(original,maxVal, strategy);
        this.height = original.length;
        this.width = height == 0? 0 : original[0].length;
        filePath = path;

        this.magicNumber = magicNumber;
    }

    public long[][] getPixels() {
        return argbPixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getMagicNumber() {
        return magicNumber;
    }

    private long[][] createArgbMatrix(long[][] original, int maxValue, ArgbConvertStrategy strategy) {
        int height = original.length;
        if (height == 0) {
            return new long[0][0];
        }
        int width = original[0].length;

        long[][] argbMatrix = new long[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                argbMatrix[y][x] = strategy.toArgb(original[y][x], maxValue);
            }
        }

        return argbMatrix;
    }
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


