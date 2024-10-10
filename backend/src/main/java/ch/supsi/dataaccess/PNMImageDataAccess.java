package ch.supsi.dataaccess;

//TEMPLATE PATTERN
public abstract class PNMImageDataAccess {
    public void readHeader(){

    }
    public final void read(){
        readHeader();
        process();
    }

    public abstract void process(); //IMPLEMENTALO IN PPM PGM PBM
}
