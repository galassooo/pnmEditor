package ch.supsi.dataaccess;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

public abstract sealed class PNMWithMaxValueDataAccess extends PNMDataAccess
        permits PPMDataAccess, PGMDataAccess {

    private int maxValue;

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

    private void writeMaxValue(OutputStream os) throws IOException {
        os.write((maxValue + "\n").getBytes());
    }

    @Override
    protected final long[] @NotNull [] processBinary(InputStream is) throws IOException {
        readMaxValue(is);
        return readBinary(is);
    }

    @Override
    protected final long[] @NotNull [] processAscii(InputStream is) throws IOException {
        readMaxValue(is);
        return readAscii(is);
    }

    @Override
    protected final void writeBinary(OutputStream os, long[][] pixels, ExecutorService ex) throws IOException {
        writeMaxValue(os);
        writeBinaryPixels(os, pixels, ex);
    }

    @Override
    protected final void writeAscii(OutputStream os, long[][] pixels, ExecutorService ex) throws IOException {
        writeMaxValue(os);
        writeAsciiPixels(os, pixels, ex);
    }

    protected int getMaxValue() {
        return maxValue;
    }

    protected abstract long[] @NotNull [] readBinary(InputStream is) throws IOException;
    protected abstract long[] @NotNull [] readAscii(InputStream is) throws IOException;
    protected abstract void writeBinaryPixels(OutputStream os, long[][] pixels, ExecutorService ex) throws IOException;
    protected abstract void writeAsciiPixels(OutputStream os, long[][] pixels, ExecutorService ex) throws IOException;
}
