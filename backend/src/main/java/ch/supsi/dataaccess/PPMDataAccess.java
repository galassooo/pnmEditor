package ch.supsi.dataaccess;

import ch.supsi.business.strategy.ArgbConvertStrategy;
import ch.supsi.business.strategy.ArgbThreeChannel;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public final class PPMDataAccess extends PNMWithMaxValueDataAccess {

    private static PPMDataAccess instance;

    public static PPMDataAccess getInstance() {
        if (instance == null) {
            instance = new PPMDataAccess();
        }
        return instance;
    }

    private PPMDataAccess() {}

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

    private int readChannel(InputStream is) throws IOException {
        return super.getMaxValue() <= 255 ? is.read() : (is.read() << 8) | is.read();
    }

    private long combineRgb(int red, int green, int blue) {
        return super.getMaxValue() <= 255
                ? ((long) red << 16) | ((long) green << 8) | blue
                : ((long) red << 32) | ((long) green << 16) | blue;
    }

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

    @Override
    protected void writeBinaryPixels(OutputStream os, long[][] pixels, ExecutorService executor) throws IOException {
        List<Future<byte[]>> futures = createRowTasks(pixels, executor, this::generateBinaryRowBuffer);
        writeFuturesToStream(futures, os);
    }

    @Override
    protected void writeAsciiPixels(OutputStream os, long[][] pixels, ExecutorService executor) throws IOException {
        List<Future<byte[]>> futures = createRowTasks(pixels, executor, this::generateAsciiRowBuffer);
        writeFuturesToStream(futures, os);
    }

    private List<Future<byte[]>> createRowTasks(long[][] pixels, ExecutorService executor, RowGenerator generator) {
        List<Future<byte[]>> futures = new ArrayList<>();
        int height = pixels.length;
        for (int y = 0; y < height; y++) {
            final int row = y;
            futures.add(executor.submit(() -> generator.generateRow(pixels, row, pixels[0].length)));
        }
        return futures;
    }

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

    private int[] extractRgb(long pixel, boolean is16bit) {
        int shift = is16bit ? 16 : 8;
        return new int[]{
                (int) ((pixel >> (2 * shift)) & (is16bit ? 0xFFFF : 0xFF)),
                (int) ((pixel >> shift) & (is16bit ? 0xFFFF : 0xFF)),
                (int) (pixel & (is16bit ? 0xFFFF : 0xFF))
        };
    }

    private void writeFuturesToStream(List<Future<byte[]>> futures, OutputStream os) throws IOException {
        for (Future<byte[]> future : futures) {
            try {
                os.write(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            //NON RIESCO A TESTARLO!!!!!!!!
        }
    }

    private int[] readRgb(Scanner scanner) throws IOException {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            if (!scanner.hasNextInt()) throw new IOException("Invalid color data in ASCII PPM file");
            rgb[i] = scanner.nextInt();
            if (rgb[i] < 0 || rgb[i] > super.getMaxValue()) throw new IOException("Color value out of range in ASCII PPM file");
        }
        return rgb;
    }

    @FunctionalInterface
    private interface RowGenerator {
        byte[] generateRow(long[][] pixels, int row, int width) throws IOException;
    }

    @Override
    protected ArgbConvertStrategy getArgbConvertStrategy() {
        return new ArgbThreeChannel(super.getMaxValue());
    }
}
