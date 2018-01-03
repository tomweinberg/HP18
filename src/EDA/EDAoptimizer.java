package EDA;

import java.io.IOException;


import main.Configuration;
import main.Protein;
import temperature.TemperatureManager;



public class EDAoptimizer extends EDAabstract {
	
	



	public EDAoptimizer(Configuration config, TemperatureManager tempratureSetter) throws IOException{
		super(config,tempratureSetter);
		this.random = config.random;
		this.probabilityMatrix = new ProbabilityMatrix(config ,temperatureManager);
		this.temperatureManager = tempratureSetter;

	}

	@Override
	public void execute() throws IOException {
		
		long startTime = System.currentTimeMillis();
		long runningTime;
		
		for (currentGenerarionNum = 0; currentGenerarionNum < config.numberOfGenerations; currentGenerarionNum++) {
			probabilityMatrix.clearProbabilities();
			probabilityMatrix.computeProbabilities(temperature);
			for (int iProtein = 0; iProtein<population.size(); iProtein++) {
				((EDAProtein) population.getByRef(iProtein)).initConformation();
			}
//			for (Protein protein: population) {
//				((EDAProtein) protein).initConformation();	
//			}
//2			population = new EDAPopulation(config, random, probabilityMatrix, probabilityMatrix.k_MarkovOrder);
//1			population = probabilityMatrix.getPopulation();
			for (int i = 0; i < population.size(); i++)
			population.sort();

			runningTime = (System.currentTimeMillis() - startTime);
			if (currentGenerarionNum % config.reportEvery == 0) {
				log.collectStatistics(population, currentGenerarionNum, config.numberOfGenerations, runningTime,temperature);
			}
			temperature = temperatureManager.getNextTemprature();
		}

	}
	
	
	
	
	

	@Override
	public void updateTempSetter(TemperatureManager temprature) {
		probabilityMatrix.updateTempratureSetter(temprature);
	}
	
	public float getBestEnergy(){
		//System.out.println("******* * "+this.bestProtein.getEnergy() + "  *********** "+population.size+ " ******************************");
		return this.bestProtein.getEnergy();
	}
	
		
	


}
