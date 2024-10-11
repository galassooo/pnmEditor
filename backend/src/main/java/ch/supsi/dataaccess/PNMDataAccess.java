package ch.supsi.dataaccess;

import ch.supsi.business.Image.ImageBusiness;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.spi.ImageWriterSpi;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

// TEMPLATE PATTERN
public abstract sealed class PNMDataAccess permits PBMDataAccess, PPMDataAccess, PGMDataAccess {

    private static final List<String> ALL_ASCII_HEADERS = List.of("P1", "P2", "P3");
    private static final List<String> ALL_BINARY_HEADERS = List.of("P4", "P5", "P6");
    //ASDRUBALO da mettere a protected dopo test
    public int width;
    //ASDRUBALO da mettere a protected dopo test
    public int height;

    //ASDRUBALO da mettere a private dopo test
    public String format;


    public final void readHeader(InputStream is) throws IOException {
        //obbligata a leggere direttamente da inputStream dato che scanner, bufferedReader etc
        //mangiano i byte del file quando si chiudono
        format = readLine(is).trim();
        if(!ALL_ASCII_HEADERS.contains(format) && !ALL_BINARY_HEADERS.contains(format)){
            throw new IOException("Invalid format");
        }
        String dimensionLine;
        do {
            dimensionLine = readLine(is).trim();
        } while (dimensionLine.startsWith("#") || dimensionLine.isEmpty());

        String[] dimensions = dimensionLine.split("\\s+");
        if (dimensions.length != 2) {
            throw new IOException("L'header non contiene larghezza e altezza valide.");
        }

        width = Integer.parseInt(dimensions[0]);
        height = Integer.parseInt(dimensions[1]);


        if (width <= 0 || height <= 0) {
            throw new IOException("Dimensioni non valide o non trovate nell'header del file.");
        }
        System.out.println("width: " + width + ", height: " + height);
    }

    private String readLine(InputStream is) throws IOException {
        StringBuilder line = new StringBuilder();
        int byteRead;
        while ((byteRead = is.read()) != -1 && byteRead != '\n') {
            line.append((char) byteRead);
        }
        return line.toString();
    }


    public final @NotNull ImageBusiness read(String path) throws IOException {
            try (InputStream is = getClass().getResourceAsStream(path)){
                if (is == null) {
                    throw new IOException("File non trovato: " + path);
                }

                readHeader(is);
                if (isBinaryFormat()) {
                    return processBinary(is);
                } else {
                    return processAscii(is);
                }
            } //DARA' SEMPRE 2/BRANCH COPERTI PERCHE' SI ASPETTA CHE PROCESS BINARY O ASCII
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

    public void setHeight(int height) {
        this.height = height;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
