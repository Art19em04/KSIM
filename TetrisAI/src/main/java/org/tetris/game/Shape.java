package org.tetris.game;

public class Shape {
    public static final int SHAPE_COUNT = 7;
    public static final String[] SHAPE_NAMES = {"I", "O", "T", "S", "Z", "J", "L"};

    public static final int[][][][] SHAPES = new int[SHAPE_COUNT][][][];

    static {
        int[][] shapeI = new int[][]{
                {0, 1}, {1, 1}, {2, 1}, {3, 1}
        };
        int[][] shapeO = new int[][]{
                {1, 0}, {2, 0},
                {1, 1}, {2, 1}
        };
        int[][] shapeT = new int[][]{
                        {1, 0},
                {0, 1}, {1, 1}, {2, 1}
        };
        int[][] shapeS = new int[][]{
                        {1, 0}, {2, 0},
                {0, 1}, {1, 1},
        };
        int[][] shapeZ = new int[][]{
                {0, 0}, {1, 0},
                        {1, 1}, {2, 1}
        };
        int[][] shapeJ = new int[][]{
                {0, 0},
                {0, 1}, {1, 1}, {2, 1}
        };
        int[][] shapeL = new int[][]{
                                {2, 1},
                {0, 1}, {1, 1}, {2, 0}
        };

        SHAPES[0] = generateRotations(shapeI); // I
        SHAPES[1] = generateRotations(shapeO); // O
        SHAPES[2] = generateRotations(shapeT); // T
        SHAPES[3] = generateRotations(shapeS); // S
        SHAPES[4] = generateRotations(shapeZ); // Z
        SHAPES[5] = generateRotations(shapeJ); // J
        SHAPES[6] = generateRotations(shapeL); // L
    }

    private static int[][][] generateRotations(int[][] base) {
        int[][][] tmp = new int[4][][];
        int count = 0;

        int[][] current = normalize(base);
        for (int r = 0; r < 4; r++) {
            if (!containsRotation(tmp, count, current)) {
                tmp[count] = current;
                count++;
            }
            current = normalize(rotate90(current));
        }

        int[][][] result = new int[count][][];
        for (int i = 0; i < count; i++) {
            result[i] = tmp[i];
        }
        return result;
    }

    private static int[][] rotate90(int[][] cells) {
        int[][] out = new int[cells.length][2];
        for (int i = 0; i < cells.length; i++) {
            int x = cells[i][0];
            int y = cells[i][1];
            int newX = -y;
            int newY = x;
            out[i][0] = newX;
            out[i][1] = newY;
        }
        return out;
    }

    private static int[][] normalize(int[][] cells) {
        int minX = cells[0][0];
        int minY = cells[0][1];
        for (int i = 1; i < cells.length; i++) {
            int x = cells[i][0];
            int y = cells[i][1];
            if (x < minX) {
                minX = x;
            }
            if (y < minY) {
                minY = y;
            }
        }
        int[][] out = new int[cells.length][2];
        for (int i = 0; i < cells.length; i++) {
            out[i][0] = cells[i][0] - minX;
            out[i][1] = cells[i][1] - minY;
        }
        return out;
    }

    private static boolean containsRotation(int[][][] list, int count, int[][] candidate) {
        for (int i = 0; i < count; i++) {
            if (sameCells(list[i], candidate)) {
                return true;
            }
        }
        return false;
    }

    private static boolean sameCells(int[][] a, int[][] b) {
        if (a.length != b.length) {
            return false;
        }
        boolean[] used = new boolean[b.length];
        for (int i = 0; i < a.length; i++) {
            int ax = a[i][0];
            int ay = a[i][1];
            boolean found = false;
            for (int j = 0; j < b.length; j++) {
                if (!used[j] && b[j][0] == ax && b[j][1] == ay) {
                    used[j] = true;
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
}
