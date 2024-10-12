package ch.supsi.dataaccess;

import ch.supsi.business.Image.ImageBusiness;
import ch.supsi.business.strategy.ArgbSingleChannel;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public final class PGMDataAccess extends PNMDataAccess {

    /* self reference */
    private static PGMDataAccess myself;

    /* instance field */
    private int maxGrayValue;

    //ASDRUBALO da eliminare dopo testing
    public long[][] originalMatrix;


    /* singleton */
    public static PGMDataAccess getInstance() {
        if (myself == null) {
            myself = new PGMDataAccess();
        }
        return myself;
    }

    /* constructor */
    private PGMDataAccess() {
    }

    /**
     * Reads the max gray value from the PGM file header
     * @param is inputStream
     * @throws IOException when the maxValue is missing or has a wrong format
     */
    public void readMaxValue(InputStream is) throws IOException {
        String maxGrayLine = readLine(is).trim(); //read maxVal riga

        try {
            //parse and check the value (non differenzia 8-16 bit per ora)
            maxGrayValue = Integer.parseInt(maxGrayLine);
            if (maxGrayValue <= 0 || maxGrayValue > 65535) {
                throw new IOException("Invalid max gray value in header");
            }
        } catch (NumberFormatException e) {
            throw new IOException("Max gray value is missing or invalid in header");
        }
    }

    /**
     * Reads a binary PGM image supporting both 8-bit and 16-bit pixel formats.
     * @param is InputStream
     * @return ImageBusiness instance representing the decoded image
     * @throws IOException when pixels value are out of range or the file is corrupted
     */
    @Override
    public @NotNull ImageBusiness processBinary(InputStream is) throws IOException {
        readMaxValue(is);
        long[][] pixelMatrix = new long[height][width];

        //loop on pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grayValue;

                //check if the image is 8-bit or 16-bit based on maxGrayValue
                if (maxGrayValue <= 255) { // 8 bit

                    grayValue = is.read(); //read 8 bit value
                    if (grayValue == -1) { //EOF before all pixels
                        throw new IOException("Insufficient data in binary pmg file");
                    }
                } else { // 16 bit
                    //read MSB and LSB
                    int highByte = is.read();
                    int lowByte = is.read();
                    if (highByte == -1 || lowByte == -1) { //EOF check
                        throw new IOException("Insufficient data in binary pmg file for a 16 bit image");
                    }
                    //create a 16bit value
                    grayValue = (highByte << 8) | lowByte;
                }

                if (grayValue > maxGrayValue) { //value check
                    throw new IOException("gray pixel value out of range in binary pmg file");
                }

                pixelMatrix[y][x] = grayValue;
            }
        }

        //ASDRUBALO da rimuovere dopo test
        originalMatrix = pixelMatrix;

        // Return an ImageBusiness instance with pixel data and ARGB conversion strategy
        return new ImageBusiness(pixelMatrix, width, height, maxGrayValue, new ArgbSingleChannel());
    }

    /**
     * Reads an ASCII PGM image, supporting both 8-bit and 16-bit pixel formats.
     * @param is InputStream
     * @return ImageBusiness instance representing the decoded image
     * @throws IOException when pixels value are out of range or the file is corrupted
     */
    @Override
    public @NotNull ImageBusiness processAscii(InputStream is) throws IOException {
        readMaxValue(is);
        long[][] pixelMatrix = new long[height][width];

        try (Scanner scanner = new Scanner(is)) {
            //loop on pixels
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    //retrieve an int
                    //con questa implementazione non serve differenziare 8 e 16 bit perchè
                    // nel file sono separati con uno spazio e quindi vengo letti e interpretati
                    //correttamente entrambi i valori
                    if (scanner.hasNextInt()) {
                        int grayValue = scanner.nextInt();
                        //valid value check
                        if (grayValue < 0 || grayValue > maxGrayValue) {
                            throw new IOException("gray pixel value out of range in binary pmg file");
                        }
                        pixelMatrix[y][x] = grayValue;
                    } else {
                        throw new IOException("Invalid or missing data in ascii PGM file");
                    }
                }
            }
        }

        //ASDRUBALO da rimuovere dopo test
        originalMatrix = pixelMatrix;

        return new ImageBusiness(pixelMatrix, width, height, maxGrayValue, new ArgbSingleChannel());
    }
}

