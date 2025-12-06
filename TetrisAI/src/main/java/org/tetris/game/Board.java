package org.tetris.game;

import static org.tetris.Constants.BOARD_HEIGHT;
import static org.tetris.Constants.BOARD_WIDTH;

public class Board {

    public static int[][] createEmptyBoard() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                board[y][x] = 0;
            }
        }
        return board;
    }

    public static int[][] copyBoard(int[][] board) {
        int[][] out = new int[BOARD_HEIGHT][BOARD_WIDTH];
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                out[y][x] = board[y][x];
            }
        }
        return out;
    }

    public static boolean collision(int[][] board, int[][] cells, int ox, int oy) {
        for (int i = 0; i < cells.length; i++) {
            int x = cells[i][0];
            int y = cells[i][1];
            int nx = ox + x;
            int ny = oy + y;

            if (nx < 0 || nx >= BOARD_WIDTH || ny >= BOARD_HEIGHT) {
                return true;
            }

            if (ny >= 0 && board[ny][nx] != 0) {
                return true;
            }
        }
        return false;
    }

    public static BoardResult lockPiece(int[][] board, int shapeIndex, int[][] cells, int ox, int oy) {
        int[][] newBoard = copyBoard(board);

        int value = shapeIndex + 1;
        for (int i = 0; i < cells.length; i++) {
            int x = cells[i][0];
            int y = cells[i][1];
            int nx = ox + x;
            int ny = oy + y;
            if (ny >= 0 && ny < BOARD_HEIGHT && nx >= 0 && nx < BOARD_WIDTH) {
                newBoard[ny][nx] = value;
            }
        }

        int linesCleared = clearLinesInPlace(newBoard);
        return new BoardResult(newBoard, linesCleared);
    }

    private static int clearLinesInPlace(int[][] board) {
        int linesCleared = 0;
        int[][] newBoard = new int[BOARD_HEIGHT][BOARD_WIDTH];

        int dstY = BOARD_HEIGHT - 1;
        for (int y = BOARD_HEIGHT - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[y][x] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                linesCleared++;
            } else {
                for (int x = 0; x < BOARD_WIDTH; x++) {
                    newBoard[dstY][x] = board[y][x];
                }
                dstY--;
            }
        }

        for (int y = dstY; y >= 0; y--) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                newBoard[y][x] = 0;
            }
        }

        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                board[y][x] = newBoard[y][x];
            }
        }

        return linesCleared;
    }

    public static int[] getColumnsHeights(int[][] board) {
        int[] heights = new int[BOARD_WIDTH];
        for (int x = 0; x < BOARD_WIDTH; x++) {
            int h = 0;
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                if (board[y][x] != 0) {
                    h = BOARD_HEIGHT - y;
                    break;
                }
            }
            heights[x] = h;
        }
        return heights;
    }

    public static int countHoles(int[][] board) {
        int holes = 0;
        for (int x = 0; x < BOARD_WIDTH; x++) {
            boolean blockSeen = false;
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                if (board[y][x] != 0) {
                    blockSeen = true;
                } else if (blockSeen) {
                    holes++;
                }
            }
        }
        return holes;
    }


    public static class BoardResult {
        public final int[][] board;
        public final int linesCleared;

        public BoardResult(int[][] board, int linesCleared) {
            this.board = board;
            this.linesCleared = linesCleared;
        }
    }
}
