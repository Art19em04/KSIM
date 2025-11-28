package org.tetris;

import org.tetris.game.Board;
import org.tetris.game.Shape;

import static org.tetris.Constants.BOARD_WIDTH;

public class AI {

    public static double evaluateBoard(int[][] board, int linesCleared, double[] weights) {
        double wLines = weights[0];
        double wHeight = weights[1];
        double wHoles = weights[2];
        double wBump = weights[3];

        int[] heights = Board.getColumnsHeights(board);

        int countHeights = 0;
        for (int x = 0; x < BOARD_WIDTH; x++) {
            countHeights += heights[x];
        }

        int bumpiness = 0;
        for (int x = 0; x < BOARD_WIDTH - 1; x++) {
            int h1 = heights[x];
            int h2 = heights[x + 1];
            int diff = h1 - h2;
            if (diff < 0) {
                diff = -diff;
            }
            bumpiness += diff;
        }

        int holes = Board.countHoles(board);

        return wLines * linesCleared
                + wHeight * countHeights
                + wHoles * holes
                + wBump * bumpiness;
    }

    private static int getPieceWidth(int[][] cells) {
        int minX = cells[0][0];
        int maxX = cells[0][0];

        for (int i = 0; i < cells.length; i++) {
            int x = cells[i][0];
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
        }
        return maxX - minX + 1;
    }

    private static int findDropY(int[][] board, int[][] cells, int xStart) {
        int y = 0;
        while (true) {
            int nextY = y + 1;
            if (Board.collision(board, cells, xStart, nextY)) {
                break;
            }
            y = nextY;
        }
        return y;
    }

    public static Move chooseBestMove(int[][] board, int shapeIndex, double[] weights) {
        double bestScore = Double.NEGATIVE_INFINITY;
        Move bestMove = null;

        int[][][] rotations = Shape.SHAPES[shapeIndex];

        for (int rotIndex = 0; rotIndex < rotations.length; rotIndex++) {
            int[][] cells = rotations[rotIndex];

            int width = getPieceWidth(cells);
            int maxXPos = BOARD_WIDTH - width;

            for (int xPos = 0; xPos <= maxXPos; xPos++) {
                int yPos = findDropY(board, cells, xPos);

                if (Board.collision(board, cells, xPos, yPos)) {
                    continue;
                }

                Board.BoardResult res = Board.lockPiece(board, shapeIndex, cells, xPos, yPos);
                double score = evaluateBoard(res.board, res.linesCleared, weights);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new Move(rotIndex, xPos, yPos);
                }
            }
        }

        return bestMove;
    }

    public static class Move {
        public final int rotationIndex;
        public final int x;
        public final int y;

        public Move(int rotationIndex, int x, int y) {
            this.rotationIndex = rotationIndex;
            this.x = x;
            this.y = y;
        }
    }
}
