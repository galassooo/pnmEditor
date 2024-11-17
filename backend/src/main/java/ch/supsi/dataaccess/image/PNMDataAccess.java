package ch.supsi.dataaccess.image;

import ch.supsi.application.image.WritableImage;
import ch.supsi.business.image.*;
import ch.supsi.business.strategy.ConvertStrategy;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public abstract sealed class PNMDataAccess implements ImageDataAccess
        permits PBMDataAccess, PNMWithMaxValueDataAccess {

    private static final List<String> ALL_ASCII_HEADERS = List.of("P1", "P2", "P3");
    private static final List<String> ALL_BINARY_HEADERS = List.of("P4", "P5", "P6");

    protected int width;
    protected int height;
    private String format;

    protected abstract long[] @NotNull [] processBinary(InputStream is) throws IOException;

    protected abstract long[] @NotNull [] processAscii(InputStream is) throws IOException;

    protected abstract void writeAscii(OutputStream os, long[][] pixels, ExecutorService ex) throws IOException;

    protected abstract void writeBinary(OutputStream os, long[][] pixels, ExecutorService ex) throws IOException;

    protected abstract ConvertStrategy getArgbConvertStrategy();


    /**
     * Reads a PNM image file from the specified path and processes it as either binary or ASCII.
     *
     * @param path The path of the image file to read
     * @return An ImageBusiness object representing the processed image
     * @throws IOException if the file is not found or there is an error reading it
     */
    @Override
    public final @NotNull WritableImage read(String path) throws IOException {
        try (InputStream is = new FileInputStream(path)) { //da cambiare in FIS
            readHeader(is);
            long[][] processedMatrix = isBinaryFormat(format) ? processBinary(is) : processAscii(is);

            ImageAdapterInterface adapter = new ImageAdapter(getArgbConvertStrategy());

            ImageBuilder builder = new ImageBuilder()
                    .withPixels(processedMatrix)
                    .withFilePath(path)
                    .withMagicNumber(format)
                    .withImageAdapter(adapter)
                    .build();

            return new ImageBusiness(builder);
        }
    }

    /**
     * Writes the PNM image (header and pixel data) to the specified path.
     *
     * @param image The image to be written
     * @return The ImageBusinessInterface instance of the image written
     * @throws IOException if there's an error writing to the file
     */
    @Override
    public final WritableImage write(WritableImage image) throws IOException {
        String outputPath = image.getFilePath();
        File outputFile = new File(outputPath);

        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        if (!outputFile.canWrite()) {
            throw new IOException("Unable to write to file: " + outputPath);
        }

        try (FileOutputStream os = new FileOutputStream(outputFile)) {
            // Write the PNM header
            writeHeader(image, os);

            // Prepare to write pixel data
            ImageAdapterInterface adapter = new ImageAdapter(getArgbConvertStrategy());

            long[][] pixels = adapter.argbToRaw(image.getPixels());

            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            // Write pixel data based on format
            if (isBinaryFormat(image.getMagicNumber())) {
                writeBinary(os, pixels, executor);
            } else {
                writeAscii(os, pixels, executor);
            }

            executor.shutdown();
        }

        return image;
    }

    /**
     * Reads a single line from an InputStream.
     *
     * @param is InputStream to be read
     * @return The read line as a String
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

    protected boolean isBinaryFormat(String imgFormat) {
        return ALL_BINARY_HEADERS.contains(imgFormat);
    }



    protected void writePixels(OutputStream os, long[][] pixels, ExecutorService executor, RowGenerator generator) throws IOException {
        List<Future<byte[]>> futures = new ArrayList<>();
        for (int y = 0; y < pixels.length; y++) {
            final int row = y;
            futures.add(executor.submit(() -> generator.generateRow(pixels, row, pixels[0].length)));
        }

        for (Future<byte[]> future : futures) {
            try {
                os.write(future.get());
            } catch (InterruptedException | ExecutionException ignored) {
                //unable to test
            }
        }
    }

    /**
     * Reads the header of a generic PNM image.
     *
     * @param is InputStream containing the image bytes
     * @throws IOException if there's an error reading from the InputStream
     */
    private void readHeader(InputStream is) throws IOException {
        format = readLine(is).trim();
        if (!ALL_ASCII_HEADERS.contains(format) && !ALL_BINARY_HEADERS.contains(format)) {
            throw new IOException("Invalid format");
        }

        String dimensionLine;
        do {
            dimensionLine = readLine(is).trim();
        } while (dimensionLine.startsWith("#") || dimensionLine.isEmpty());

        String[] dimensions = dimensionLine.split("\\s+");
        if (dimensions.length != 2) {
            throw new IOException("Width or height is missing");
        }

        width = Integer.parseInt(dimensions[0]);
        height = Integer.parseInt(dimensions[1]);

        if (width <= 0 || height <= 0) {
            throw new IOException("Invalid dimension in header");
        }
    }

    /**
     * Writes the PNM image header to the specified path.
     *
     * @param image The image whose header will be written
     * @param os    The output stream to write the header to
     * @throws IOException if there's an error writing to the file
     */
    private void writeHeader(WritableImage image, OutputStream os) throws IOException {
        os.write((image.getMagicNumber() + "\n" + image.getWidth() + " " + image.getHeight() + "\n").getBytes());

    }

    /**
     * Functional interface for generating rows of pixel data.
     */
    @FunctionalInterface
    protected interface RowGenerator {
        byte[] generateRow(long[][] pixels, int row, int width) throws IOException;
    }
}
