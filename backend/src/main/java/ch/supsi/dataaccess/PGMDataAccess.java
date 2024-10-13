package ch.supsi.dataaccess;

import ch.supsi.business.strategy.ArgbConvertStrategy;
import ch.supsi.business.strategy.ArgbSingleChannel;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

public final class PGMDataAccess extends PNMWithMaxValueDataAccess {

    /* self reference */
    private static PGMDataAccess myself;


    /* singleton */
    public static PGMDataAccess getInstance() {
        if (myself == null) {
            myself = new PGMDataAccess();
        }
        return myself;
    }

    /* constructor */
    private PGMDataAccess() {}

    /**
     * Reads a binary PGM image supporting both 8-bit and 16-bit pixel formats.
     * @param is InputStream
     * @return ImageBusiness instance representing the decoded image
     * @throws IOException when pixels value are out of range or the file is corrupted
     */
    @Override
    protected long[] @NotNull [] readBinary(InputStream is) throws IOException {
        long[][] pixelMatrix = new long[height][width];

        //loop on pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grayValue;

                //check if the image is 8-bit or 16-bit based on maxGrayValue
                if (super.getMaxValue() <= 255) { // 8 bit

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

                if (grayValue > super.getMaxValue()) { //value check
                    throw new IOException("gray pixel value out of range in binary pmg file");
                }

                pixelMatrix[y][x] = grayValue;
            }
        }

        return pixelMatrix;
    }

    /**
     * Reads an ASCII PGM image, supporting both 8-bit and 16-bit pixel formats.
     * @param is InputStream
     * @return ImageBusiness instance representing the decoded image
     * @throws IOException when pixels value are out of range or the file is corrupted
     */
    @Override
    protected long[] @NotNull [] readAscii(InputStream is) throws IOException {
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
                        if (grayValue < 0 || grayValue > super.getMaxValue()) {
                            throw new IOException("gray pixel value out of range in binary pmg file");
                        }
                        pixelMatrix[y][x] = grayValue;
                    } else {
                        throw new IOException("Invalid or missing data in ascii PGM file");
                    }
                }
            }
        }
        return pixelMatrix;
    }

    @Override
    protected void writeBinaryPixels(OutputStream os, long[][] pixels, ExecutorService ex) throws IOException {

    }

    @Override
    protected void writeAsciiPixels(OutputStream os, long[][] pixels, ExecutorService ex) throws IOException {

    }

    @Override
    protected ArgbConvertStrategy getArgbConvertStrategy() {
        return new ArgbSingleChannel(getMaxValue());
    }
}

