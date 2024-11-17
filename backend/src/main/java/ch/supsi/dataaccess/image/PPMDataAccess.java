package ch.supsi.dataaccess.image;

import ch.supsi.annotation.ImageAccess;
import ch.supsi.business.strategy.ConvertStrategy;
import ch.supsi.business.strategy.ThreeChannel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Provides functionality to read and write PPM image files in both ASCII (P3) and binary (P6) formats.
 * Implements a Singleton pattern to ensure a single instance is used across the application.
 * Extends {@link PNMWithMaxValueDataAccess} for shared functionality with PNM formats.
 */
@ImageAccess(magicNumber = {"P3", "P6"})
public final class PPMDataAccess extends PNMWithMaxValueDataAccess {

    private static PPMDataAccess instance;

    /**
     * Retrieves the Singleton instance of the {@link PPMDataAccess}.
     *
     * @return the Singleton instance
     */
    public static PPMDataAccess getInstance() {
        if (instance == null) {
            instance = new PPMDataAccess();
        }
        return instance;
    }

    /**
     * Reads the binary (P6) PPM image data from the input stream and converts it into a 2D pixel array.
     *
     * @param is the {@link InputStream} to read the binary PPM data from
     * @return a 2D array of pixels where each pixel contains combined RGB data
     * @throws IOException if an error occurs while reading the binary data
     */
    @Override
    protected long[] @NotNull [] readBinary(InputStream is) throws IOException {
        long[][] pixelMatrix = new long[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = readChannel(is);
                int green = readChannel(is);
                int blue = readChannel(is);

                if (red == -1 || green == -1 || blue == -1) {
                    throw new IOException("Insufficient data in binary ppm file");
                }

                pixelMatrix[y][x] = combineRgb(red, green, blue);
            }
        }
        return pixelMatrix;
    }

    /**
     * Reads an individual color channel value from the input stream.
     *
     * @param is the {@link InputStream} to read the channel data from
     * @return the channel value as an integer
     * @throws IOException if an error occurs while reading the channel data
     */
    private int readChannel(InputStream is) throws IOException {
        return super.getMaxValue() <= 255 ? is.read() : (is.read() << 8) | is.read();
    }

    /**
     * Combines red, green, and blue channel values into a single long value.
     *
     * @param red   the red channel value
     * @param green the green channel value
     * @param blue  the blue channel value
     * @return a long value representing the combined RGB data
     */
    private long combineRgb(int red, int green, int blue) {
        return super.getMaxValue() <= 255
                ? ((long) red << 16) | ((long) green << 8) | blue
                : ((long) red << 32) | ((long) green << 16) | blue;
    }

    /**
     * Reads the ASCII (P3) PPM image data from the input stream and converts it into a 2D pixel array.
     *
     * @param is the {@link InputStream} to read the ASCII PPM data from
     * @return a 2D array of pixels where each pixel contains combined RGB data
     * @throws IOException if an error occurs while reading the ASCII data
     */
    @Override
    protected long[] @NotNull [] readAscii(InputStream is) throws IOException {
        long[][] pixelMatrix = new long[height][width];
        try (Scanner scanner = new Scanner(is)) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int[] rgb = readRgb(scanner);
                    pixelMatrix[y][x] = combineRgb(rgb[0], rgb[1], rgb[2]);
                }
            }
        }
        return pixelMatrix;
    }

    /**
     * Writes binary (P6) PPM pixel data to the output stream.
     *
     * @param os       the {@link OutputStream} to write to
     * @param pixels   the 2D array of pixels to write
     * @param executor the {@link ExecutorService} used for parallel processing
     * @throws IOException if an error occurs while writing
     */
    @Override
    protected void writeBinaryPixels(OutputStream os, long[][] pixels, ExecutorService executor) throws IOException {
        List<Future<byte[]>> futures = createRowTasks(pixels, executor, this::generateBinaryRowBuffer);
        writeFuturesToStream(futures, os);
    }

    /**
     * Writes ASCII (P3) PPM pixel data to the output stream.
     *
     * @param os       the {@link OutputStream} to write to
     * @param pixels   the 2D array of pixels to write
     * @param executor the {@link ExecutorService} used for parallel processing
     * @throws IOException if an error occurs while writing
     */
    @Override
    protected void writeAsciiPixels(OutputStream os, long[][] pixels, ExecutorService executor) throws IOException {
        List<Future<byte[]>> futures = createRowTasks(pixels, executor, this::generateAsciiRowBuffer);
        writeFuturesToStream(futures, os);
    }


    /**
     * Retrieves the conversion strategy for ARGB representation based on the maximum pixel value.
     *
     * @return the {@link ConvertStrategy} for ARGB conversion
     */
    @Override
    protected ConvertStrategy getArgbConvertStrategy() {
        return new ThreeChannel(super.getMaxValue());
    }


    /**
     * Creates tasks for generating pixel rows in parallel using the provided executor and row generator.
     *
     * @param pixels   the 2D array of pixels to process
     * @param executor the {@link ExecutorService} to use for parallel task execution
     * @param generator a {@link RowGenerator} to generate each row
     * @return a {@link List} of {@link Future} objects representing the tasks for each row
     */
    private List<Future<byte[]>> createRowTasks(long[][] pixels, ExecutorService executor, RowGenerator generator) {
        List<Future<byte[]>> futures = new ArrayList<>();
        int height = pixels.length;
        for (int y = 0; y < height; y++) {
            final int row = y;
            futures.add(executor.submit(() -> generator.generateRow(pixels, row, pixels[0].length)));
        }
        return futures;
    }


    /**
     * Generates a binary buffer for a single row of pixels.
     *
     * @param pixels the 2D array of pixels
     * @param row    the row index to process
     * @param width  the width of the row
     * @return a byte array representing the binary data for the row
     */
    private byte[] generateBinaryRowBuffer(long[][] pixels, int row, int width) {
        boolean is16bit = super.getMaxValue() > 255;
        int m = is16bit ? 2 : 1;
        byte[] rowBuffer = new byte[width * 3 * m];
        int index = 0;

        for (int x = 0; x < width; x++) {
            long pixel = pixels[row][x];
            index = writeRgbToBuffer(rowBuffer, index, pixel, is16bit);
        }

        return rowBuffer;
    }

    /**
     * Generates an ASCII buffer for a single row of pixels.
     *
     * @param pixels the 2D array of pixels
     * @param row    the row index to process
     * @param width  the width of the row
     * @return a byte array representing the ASCII data for the row
     */
    private byte[] generateAsciiRowBuffer(long[][] pixels, int row, int width) {
        boolean is16bit = super.getMaxValue() > 255;
        StringBuilder rowContent = new StringBuilder();

        for (int x = 0; x < width; x++) {
            long pixel = pixels[row][x];
            int red = (int) ((pixel >> (is16bit ? 32 : 16)) & (is16bit ? 0xFFFF : 0xFF));
            int green = (int) ((pixel >> (is16bit ? 16 : 8)) & (is16bit ? 0xFFFF : 0xFF));
            int blue = (int) (pixel & (is16bit ? 0xFFFF : 0xFF));

            rowContent.append(red).append(" ").append(green).append(" ").append(blue).append(" ");
        }

        rowContent.append("\n");
        return rowContent.toString().getBytes();
    }

    /**
     * Writes the RGB data of a pixel to the buffer.
     *
     * @param buffer   the byte array buffer to write to
     * @param index    the starting index in the buffer
     * @param pixel    the pixel containing combined RGB data
     * @param is16bit  whether the pixel data is 16-bit
     * @return the updated index after writing the pixel data
     */
    private int writeRgbToBuffer(byte[] buffer, int index, long pixel, boolean is16bit) {
        int[] rgb = extractRgb(pixel, is16bit);
        for (int color : rgb) {
            if (is16bit) {
                buffer[index++] = (byte) (color >> 8);
            }
            buffer[index++] = (byte) color;
        }
        return index;
    }

    /**
     * Extracts the RGB components from a pixel.
     *
     * @param pixel   the pixel containing combined RGB data
     * @param is16bit whether the pixel data is 16-bit
     * @return an array of integers containing the red, green, and blue components
     */
    private int[] extractRgb(long pixel, boolean is16bit) {
        int shift = is16bit ? 16 : 8;
        return new int[]{
                (int) ((pixel >> (2 * shift)) & (is16bit ? 0xFFFF : 0xFF)),
                (int) ((pixel >> shift) & (is16bit ? 0xFFFF : 0xFF)),
                (int) (pixel & (is16bit ? 0xFFFF : 0xFF))
        };
    }

    /**
     * Writes the results of row generation tasks to the output stream.
     *
     * @param futures the list of {@link Future} objects representing row data tasks
     * @param os      the {@link OutputStream} to write the data to
     * @throws IOException if an error occurs during writing
     */
    private void writeFuturesToStream(List<Future<byte[]>> futures, OutputStream os) throws IOException {
        for (Future<byte[]> future : futures) {
            try {
                os.write(future.get());
            } catch (InterruptedException | ExecutionException ignored) {

            }
            //NON RIESCO A TESTARLO!!!!!!!!
        }
    }

    /**
     * Reads an RGB triplet from the scanner.
     *
     * @param scanner the {@link Scanner} to read data from
     * @return an array containing the red, green, and blue values
     * @throws IOException if the data is invalid or out of range
     */
    private int[] readRgb(Scanner scanner) throws IOException {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            if (!scanner.hasNextInt()) throw new IOException("Invalid color data in ASCII PPM file");
            rgb[i] = scanner.nextInt();
            if (rgb[i] < 0 || rgb[i] > super.getMaxValue())
                throw new IOException("Color value out of range in ASCII PPM file");
        }
        return rgb;
    }
}
