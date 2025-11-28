package org.tetris.ga;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

public class GaTrainer {

    private final int populationSize;
    private final int generations;
    private final int eliteSize;
    private final double mutationRate;
    private final int gamesPerFitness;
    private final long baseSeed;
    private final int threads;
    private final Random rnd;

    public GaTrainer() {
        this.populationSize = 100;
        this.generations = 50;
        this.eliteSize = 3;
        this.mutationRate = 0.8;
        this.gamesPerFitness = 5;
        this.baseSeed = 1234L;
        this.threads = Runtime.getRuntime().availableProcessors();
        this.rnd = new Random(42L);
    }

    private double randomInRange(double min, double max) {
        double r = rnd.nextDouble();
        return min + r * (max - min);
    }

    private double[] randomWeights() {
        double[] w = new double[4];
        w[0] = randomInRange(0.05, 5.0);
        w[1] = randomInRange(-5.0, -0.05);
        w[2] = randomInRange(-5.0, -0.05);
        w[3] = randomInRange(-5.0, -0.05);
        return w;
    }

    private double clamp(double x, double lo, double hi) {
        if (x < lo) return lo;
        if (x > hi) return hi;
        return x;
    }

    private double[] mutate(double[] weights, double sigma) {
        double[] out = new double[4];
        out[0] = clamp(weights[0] + rnd.nextGaussian() * sigma, 0.05, 5.0);
        out[1] = clamp(weights[1] + rnd.nextGaussian() * sigma, -5.0, -0.05);
        out[2] = clamp(weights[2] + rnd.nextGaussian() * sigma, -5.0, -0.05);
        out[3] = clamp(weights[3] + rnd.nextGaussian() * sigma, -5.0, -0.05);
        return out;
    }

    private double[] crossover(double[] a, double[] b) {
        double[] child = new double[4];
        for (int i = 0; i < 4; i++) {
            if (rnd.nextDouble() < 0.5) {
                child[i] = a[i];
            } else {
                child[i] = b[i];
            }
        }
        return child;
    }

    public double[] optimize() {
        double[][] population = new double[populationSize][4];

        for (int i = 0; i < populationSize; i++) {
            population[i] = randomWeights();
        }

        double[] bestOverall = population[0].clone();
        double bestOverallFit = Double.NEGATIVE_INFINITY;

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        try {
            for (int gen = 0; gen < generations; gen++) {
                double[] fitnesses = new double[populationSize];

                List<Future<Double>> futures = new ArrayList<Future<Double>>(populationSize);
                for (int i = 0; i < populationSize; i++) {
                    final double[] weights = population[i];
                    futures.add(executor.submit(() -> GameSimulator.fitness(weights, gamesPerFitness, baseSeed)));
                }

                for (int i = 0; i < populationSize; i++) {
                    try {
                        fitnesses[i] = futures.get(i).get();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }

                for (int i = 0; i < populationSize - 1; i++) {
                    int bestIdx = i;
                    for (int j = i + 1; j < populationSize; j++) {
                        if (fitnesses[j] > fitnesses[bestIdx]) {
                            bestIdx = j;
                        }
                    }
                    if (bestIdx != i) {
                        double tmpFit = fitnesses[i];
                        fitnesses[i] = fitnesses[bestIdx];
                        fitnesses[bestIdx] = tmpFit;

                        double[] tmpW = population[i];
                        population[i] = population[bestIdx];
                        population[bestIdx] = tmpW;
                    }
                }

                double bestFitGen = fitnesses[0];
                double[] bestWGen = population[0];

                if (bestFitGen > bestOverallFit) {
                    bestOverallFit = bestFitGen;
                    bestOverall = bestWGen.clone();
                }

                System.out.println("Gen " + (gen + 1) + "/" + generations +
                        ": best fitness = " + String.format("%.2f", bestFitGen) +
                        ", weights = [" +
                        bestWGen[0] + ", " + bestWGen[1] + ", " +
                        bestWGen[2] + ", " + bestWGen[3] + "]");

                double[][] newPop = new double[populationSize][4];

                for (int i = 0; i < eliteSize; i++) {
                    newPop[i] = population[i].clone();
                }

                int idx = eliteSize;
                while (idx < populationSize) {
                    int aIdx = rnd.nextInt(eliteSize);
                    int bIdx = rnd.nextInt(eliteSize);
                    double[] parentA = population[aIdx];
                    double[] parentB = population[bIdx];

                    double[] child = crossover(parentA, parentB);
                    if (rnd.nextDouble() < mutationRate) {
                        child = mutate(child, 0.6);
                    }
                    newPop[idx] = child;
                    idx++;
                }

                population = newPop;
            }
        } finally {
            executor.shutdown();
        }

        System.out.println();
        System.out.println("Best weights:");
        System.out.println("  fitness = " + String.format("%.2f", bestOverallFit));
        System.out.println("  weights = [" +
                bestOverall[0] + ", " +
                bestOverall[1] + ", " +
                bestOverall[2] + ", " +
                bestOverall[3] + "]");

        return bestOverall;
    }
}
