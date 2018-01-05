package liang_monte_carlo;

import gui.RunningView;
import main.Configuration;
import main.Grid;
import main.Optimizer;
import main.OutputPrinter;
import main.Protein;
import main.Sequence;
import mutation.MutationManager;

import java.io.IOException;
import java.util.Random;

/**
 * Created by tomw on 26/12/2017
 */
public abstract class Linagabstract implements Optimizer {

    public static final boolean debug = true;
    protected final boolean SHOW_BEST_IN_GUI = true;
    // private GUIGrid guiGrid;
    // private GUIGrid bestguiGrid;
    protected RunningView gui;

    protected boolean isFirstInit;

    // Data for All the Run
    /**
     * The protein population.
     */
    protected LiangPopulation population;

    /**
     * The mutation manager.
     */
    protected MutationManager mutationManager;

    /**
     * The current generarion num.
     */
    protected int currentGenerarionNum;

    /**
     * The random number for usage in the run.
     */
    protected Random random;

    /**
     * the output file writer.
     */
    protected OutputPrinter fileWriter;

    /**
     * The configuration class to get User given arguments.
     */
    protected Configuration config;

    protected int numberOfGenerations, reportEvery;
    protected Protein bestProtein;

    /**
     * Instantiates a new Genetic Algorithm (GA) run.
     *
     * @param fileWriter the file writer to write the stored data in the run
     * @param config     the configuration object to load user given data
     */

    public Linagabstract(OutputPrinter fileWriter, Configuration config,
                         MutationManager mutationManager) {

        this.config = config;
        this.random = config.random;
        this.fileWriter = fileWriter;
        this.mutationManager = mutationManager;
        isFirstInit = true;
        this.numberOfGenerations = config.numberOfGenerations;
        this.reportEvery = config.reportEvery;
        int size = (config.sequence.length() * 2) - 1;
        this.bestProtein = new Protein(config.dimensions, new Sequence(
                config.sequence), random, Grid.getInstance(size,
                                                           config.dimensions), "best protain");
        gui = new RunningView("results", size, size);
    }

    /**
     * create new random population
     *
     * @param runNumber
     */
    public void initiate(int runNumber) {

        System.out.println("Initializing run # " + runNumber);
        population = new LiangPopulation(config, random, mutationManager);
        currentGenerarionNum = 0;
    }

    /**
     * Gets the fitest.
     *
     * @return the fitest
     * @throws IOException
     */

    public abstract void execute() throws IOException;
}


