package org.tetris.ga;

import org.tetris.AI;
import org.tetris.game.Board;
import org.tetris.game.Shape;

import java.util.Random;

public class GameSimulator {

    public static final int MAX_PIECES = 3000;

    public static int playOneGame(double[] weights, long seed) {
        Random rnd = new Random(seed);
        int[][] board = Board.createEmptyBoard();

        int totalLines = 0;
        int pieces = 0;

        while (pieces < MAX_PIECES) {
            int shapeIndex = rnd.nextInt(Shape.SHAPE_COUNT);

            AI.Move move = AI.chooseBestMove(board, shapeIndex, weights);
            if (move == null) {
                break;
            }

            int[][] cells = Shape.SHAPES[shapeIndex][move.rotationIndex];

            if (Board.collision(board, cells, move.x, move.y)) {
                break;
            }

            Board.BoardResult result =
                    Board.lockPiece(board, shapeIndex, cells, move.x, move.y);

            board = result.board;

            totalLines += result.linesCleared;
            pieces++;
        }

        return totalLines;
    }

    public static double fitness(double[] weights, int games, long baseSeed) {
        int sum = 0;
        for (int i = 0; i < games; i++) {
            long seed = baseSeed + i;
            sum += playOneGame(weights, seed);
        }
        return (double) sum / (double) games;
    }
}
