package simpleGA;


import main.Configuration;
import main.GAabstruct;
import main.Optimizer;
import main.OutputPrinter;
import main.PopulationLog;
import main.Protein;
import mutation.MutationManager;

import java.io.IOException;

/**
 * A single run of the GA algorithm.
 */
public class GAOptimizer extends GAabstruct {

	/**
	 * the graph that represent's the system
	 *
	 */
	private PopulationLog log;
	private int runNumber;


	/**
	 * constructor : creates a new GARun
	 *
	 * @param fileWriter
	 *            the fileWriter of the system to write the result of the run
	 *            when finished
	 * @param config
	 *            the configuration file of the system
	 * @param mutationManager
	 *            the mutation manager of the system
	 */
	public GAOptimizer(OutputPrinter fileWriter, Configuration config,
			MutationManager mutationManager) {
		super(fileWriter, config, mutationManager);
		log = new PopulationLog(config.numberOfGenerations/config.reportEvery+1, fileWriter, gui);
		runNumber = 0;
	}



	/**
	 * Execute the run.
	 * @throws IOException
	 *
	 * @see Optimizer
	 */
	@Override
	public void execute() throws IOException {
		Protein in1, in2, out1, out2;
		Protein[] temp;

		long startTime = System.currentTimeMillis();
		long runningTime;
		log.initialize(runNumber);
		for (currentGenerarionNum = 0; currentGenerarionNum < config.numberOfGenerations; currentGenerarionNum++) {
			// Collections.sort(population, Collections.reverseOrder());
			population.sort();
			//testPopulation();
			Protein best = population.getFirst();

			if (random.nextFloat() < config.crossoverRate) {
				in1 = population.chooseProtein();
				in2 = population.chooseProtein();
				temp = population.getLastTwo();
				out1 = temp[0];
				out2 = temp[1];
				Protein.crossover(in1, in2, out1, out2, random);
				population.updateLastTwo();

			} else {
				in1 =  population.chooseProtein() ;
				if (in1.getConformation().size() == 0)
					throw new RuntimeException("in1.conformation.size() == 0\n"+"energy = "+in1.getEnergy());
				out1 = population.getLast();
				mutationManager.mutate(in1, out1, 10);
				population.updateLastTwo();
			}

			if (SHOW_BEST_IN_GUI
					&& (this.bestProtein == null || best.getEnergy() < this.bestProtein
							.getEnergy())) {
				this.bestProtein.reset();
				this.bestProtein.setConformation(best.getConformation());
				// this.bestProtein=new Protein(best);

			}

			runningTime = (System.currentTimeMillis() - startTime);
			if (currentGenerarionNum % config.reportEvery == 0) {
				log.collectStatistics(population, currentGenerarionNum, numberOfGenerations, runningTime);
			}
		}
		log.printRun();
		runNumber++;
	}

	public void testPopulation() {
		for (int i = 0; i < population.size()-2; i++) {
			Protein protein = population.getByRef(i);
			if (protein.getEnergy() == Float.MAX_VALUE)
				throw new RuntimeException("This is weird "+protein);
		}
	}


}
