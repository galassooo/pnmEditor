package ch.supsi.dataaccess.image;

import ch.supsi.annotation.ImageAccess;
import ch.supsi.business.strategy.ConvertStrategy;
import ch.supsi.business.strategy.SingleChannel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 Provides functionality to read and write PPM image files in both ASCII (P2) and binary (P5) formats.
 * Implements a Singleton pattern to ensure a single instance is used throughout the application.
 * Extends {@link PNMWithMaxValueDataAccess} for shared functionality with PNM formats with maxvalue.
 */
@ImageAccess(magicNumber = {"P2", "P5"})
public final class PGMDataAccess extends PNMWithMaxValueDataAccess {

    private static PGMDataAccess instance;

    /**
     * Retrieves the Singleton instance of {@link PGMDataAccess}.
     *
     * @return the Singleton instance
     */
    public static PGMDataAccess getInstance() {
        if (instance == null) {
            instance = new PGMDataAccess();
        }
        return instance;
    }

    /**
     * Reads the binary (P5) PGM image data from the input stream and converts it into a 2D pixel array.
     *
     * @param is the {@link InputStream} to read the binary PGM data from
     * @return a 2D array of pixels where each pixel contains a grayscale value
     * @throws IOException if an error occurs while reading the binary data
     */
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
     * Reads the ASCII (P2) PGM image data from the input stream and converts it into a 2D pixel array.
     *
     * @param is the {@link InputStream} to read the ASCII PGM data from
     * @return a 2D array of pixels where each pixel contains a grayscale value
     * @throws IOException if an error occurs while reading the ASCII data
     */
    @Override
    protected long[][] readAscii(InputStream is) throws IOException {
        long[][] pixelMatrix = new long[height][width];
        try (Scanner scanner = new Scanner(is)) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (!scanner.hasNextInt()) {
                        throw new IOException("Invalid or missing data in ascii PGM file");
                    }
                    int grayValue = scanner.nextInt();
                    if (grayValue < 0 || grayValue > getMaxValue()) {
                        throw new IOException("gray pixel value out of range in binary pmg file");
                    }
                    pixelMatrix[y][x] = grayValue;
                }
            }
        }
        return pixelMatrix;
    }

    /**
     * Writes binary (P5) PGM pixel data to the output stream using an executor for parallel processing.
     *
     * @param os       the {@link OutputStream} to write to
     * @param pixels   the 2D array of pixels to write
     * @param executor the {@link ExecutorService} used for parallel processing
     * @throws IOException if an error occurs while writing
     */
    @Override
    protected void writeBinaryPixels(OutputStream os, long[][] pixels, ExecutorService executor) throws IOException {
        writePixels(os, pixels, executor, this::generateBinaryRowBuffer);
    }

    /**
     * Writes ASCII (P2) PGM pixel data to the output stream using an executor for parallel processing.
     *
     * @param os       the {@link OutputStream} to write to
     * @param pixels   the 2D array of pixels to write
     * @param executor the {@link ExecutorService} used for parallel processing
     * @throws IOException if an error occurs while writing
     */
    @Override
    protected void writeAsciiPixels(OutputStream os, long[][] pixels, ExecutorService executor) throws IOException {
        writePixels(os, pixels, executor, this::generateAsciiRowBuffer);
    }

    /**
     * Retrieves the conversion strategy for ARGB representation based on the maximum pixel value.
     *
     * @return the {@link ConvertStrategy} for ARGB conversion
     */
    @Override
    protected ConvertStrategy getArgbConvertStrategy() {
        return new SingleChannel(getMaxValue());
    }

    /**
     * Generates a binary row buffer for PGM data.
     *
     * @param pixels the 2D array of pixels
     * @param row    the row index to process
     * @param width  the width of the row
     * @return a byte array representing the binary data for the row
     */
    private byte[] generateBinaryRowBuffer(long[][] pixels, int row, int width) {
        boolean is16bit = getMaxValue() > 255;
        byte[] rowBuffer = new byte[width * (is16bit ? 2 : 1)];
        int index = 0;

        for (int x = 0; x < width; x++) {
            int grayscale = (int) pixels[row][x];
            if (is16bit) rowBuffer[index++] = (byte) (grayscale >> 8);
            rowBuffer[index++] = (byte) grayscale;
        }

        return rowBuffer;
    }

    /**
     * Generates an ASCII row buffer for PGM data.
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
}
