package ch.supsi.dataaccess;

import ch.supsi.business.Image.ImageBusiness;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

// TEMPLATE PATTERN
public abstract sealed class PNMDataAccess permits PBMDataAccess, PPMDataAccess, PGMDataAccess {

    /* static fields */
    private static final List<String> ALL_ASCII_HEADERS = List.of("P1", "P2", "P3");
    private static final List<String> ALL_BINARY_HEADERS = List.of("P4", "P5", "P6");

    /* instance fields */
    //ASDRUBALO da mettere a protected dopo test
    public int width;
    //ASDRUBALO da mettere a protected dopo test
    public int height;

    //ASDRUBALO da mettere a private dopo test
    public String format;


    /**
     * read the header of a generic PNM image
     * @param is inputStream containing the image bytes
     * @throws IOException if something went wrong while reading from IS
     */
    public final void readHeader(InputStream is) throws IOException {
        //obbligata a leggere direttamente da inputStream dato che scanner, bufferedReader etc
        //mangiano i byte del file quando si chiudono e il parsing nelle figlie
        //non funzionerebbe correttamente


        //read format
        format = readLine(is).trim();
        if(!ALL_ASCII_HEADERS.contains(format) && !ALL_BINARY_HEADERS.contains(format)){
            throw new IOException("Invalid format");
        }

        //read comments and skip blank lines
        String dimensionLine;
        do {
            dimensionLine = readLine(is).trim();
        } while (dimensionLine.startsWith("#") || dimensionLine.isEmpty());

        //read dimensions
        String[] dimensions = dimensionLine.split("\\s+");
        if (dimensions.length != 2) {
            throw new IOException("width or height is missing");
        }

        //save width and height
        width = Integer.parseInt(dimensions[0]);
        height = Integer.parseInt(dimensions[1]);

        //dimension validity check
        if (width <= 0 || height <= 0) {
            throw new IOException("Invalid or missing dimension in header");
        }
    }

    /**
     * read a line from an inputStream
     * @param is inputStream to be read
     * @return a string
     * @throws IOException if there was an error while reading the byte
     */
    protected String readLine(InputStream is) throws IOException {
        StringBuilder line = new StringBuilder();
        int byteRead;
        while ((byteRead = is.read()) != -1 && byteRead != '\n') {
            line.append((char) byteRead);
        }
        return line.toString();
    }

    /**
     * Reads a PNM image file from the specified path and processes it as either binary or ASCII.
     *
     * @param path the path of the image file to read
     * @return an ImageBusiness object representing the processed image
     * @throws IOException if the file is not found or there is an error reading it
     */
    public final @NotNull ImageBusiness read(String path) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(path)){
            if (is == null) {
                throw new IOException("File not found: " + path);
            }

            readHeader(is);
            if (isBinaryFormat()) {
                return processBinary(is);
            } else {
                return processAscii(is);
            }
        } //DARA' SEMPRE 2/4 BRANCH COPERTI PERCHE' SI ASPETTA CHE PROCESS BINARY O ASCII
        // RITORNINO NULL MA E' IMPOSSIBILE E LE CLASSI SONO SEALD QUINDI NEANCHE CON UN ESTENSIONE
    }

    @NotNull
    protected abstract ImageBusiness processBinary(InputStream is) throws IOException;
    @NotNull
    protected abstract ImageBusiness processAscii(InputStream is) throws IOException;

    //ASDRUBALO da mettere a private dopo test
    public boolean isBinaryFormat() {
        return ALL_BINARY_HEADERS.contains(format);
    }

    //ASDRUBALO da eliminare dopo testing

    public void setWidth(int width) {
        this.width = width;
    }
    //ASDRUBALO da eliminare dopo testing
    public void setHeight(int height) {
        this.height = height;
    }
    //ASDRUBALO da eliminare dopo testing
    public void setFormat(String format) {
        this.format = format;
    }
}

