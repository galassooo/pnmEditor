package ch.supsi.business.strategy;

public class ArgbSingleChannelNoLevels implements ArgbConvertStrategy {

    private static final int ARGB_WHITE = 0xFFFFFFFF; // Bianco opaco
    private static final int ARGB_BLACK = 0xFF000000; // Nero opaco

    @Override
    public int[][] toArgb(int[][] originalMatrix) {
        int height = originalMatrix.length;
        if(height == 0){
            return new int[0][0];
        }
        int width = originalMatrix[0].length;

        int[][] argbMatrix = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                argbMatrix[y][x] = convertToArgb(originalMatrix[y][x]);
            }
        }

        return argbMatrix;
    }

    private int convertToArgb(int value) {
        return value == 1 ? ARGB_BLACK : ARGB_WHITE;
    }
}


