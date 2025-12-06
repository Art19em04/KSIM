package org.tetris.ga;

public class GaMain {
    public static void main(String[] args) {
        GaTrainer trainer = new GaTrainer();
        double[] best = trainer.optimize();

        System.out.println();
        System.out.println("Res:");
        System.out.println("Best weights (w_lines, w_height, w_holes, w_bump):");
        System.out.println(
                best[0] + ", " +
                best[1] + ", " +
                best[2] + ", " +
                best[3]
        );
    }
}
