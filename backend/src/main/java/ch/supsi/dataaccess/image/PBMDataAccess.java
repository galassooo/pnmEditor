package ch.supsi.dataaccess.image;

import ch.supsi.annotation.ImageAccess;
import ch.supsi.business.strategy.ConvertStrategy;
import ch.supsi.business.strategy.SingleBit;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

/**
 * Provides functionality to read and write PBM image files in both ASCII (P1) and binary (P4) formats.
 * Implements a Singleton pattern to ensure a single instance is used across the application.
 * Extends {@link PNMDataAccess} for shared functionality with PNM formats.
 */

@ImageAccess(magicNumber = {"P1", "P4"})
public final class PBMDataAccess extends PNMDataAccess {

    private static PBMDataAccess myself;

    /**
     * Retrieves the Singleton instance of {@link PBMDataAccess}.
     *
     * @return the Singleton instance
     */
    public static PBMDataAccess getInstance() {
        if (myself == null) {
            myself = new PBMDataAccess();
        }
        return myself;
    }

    /**
     * Processes binary (P4) PBM image data from the input stream and converts it into a 2D pixel array.
     *
     * @param is the {@link InputStream} containing the binary PBM data
     * @return a 2D array of pixels where each pixel is either 0 (white) or 1 (black)
     * @throws IOException if the input stream contains insufficient data or if there's an error while reading
     */
    @Override
    protected long[] @NotNull [] processBinary(InputStream is) throws IOException {
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
     * Processes ASCII (P1) PBM image data from the input stream and converts it into a 2D pixel array.
     *
     * @param is the {@link InputStream} containing the ASCII PBM data
     * @return a 2D array of pixels where each pixel is either 0 (white) or 1 (black)
     * @throws IOException if the input stream contains invalid or insufficient data
     */
    @Override
    protected long[] @NotNull [] processAscii(InputStream is) throws IOException {
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

    /**
     * Writes binary (P4) PBM pixel data to the output stream using an executor for parallel processing.
     *
     * @param os       the {@link OutputStream} to write to
     * @param pixels   the 2D array of pixels to write
     * @param executor the {@link ExecutorService} used for parallel processing
     * @throws IOException if an error occurs while writing
     */
    @Override
    protected void writeBinary(OutputStream os, long[][] pixels, ExecutorService executor) throws IOException {
        writePixels(os, pixels, executor, this::generateBinaryRowBuffer);
    }

    /**
     * Writes ASCII (P1) PBM pixel data to the output stream using an executor for parallel processing.
     *
     * @param os       the {@link OutputStream} to write to
     * @param pixels   the 2D array of pixels to write
     * @param executor the {@link ExecutorService} used for parallel processing
     * @throws IOException if an error occurs while writing
     */
    @Override
    protected void writeAscii(OutputStream os, long[][] pixels, ExecutorService executor) throws IOException {
        writePixels(os, pixels, executor, this::generateAsciiRowBuffer);
    }

    /**
     * Retrieves the conversion strategy for ARGB representation based on PBM (single bit) format.
     *
     * @return the {@link ConvertStrategy} for ARGB conversion
     */
    @Override
    protected ConvertStrategy getArgbConvertStrategy() {
        return new SingleBit();
    }

    /**
     * Generates an ASCII row buffer for PBM data.
     *
     * @param pixels the 2D array of pixels
     * @param row    the row index to process
     * @param width  the width of the row
     * @return a byte array representing the ASCII data for the row
     */
    private byte[] generateAsciiRowBuffer(long[][] pixels, int row, int width) {
        StringBuilder rowContent = new StringBuilder();
        for (int x = 0; x < width; x++) {
            rowContent.append(pixels[row][x]).append(" ");
        }
        rowContent.append("\n");
        return rowContent.toString().getBytes();
    }

    /**
     * Generates a binary row buffer for PBM data.
     *
     * @param pixels the 2D array of pixels
     * @param row    the row index to process
     * @param width  the width of the row
     * @return a byte array representing the binary data for the row
     */
    private byte[] generateBinaryRowBuffer(long[][] pixels, int row, int width) {
        int byteWidth = (width + 7) / 8;
        byte[] rowBuffer = new byte[byteWidth];
        int index = 0;

        for (int x = 0; x < width; x++) {
            int bitPosition = 7 - (x % 8);
            if (pixels[row][x] == 1) {
                rowBuffer[index] |= (byte) (1 << bitPosition); //posiziona il bit nella posizione corretta
            }
            if (bitPosition == 0) index++; //se abbiamo scritto il byte andiamo al prox
        }

        return rowBuffer;
    }
}

