package ch.supsi.dataaccess.image;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

/**
 * Abstract base class for handling PNM image formats that include a "max value" field in their headers.
 * This class extends {@link PNMDataAccess} and provides shared functionality for formats like PPM and PGM,
 * which support pixel values up to a defined maximum.
 *
 * @see PNMDataAccess
 * @see PPMDataAccess
 * @see PGMDataAccess
 */
public abstract sealed class PNMWithMaxValueDataAccess extends PNMDataAccess
        permits PPMDataAccess, PGMDataAccess {

    private int maxValue = 255; //default

    /**
     * Processes binary PNM data, reading the max value from the header and delegating to the specific implementation.
     *
     * @param is the {@link InputStream} containing the binary PNM data
     * @return a 2D array of pixels
     * @throws IOException if an error occurs while reading the data
     */
    @Override
    protected final long[] @NotNull [] processBinary(InputStream is) throws IOException {
        readMaxValue(is);
        return readBinary(is);
    }

    /**
     * Processes ASCII PNM data, reading the max value from the header and delegating to the specific implementation.
     *
     * @param is the {@link InputStream} containing the ASCII PNM data
     * @return a 2D array of pixels
     * @throws IOException if an error occurs while reading the data
     */
    @Override
    protected final long[] @NotNull [] processAscii(InputStream is) throws IOException {
        readMaxValue(is);
        return readAscii(is);
    }

    /**
     * Writes binary PNM data, including the max value in the header, and delegates pixel writing to the specific implementation.
     *
     * @param os     the {@link OutputStream} to write to
     * @param pixels the 2D array of pixels to write
     * @param ex     the {@link ExecutorService} for parallel processing
     * @throws IOException if an error occurs while writing the data
     */
    @Override
    protected final void writeBinary(OutputStream os, long[][] pixels, ExecutorService ex) throws IOException {
        writeMaxValue(os);
        writeBinaryPixels(os, pixels, ex);
    }


    /**
     * Writes ASCII PNM data, including the max value in the header, and delegates pixel writing to the specific implementation.
     *
     * @param os     the {@link OutputStream} to write to
     * @param pixels the 2D array of pixels to write
     * @param ex     the {@link ExecutorService} for parallel processing
     * @throws IOException if an error occurs while writing the data
     */
    @Override
    protected final void writeAscii(OutputStream os, long[][] pixels, ExecutorService ex) throws IOException {
        writeMaxValue(os);
        writeAsciiPixels(os, pixels, ex);
    }

    /**
     * Reads binary pixel data after the header. To be implemented by subclasses.
     *
     * @param is the {@link InputStream} to read from
     * @return a 2D array of pixels
     * @throws IOException if an error occurs while reading the data
     */
    protected abstract long[] @NotNull [] readBinary(InputStream is) throws IOException;

    /**
     * Reads ASCII pixel data after the header. To be implemented by subclasses.
     *
     * @param is the {@link InputStream} to read from
     * @return a 2D array of pixels
     * @throws IOException if an error occurs while reading the data
     */
    protected abstract long[] @NotNull [] readAscii(InputStream is) throws IOException;

    /**
     * Writes binary pixel data. To be implemented by subclasses.
     *
     * @param os     the {@link OutputStream} to write to
     * @param pixels the 2D array of pixels to write
     * @param ex     the {@link ExecutorService} for parallel processing
     * @throws IOException if an error occurs while writing the data
     */
    protected abstract void writeBinaryPixels(OutputStream os, long[][] pixels, ExecutorService ex) throws IOException;

    /**
     * Writes ASCII pixel data. To be implemented by subclasses.
     *
     * @param os     the {@link OutputStream} to write to
     * @param pixels the 2D array of pixels to write
     * @param ex     the {@link ExecutorService} for parallel processing
     * @throws IOException if an error occurs while writing the data
     */
    protected abstract void writeAsciiPixels(OutputStream os, long[][] pixels, ExecutorService ex) throws IOException;

    /**
     * Retrieves the maximum pixel value read from the PNM header.
     *
     * @return the max pixel value
     */
    protected int getMaxValue() {
        return maxValue;
    }

    /**
     * Reads the maximum pixel value from the PNM header.
     *
     * @param is the {@link InputStream} to read from
     * @throws IOException if the max value is invalid or out of range
     */
    private void readMaxValue(InputStream is) throws IOException {
        String line = readLine(is);
        try {
            maxValue = Integer.parseInt(line.trim());
            if (maxValue <= 0 || maxValue > 65535) {
                throw new IOException("Max value out of range in header");
            }
        } catch (NumberFormatException e) {
            throw new IOException("Max value is invalid in header");
        }
    }

    /**
     * writes the max value to the output stream
     * @param os the {@link OutputStream} to be written
     * @throws IOException if an error occurs during writing operation
     */
    private void writeMaxValue(OutputStream os) throws IOException {
        os.write((maxValue + "\n").getBytes());
    }
}
