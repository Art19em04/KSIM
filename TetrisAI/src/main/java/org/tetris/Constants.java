package org.tetris;

import java.awt.Color;

public class Constants {
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    public static final int BLOCK_SIZE = 30;

    public static final int SIDE_PANEL_WIDTH = 180;
    public static final int WINDOW_WIDTH = BOARD_WIDTH * BLOCK_SIZE + SIDE_PANEL_WIDTH;
    public static final int WINDOW_HEIGHT = BOARD_HEIGHT * BLOCK_SIZE;

    public static final Color BACKGROUND_COLOR = Color.BLACK;
    public static final Color GRID_COLOR = new Color(40, 40, 40);
    public static final Color TEXT_COLOR = new Color(220, 220, 220);

    public static final Color[] SHAPE_COLORS = new Color[] {
            new Color(0, 240, 240),   // I
            new Color(240, 240, 0),   // O
            new Color(160, 0, 240),   // T
            new Color(0, 240, 0),     // S
            new Color(240, 0, 0),     // Z
            new Color(0, 0, 240),     // J
            new Color(240, 160, 0)    // L
    };

    public static final double[] DEFAULT_WEIGHTS = new double[]{
            2.7563652341021756, -0.07245084551174433, -5.0, -1.0946993304593289
    };

    public static final int BOT_SPEED = 10;
}
