package liang_monte_carlo;

import gui.RunningView;
import main.Conformation;
import main.Log;
import main.OutputPrinter;

import java.io.IOException;

/**
 * Created by Tom Weinberg and Jonathan Rosenberg on 26/12/2017
 */
public class LiangPopulationLog extends Log {
    //Trajectory data

    /**
     * The best fitness.
     */
    protected float[] bestFitness;

    /**
     * The average fitness.
     */
    protected float[] averageFitness;

    /**
     * The worst fitness.
     */
    protected float[] worstFitness;

    protected OutputPrinter outPrinter;

    public LiangPopulationLog(int trajectorySize, OutputPrinter outPrinter, RunningView gui) {
        super(trajectorySize, outPrinter, gui);
        bestFitness = new float[trajectorySize];
        averageFitness = new float[trajectorySize];
        worstFitness = new float[trajectorySize];
        this.outPrinter = outPrinter;
    }

    public void initialize(int runNumber) {
        super.initialize(runNumber);
        for (int i = 0; i < trajectorySize; i++) {
            bestFitness[i] = 0;
            averageFitness[i] = 0;
            worstFitness[i] = 0;
        }
    }

    public void printRun() {
        outPrinter.printRun(runNumber, this);
    }

    /**
     * Gets the best fitness per generation array.
     *
     * @return the best fitness
     */
    public float[] getBestFitness() {
        return bestFitness;
    }

    /**
     * Gets the worst fitness per generation array.
     *
     * @return the worst fitness array
     */
    public float[] getWorstFitness() {
        return worstFitness;
    }

    /**
     * Gets the fittest Of Generation array.
     *
     * @return the fittest array
     */
    public Conformation[] getBestConformations() {
        return bestConformations;
    }

    /**
     * Gets the average fitness per generation array.
     *
     * @return the average fitness
     */
    public float[] getAverageFitness() {
        return averageFitness;
    }

    public void collectStatistics(LiangPopulation population, int currentGenerarionNum, int numberOfGenerations, Long runningTime) throws IOException {
        this.collectStatistics(population, currentGenerarionNum, numberOfGenerations, runningTime, population.getPopulationTemperature());
    }

    public void collectStatistics(LiangPopulation population, int currentGenerarionNum, int numberOfGenerations, Long runningTime, float temperature) throws IOException {
        super.collectStatistics(population.getFirst(), population.getBestEnergy(), population.getAverageEnergy(), population.getWorstEnergy(), currentGenerarionNum, numberOfGenerations, runningTime, temperature);
        averageFitness[step - 1] = population.getAverageFitness();
        worstFitness[step - 1] = population.getLast().getFitness();
        bestFitness[step - 1] = population.getFirst().getFitness();
    }
}

