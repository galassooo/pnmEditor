package ch.supsi.dataaccess;

import ch.supsi.business.strategy.ArgbConvertStrategy;
import ch.supsi.business.strategy.ArgbSingleBit;
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

public final class PBMDataAccess extends PNMDataAccess {

    /* self reference */
    private static PBMDataAccess myself;


    /* singleton */
    public static PBMDataAccess getInstance() {
        if (myself == null) {
            myself = new PBMDataAccess();
        }
        return myself;
    }

    /* constructor */
    private PBMDataAccess() {}

    /**
     * Processes binary PBM image data
     *
     * @param is InputStream containing binary PBM image data
     * @return ImageBusiness instance representing the decoded image
     * @throws IOException if the pixels don't match the width/height
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
     * Processes ascii PBM image data
     *
     * @param is InputStream containing ASCII PBM image data
     * @return ImageBusiness instance representing the decoded image
     * @throws IOException if there is an error in reading the ASCII data
     */
    @Override
    protected long[] @NotNull [] processAscii(InputStream is)throws IOException {
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
    protected void writeBinary(OutputStream os, long[][] pixels, ExecutorService executor) throws IOException {
        writePixels(os, pixels, executor, this::generateBinaryRowBuffer);
    }

    @Override
    protected void writeAscii(OutputStream os, long[][] pixels, ExecutorService executor) throws IOException {
        writePixels(os, pixels, executor, this::generateAsciiRowBufferPbm);
    }

    private byte[] generateAsciiRowBufferPbm(long[][] pixels, int row, int width) {
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

    @FunctionalInterface
    private interface RowGenerator {
        byte[] generateRow(long[][] pixels, int row, int width) throws IOException;
    }

    @Override
    protected ArgbConvertStrategy getArgbConvertStrategy() {
        return new ArgbSingleBit();
    }
}

