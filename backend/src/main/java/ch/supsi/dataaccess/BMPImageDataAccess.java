package ch.supsi.dataaccess;

import java.io.IOException;
import java.io.InputStream;
import ch.supsi.business.BMPImage;

public class BMPImageDataAccess{

    public BMPImage readBMP(String filePath) throws IOException {
        InputStream inputStream = BMPImage.class.getResourceAsStream(filePath);

        byte[] bmpHeader = new byte[14];
        inputStream.read(bmpHeader);

        byte[] dibHeader = new byte[40];
        inputStream.read(dibHeader);

        int width = ((dibHeader[7] & 0xFF) << 24) | ((dibHeader[6] & 0xFF) << 16) |
                ((dibHeader[5] & 0xFF) << 8)  | (dibHeader[4] & 0xFF);
        int height = ((dibHeader[11] & 0xFF) << 24) | ((dibHeader[10] & 0xFF) << 16) |
                ((dibHeader[9] & 0xFF) << 8)  | (dibHeader[8] & 0xFF);

        int pixelDataOffset = ((bmpHeader[13] & 0xFF) << 24) | ((bmpHeader[12] & 0xFF) << 16) |
                ((bmpHeader[11] & 0xFF) << 8)  | (bmpHeader[10] & 0xFF);
        inputStream.skip(pixelDataOffset - 14 - 40);

        byte[] pixelData = new byte[width * height * 3];
        inputStream.read(pixelData);

        int[][] pixels = new int[height][width];

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                int index = (x + y * width) * 3;

                int blue = pixelData[index] & 0xFF;
                int green = pixelData[index + 1] & 0xFF;
                int red = pixelData[index + 2] & 0xFF;

                int argb = (0xFF << 24) | (red << 16) | (green << 8) | blue;

                pixels[height - y - 1][x] = argb;
            }
        }

        inputStream.close();
        return new BMPImage(width, height, pixels);
    }
}
