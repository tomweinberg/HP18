/**
 * 
 */
package EDA;


import main.Configuration;
import main.OutputPrinter;
import main.PopulationOutputPrinter;
import temperature.TemperatureManager;
import main.Optimizer;
import main.Conformation;
import main.Protein;

import gui.RunningView;

import java.io.IOException;
import java.util.Random;



/**
 * @author user
 *
 */
public abstract class EDAabstract implements Optimizer {
	/** The id for the run */
	protected String id;
	protected RunningView gui;
	protected boolean isFirstInit;
	/** The current generation number. */
	protected int currentGenerarionNum;
	protected float temperature;

	/** The random number for usage in the run. */
	protected Random random;

	/** the output file writer. */
	protected OutputPrinter fileWriter;

	/** The configuration class to get User given arguments. */
	protected Configuration config;
	protected ProbabilityMatrix probabilityMatrix;
	protected int numberOfGenerations, reportEvery;
	protected EDAProtein bestProtein;
	protected final boolean SHOW_BEST_IN_GUI = true;
	protected EDAPopulation population;
	protected final EDAlog log; 
	protected TemperatureManager temperatureManager;
	
	


	/* (non-Javadoc)
	 * @see main.Optimizer#initiate(int)
	 */
	public EDAabstract(Configuration config, TemperatureManager temperatureManager) throws IOException {
		this.temperatureManager = temperatureManager;
		this.temperatureManager.reset();
		this.config = config;
		this.random = config.random;
		this.fileWriter = new PopulationOutputPrinter(config);
		isFirstInit = true;
		this.numberOfGenerations = config.numberOfGenerations;
		this.reportEvery = config.reportEvery;
		this.bestProtein = null;
		int size = (config.sequence.length() * 2) - 1;
		gui = new RunningView("results", size, size);
		log = new EDAlog(config.numberOfGenerations/config.reportEvery+1, fileWriter, temperatureManager,gui);
		temperature = temperatureManager.getNextTemprature();
				/*new Protein(config.dimensions, new Sequence(
				config.sequence), random, Grid.getInstance(size,
				config.dimensions), "best protain");*/
		//gui = new RunningView("results", size, size);
	
	}
	@Override
	public void initiate(int runNumber) {
		this.temperatureManager.reset();
		probabilityMatrix.initiateProbabilities();
		population = new EDAPopulation(config, config.random, probabilityMatrix, config.K_MarkovOrder);
		log.initialize(runNumber);
		System.out.println("Initializing run # " + runNumber);
		log.initialize(runNumber);
		setId("runNumber " + runNumber);
		for (Protein protein: population) {
			System.out.println(protein.getConformation()+" "+protein.getEnergy());
		}
		//if (1 == 1) throw new RuntimeException();
		//population = new EDAPopulation(config, config.random, probabilityMatrix, config.K_MarkovOrder);
		System.out.println("========================= EDAabstract "+population.size());

		population.sort();
		
		
		
		//population = new Population(config, random, mutationManager);
	//	population.sort();
		currentGenerarionNum = 0;
		if (isFirstInit) { // if this is the first init then create the needed
							// arrays else. clear the existing one's
			//population.sort();
			// Collections.sort(population, Collections.reverseOrder());
			bestEnergy = new float[numberOfGenerations / reportEvery + 1];
			averageEnergy = new float[numberOfGenerations / reportEvery + 1];
			worstEnergy = new float[numberOfGenerations / reportEvery + 1];
			bestFitness = new float[numberOfGenerations / reportEvery + 1];
			averageFitness = new float[numberOfGenerations / reportEvery + 1];
			worstFitness = new float[numberOfGenerations / reportEvery + 1];
			fittest = new Conformation[numberOfGenerations / reportEvery + 1];
			generation = new int[numberOfGenerations / reportEvery + 1];
		}
		for (int i = 0; i < numberOfGenerations / config.reportEvery + 1; i++) {
			bestEnergy[i] = 0;
			averageEnergy[i] = 0;
			worstEnergy[i] = 0;
			bestFitness[i] = 0;
			averageFitness[i] = 0;
			worstFitness[i] = 0;
			fittest[i] = null;
		}
	
	}

	
	
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	
	protected int generation[];
	/** The fittest per generation */
	protected Conformation[] fittest;
	// Data per generation
	/** The best energy. */
	protected float[] bestEnergy;

	/** The best fitness. */
	protected float[] bestFitness;

	/** The average energy. */
	protected float[] averageEnergy;

	/** The average fitness. */
	protected float[] averageFitness;

	/** The worst energy. */
	protected float[] worstEnergy;

	/** The worst fitness. */
	protected float[] worstFitness;
	
	public abstract void updateTempSetter(TemperatureManager temprature);
	

}





























