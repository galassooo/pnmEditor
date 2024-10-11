package ch.supsi.dataaccess;

import ch.supsi.business.Image.ImageBusiness;
import ch.supsi.business.strategy.ArgbSingleChannelNoLevels;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;


public final class PBMDataAccess extends PNMDataAccess {

    private static PBMDataAccess myself;

    public static PBMDataAccess getInstance() {
        if (myself == null) {
            myself = new PBMDataAccess();
        }
        return myself;
    }

    private PBMDataAccess() {

    }

    //ASDRUBALO elimina questo campo serve per testing e basta
    public int[][] originalMatrix;

    @Override
    public @NotNull ImageBusiness processBinary(InputStream is) throws IOException {
        int[][] pixelMatrix = new int[height][width];
        int bytesPerRow = (width + 7) / 8; // Numero di byte necessari per rappresentare una riga, inclusi eventuali padding

        for (int y = 0; y < height; y++) {
            for (int byteIndex = 0; byteIndex < bytesPerRow; byteIndex++) {
                int byteValue = is.read();
                if (byteValue == -1) {
                    throw new IOException("Dati insufficienti nel file PBM.");
                }

                // Decodifica ogni bit del byte e assegna il pixel corrispondente
                for (int bit = 0; bit < 8 && (byteIndex * 8 + bit) < width; bit++) {
                    int x = byteIndex * 8 + bit;
                    pixelMatrix[y][x] = (byteValue >> (7 - bit)) & 1;
                }
            }
        }
        //ASDRUBALO
        originalMatrix = pixelMatrix;

        return new ImageBusiness(pixelMatrix, width, height, new ArgbSingleChannelNoLevels());
    }

    @Override
    public @NotNull ImageBusiness processAscii(InputStream is)throws IOException {
        int[][] pixelMatrix = new int[height][width];
        try (Scanner scanner = new Scanner(is)) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (scanner.hasNextInt()) {
                        pixelMatrix[y][x] = scanner.nextInt();
                    } else {
                        throw new IOException("Formato del file PBM ASCII non valido o dati mancanti.");
                    }
                }
            }
        }

        //ASDRUBALO
        originalMatrix = pixelMatrix;
        return new ImageBusiness(pixelMatrix, width, height, new ArgbSingleChannelNoLevels());

    }
}