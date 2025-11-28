package org.tetris;

import org.tetris.game.TetrisPanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import static org.tetris.Constants.WINDOW_WIDTH;
import static org.tetris.Constants.WINDOW_HEIGHT;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndStart();
            }
        });
    }

    private static void createAndStart() {
        JFrame frame = new JFrame("Tetris AI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        TetrisPanel panel = new TetrisPanel();
        frame.setContentPane(panel);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT + 40);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
