package org.tetris.game;

import org.tetris.AI;
import org.tetris.Constants;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import static org.tetris.Constants.*;

public class TetrisPanel extends JPanel implements ActionListener {

    private final int[][] board;
    private final double[] weights;
    private final Random random;

    private int currentShape;
    private int nextShape;

    private int score;
    private int totalLines;
    private int pieces;
    private boolean gameOver;

    private final Timer timer;

    public TetrisPanel() {
        this.board = Board.createEmptyBoard();
        this.weights = Constants.DEFAULT_WEIGHTS;
        this.random = new Random();

        this.currentShape = random.nextInt(Shape.SHAPE_COUNT);
        this.nextShape = random.nextInt(Shape.SHAPE_COUNT);

        this.score = 0;
        this.totalLines = 0;
        this.pieces = 0;
        this.gameOver = false;

        this.timer = new Timer(BOT_SPEED, this); //  Bot latency
        this.timer.start();

        setBackground(BACKGROUND_COLOR);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawBoard(g);
        drawSidePanel(g);
    }

    private void drawBoard(Graphics g) {
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                int px = x * BLOCK_SIZE;
                int py = y * BLOCK_SIZE;

                g.setColor(GRID_COLOR);
                g.drawRect(px, py, BLOCK_SIZE, BLOCK_SIZE);

                int cell = board[y][x];
                if (cell != 0) {
                    Color color = SHAPE_COLORS[cell - 1];
                    g.setColor(color);
                    g.fillRect(px + 2, py + 2, BLOCK_SIZE - 4, BLOCK_SIZE - 4);
                }
            }
        }
    }

    private void drawSidePanel(Graphics g) {
        int offsetX = BOARD_WIDTH * BLOCK_SIZE + 20;

        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Consolas", Font.PLAIN, 18));
        g.drawString("Java Tetris AI", offsetX, 30);

        g.drawString("Score: " + score, offsetX, 70);
        g.drawString("Lines: " + totalLines, offsetX, 100);
        g.drawString("Pieces: " + pieces, offsetX, 130);

        g.drawString("Next:", offsetX, 170);

        int[][] previewShape = Shape.SHAPES[nextShape][0];
        int minX = previewShape[0][0];
        int minY = previewShape[0][1];
        for (int i = 1; i < previewShape.length; i++) {
            int x = previewShape[i][0];
            int y = previewShape[i][1];
            if (x < minX) {
                minX = x;
            }
            if (y < minY) {
                minY = y;
            }
        }

        int startX = offsetX + 20;
        int startY = 190;
        for (int i = 0; i < previewShape.length; i++) {
            int x = previewShape[i][0] - minX;
            int y = previewShape[i][1] - minY;
            int px = startX + x * BLOCK_SIZE;
            int py = startY + y * BLOCK_SIZE;
            Color c = SHAPE_COLORS[nextShape];
            g.setColor(c);
            g.fillRect(px, py, BLOCK_SIZE - 4, BLOCK_SIZE - 4);
            g.setColor(GRID_COLOR);
            g.drawRect(px, py, BLOCK_SIZE - 4, BLOCK_SIZE - 4);
        }

        if (gameOver) {
            g.setColor(new Color(255, 80, 80));
            g.drawString("GAME OVER", offsetX, WINDOW_HEIGHT - 40);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            timer.stop();
            repaint();
            return;
        }

        stepGame();
        repaint();
    }

    private void stepGame() {
        AI.Move move = AI.chooseBestMove(board, currentShape, weights);
        if (move == null) {
            gameOver = true;
            return;
        }

        int[][] cells = Shape.SHAPES[currentShape][move.rotationIndex];

        if (Board.collision(board, cells, move.x, move.y)) {
            gameOver = true;
            return;
        }

        Board.BoardResult result = Board.lockPiece(board, currentShape, cells, move.x, move.y);

        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                board[y][x] = result.board[y][x];
            }
        }

        pieces++;
        totalLines += result.linesCleared;

        if (result.linesCleared == 1) {
            score += 40;
        } else if (result.linesCleared == 2) {
            score += 100;
        } else if (result.linesCleared == 3) {
            score += 300;
        } else if (result.linesCleared == 4) {
            score += 1200;
        }

        currentShape = nextShape;
        nextShape = random.nextInt(Shape.SHAPE_COUNT);
    }
}
