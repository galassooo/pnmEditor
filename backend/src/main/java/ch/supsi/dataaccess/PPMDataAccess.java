package ch.supsi.dataaccess;

import ch.supsi.business.Image.ImageBusiness;
import ch.supsi.business.strategy.ArgbThreeChannel;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public final class PPMDataAccess extends PNMDataAccess {

    /* self reference */
    private static PPMDataAccess myself;

    /* instance field */
    private int maxColorValue;

    // ASDRUBALO da eliminare dopo testing
    public long[][] originalMatrix;


    /* singleton */
    public static PPMDataAccess getInstance() {
        if (myself == null) {
            myself = new PPMDataAccess();
        }
        return myself;
    }

    /* constructor */
    private PPMDataAccess() {
    }

    /**
     * Reads the max color value from the PPM file header
     * @param is inputStream
     * @throws IOException when the maxColorValue is missing or has a wrong format
     */
    public void readMaxValue(InputStream is) throws IOException {
        String maxColorLine = readLine(is).trim();

        try {
            // parse and check the max color value (non differenzia 8-16 bit per ora)
            maxColorValue = Integer.parseInt(maxColorLine);

            if (maxColorValue <= 0 || maxColorValue > 65535) {
                throw new IOException("Invalid max color value in header");
            }
        } catch (NumberFormatException e) {
            throw new IOException("Max color value is missing or invalid in header");
        }
    }

    /**
     * Reads a binary PPM image supporting both 8-bit and 16-bit pixel formats.
     * @param is InputStream
     * @return ImageBusiness instance representing the decoded image
     * @throws IOException when pixel values are out of range or the file is corrupted
     */
    @Override
    public @NotNull ImageBusiness processBinary(InputStream is) throws IOException {
        readMaxValue(is);
        long[][] pixelMatrix = new long[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = readChannel(is);
                int green = readChannel(is);
                int blue = readChannel(is);

                if (red == -1 || green == -1 || blue == -1) {
                    throw new IOException("Insufficient data in binary ppm file");
                }

                long rgbValue = combineRgb(red, green, blue);
                pixelMatrix[y][x] = rgbValue;
            }
        }

        originalMatrix = pixelMatrix;
        return new ImageBusiness(pixelMatrix, width, height, maxColorValue, new ArgbThreeChannel());
    }


    /**
     * reads a single channel (both 16 and 8 bits)
     * @param is InputStream
     * @return an int representing an rgb value
     * @throws IOException if it cannot read from inputStream
     */
    private int readChannel(InputStream is) throws IOException {
        return maxColorValue <= 255 ? is.read() : (is.read() << 8) | is.read();
    }

    /**
     * combines RGB channel to create an int
     * @param red red channel value
     * @param green green channel value
     * @param blue blue channel value
     * @return an int representing all three channels
     */
    private long combineRgb(int red, int green, int blue) {
        return maxColorValue <= 255
                ? ((long) red << 16) | ((long) green << 8) | blue
                : ((long) red << 32) | ((long) green << 16) | blue;
    }


    /**
     * Reads an ASCII PPM image, supporting both 8-bit and 16-bit pixel formats.
     * @param is InputStream
     * @return ImageBusiness instance representing the decoded image
     * @throws IOException when pixel values are out of range or the file is corrupted
     */
    @Override
    public @NotNull ImageBusiness processAscii(InputStream is) throws IOException {
        readMaxValue(is);
        long[][] pixelMatrix = new long[height][width];

        try (Scanner scanner = new Scanner(is)) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int[] rgb = readRgb(scanner); // Legge e verifica i valori RGB
                    pixelMatrix[y][x] = combineRgb(rgb[0], rgb[1], rgb[2]);
                }
            }
        }

        originalMatrix = pixelMatrix; // ASDRUBALO per testing
        return new ImageBusiness(pixelMatrix, width, height, maxColorValue, new ArgbThreeChannel());
    }
    /**
     * Legge e verifica i valori RGB da Scanner
     */
    private int[] readRgb(Scanner scanner) throws IOException {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            if (!scanner.hasNextInt()) {
                throw new IOException("Missing or invalid color data in ascii ppm file");
            }
            rgb[i] = scanner.nextInt();
            if (rgb[i] < 0 || rgb[i] > maxColorValue) {
                throw new IOException("color value out of range in ascii ppm file");
            }
        }
        return rgb;
    }
}
