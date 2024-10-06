package ch.supsi.dataaccess;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import ch.supsi.business.BMPImage;

public class BMPImageDataAccess{

    public BMPImage readBMP(String filePath) throws IOException {
        InputStream inputStream = BMPImage.class.getResourceAsStream(filePath);

        // Leggere il BMP header (14 byte)
        byte[] bmpHeader = new byte[14];
        inputStream.read(bmpHeader);

        // Leggere il DIB header (40 byte per BMP standard a 24 bit)
        byte[] dibHeader = new byte[40];
        inputStream.read(dibHeader);

        // Estrarre larghezza e altezza dell'immagine dal DIB header
        int width = ((dibHeader[7] & 0xFF) << 24) | ((dibHeader[6] & 0xFF) << 16) |
                ((dibHeader[5] & 0xFF) << 8)  | (dibHeader[4] & 0xFF);
        int height = ((dibHeader[11] & 0xFF) << 24) | ((dibHeader[10] & 0xFF) << 16) |
                ((dibHeader[9] & 0xFF) << 8)  | (dibHeader[8] & 0xFF);

        // Saltare fino all'inizio dei dati dei pixel (specificato nel BMP header)
        int pixelDataOffset = ((bmpHeader[13] & 0xFF) << 24) | ((bmpHeader[12] & 0xFF) << 16) |
                ((bmpHeader[11] & 0xFF) << 8)  | (bmpHeader[10] & 0xFF);
        inputStream.skip(pixelDataOffset - 14 - 40);

        // Buffer per i dati dei pixel (24-bit BMP: 3 byte per pixel)
        byte[] pixelData = new byte[width * height * 3];
        inputStream.read(pixelData);

        // Matrice per memorizzare i pixel ARGB
        int[][] pixels = new int[height][width];

        // Estrarre i pixel dai dati e salvarli come ARGB
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                int index = (x + y * width) * 3;

                // Leggere BGR
                int blue = pixelData[index] & 0xFF;
                int green = pixelData[index + 1] & 0xFF;
                int red = pixelData[index + 2] & 0xFF;

                // Convertire in ARGB (canale Alpha impostato a 255)
                int argb = (0xFF << 24) | (red << 16) | (green << 8) | blue;

                // Memorizzare il valore ARGB
                pixels[height - y - 1][x] = argb;
            }
        }

        inputStream.close();
        return new BMPImage(width, height, pixels);
    }
}
