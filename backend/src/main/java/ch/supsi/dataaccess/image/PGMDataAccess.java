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

@ImageAccess(magicNumber = {"P2", "P5"})
public final class PGMDataAccess extends PNMWithMaxValueDataAccess {

    private static PGMDataAccess instance;

    private PGMDataAccess() {}

    public static PGMDataAccess getInstance() {
        if (instance == null) {
            instance = new PGMDataAccess();
        }
        return instance;
    }

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

    @Override
    protected void writeBinaryPixels(OutputStream os, long[][] pixels, ExecutorService executor) throws IOException {
        writePixels(os, pixels, executor, this::generateBinaryRowBufferPgm);
    }

    @Override
    protected void writeAsciiPixels(OutputStream os, long[][] pixels, ExecutorService executor) throws IOException {
        writePixels(os, pixels, executor, this::generateAsciiRowBufferPgm);
    }

    private byte[] generateBinaryRowBufferPgm(long[][] pixels, int row, int width) {
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

    private byte[] generateAsciiRowBufferPgm(long[][] pixels, int row, int width) {
        StringBuilder rowContent = new StringBuilder();
        for (int x = 0; x < width; x++) {
            rowContent.append(pixels[row][x]).append(" ");
        }
        rowContent.append("\n");
        return rowContent.toString().getBytes();
    }

    private void writePixels(OutputStream os, long[][] pixels, ExecutorService executor, RowGenerator generator) throws IOException {
        List<Future<byte[]>> futures = new ArrayList<>();
        for (int y = 0; y < pixels.length; y++) {
            final int row = y;
            futures.add(executor.submit(() -> generator.generateRow(pixels, row, pixels[0].length)));
        }

        for (Future<byte[]> future : futures) {
            try {
                os.write(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @FunctionalInterface
    private interface RowGenerator {
        byte[] generateRow(long[][] pixels, int row, int width) throws IOException;
    }

    @Override
    protected ConvertStrategy getArgbConvertStrategy() {
        return new SingleChannel(getMaxValue());
    }
}
