package ch.supsi.dataaccess;

import ch.supsi.business.Image.ImageBusiness;
import ch.supsi.business.strategy.ArgbSingleBit;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public final class PBMDataAccess extends PNMDataAccess {

    /* self reference */
    private static PBMDataAccess myself;

    /* static field */
    private static final int MAX_PIXEL_VALUE = 1;

    //ASDRUBALO elimina questo campo serve per testing e basta
    public int[][] originalMatrix;

    /* singleton */
    public static PBMDataAccess getInstance() {
        if (myself == null) {
            myself = new PBMDataAccess();
        }
        return myself;
    }

    /* constructor */
    private PBMDataAccess() {

    }

    /**
     * Processes binary PBM image data
     *
     * @param is InputStream containing binary PBM image data
     * @return ImageBusiness instance representing the decoded image
     * @throws IOException if the pixels don't match the width/height
     */
    @Override
    public @NotNull ImageBusiness processBinary(InputStream is) throws IOException {
        int[][] pixelMatrix = new int[height][width];
        int bytesPerRow = (width + 7) / 8;

        for (int y = 0; y < height; y++) {
            for (int byteIndex = 0; byteIndex < bytesPerRow; byteIndex++) {
                int byteValue = is.read();
                if (byteValue == -1) {
                    throw new IOException("Dati insufficienti nel file PBM.");
                }

                //splitta il byte in bit e assegna il valore
                for (int bit = 0; bit < 8 && (byteIndex * 8 + bit) < width; bit++) {
                    int x = byteIndex * 8 + bit;
                    pixelMatrix[y][x] = (byteValue >> (7 - bit)) & 1;
                }
            }
        }
        //ASDRUBALO
        originalMatrix = pixelMatrix;

        return new ImageBusiness(pixelMatrix, width, height, MAX_PIXEL_VALUE,  new ArgbSingleBit());
    }

    /**
     * Processes ascii PBM image data
     *
     * @param is InputStream containing ASCII PBM image data
     * @return ImageBusiness instance representing the decoded image
     * @throws IOException if there is an error in reading the ASCII data
     */
    @Override
    public @NotNull ImageBusiness processAscii(InputStream is)throws IOException {
        int[][] pixelMatrix = new int[height][width];
        try (Scanner scanner = new Scanner(is)) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (scanner.hasNextInt()) {
                        pixelMatrix[y][x] = scanner.nextInt();
                    } else {
                        throw new IOException("Formato del file PBM ASCII non valido o dati mancanti.");
                    }
                }
            }
        }

        //ASDRUBALO
        originalMatrix = pixelMatrix;
        return new ImageBusiness(pixelMatrix, width, height, MAX_PIXEL_VALUE, new ArgbSingleBit());

    }
}
