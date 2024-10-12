package ch.supsi.dataaccess;

import ch.supsi.business.strategy.ArgbConvertStrategy;
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
    //ASDRUBALO protected
    @Override
    public long[] @NotNull [] processBinary(InputStream is) throws IOException {
        long[][] pixelMatrix = new long[height][width];
        int bytesPerRow = (width + 7) / 8;

        for (int y = 0; y < height; y++) {
            for (int byteIndex = 0; byteIndex < bytesPerRow; byteIndex++) {
                int byteValue = is.read();
                if (byteValue == -1) {
                    throw new IOException("Insufficient data in pbm binary file");
                }

                //splitta il byte in bit e assegna il valore
                for (int bit = 0; bit < 8 && (byteIndex * 8 + bit) < width; bit++) {
                    int x = byteIndex * 8 + bit;
                    pixelMatrix[y][x] = (byteValue >> (7 - bit)) & 1;
                }
            }
        }
        return pixelMatrix;
    }

    /**
     * Processes ascii PBM image data
     *
     * @param is InputStream containing ASCII PBM image data
     * @return ImageBusiness instance representing the decoded image
     * @throws IOException if there is an error in reading the ASCII data
     */
    //ASDRUBALO protected
    @Override
    public long[] @NotNull [] processAscii(InputStream is)throws IOException {
        long[][] pixelMatrix = new long[height][width];
        try (Scanner scanner = new Scanner(is)) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (scanner.hasNextInt()) {
                        pixelMatrix[y][x] = scanner.nextInt();
                    } else {
                        throw new IOException("Insufficient data in pbm ascii file");
                    }
                }
            }
        }
        return pixelMatrix;

    }

    @Override
    public int getMaxPixelValue() {
        return MAX_PIXEL_VALUE;
    }

    @Override
    public ArgbConvertStrategy getArgbConvertStrategy() {
        return new ArgbSingleBit();
    }
}
