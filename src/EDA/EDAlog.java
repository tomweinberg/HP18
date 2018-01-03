package EDA;

import java.io.IOException;

import main.OutputPrinter;
import main.PopulationLog;
import main.Protein;
import temperature.TemperatureManager;
import gui.RunningView;

public class EDAlog extends PopulationLog {
	protected float[] temperatures;
	protected TemperatureManager temperatureManager;
	
	public EDAlog(int trajectorySize, OutputPrinter outWriter, TemperatureManager temperatureManager, RunningView gui ) {
		super(trajectorySize, outWriter,gui);
		this.temperatureManager = temperatureManager;
		temperatures = new float[generation.length];
	}
	
	public void initialize(int runNumber) {
		super.initialize(runNumber);
		for (int i = 0; i < generation.length; i++)
			temperatures[i] = 0;
	}
	
	public void collectStatistics(Protein protein, float bestEnergy, 
            float averageEnergy, float worstEnergy, 
            int currentGenerarionNum, int numberOfGenerations, Long runningTime, float temperature) throws IOException{
		super.collectStatistics(protein, bestEnergy, averageEnergy, worstEnergy, currentGenerarionNum, numberOfGenerations, runningTime,temperature);
		temperatures[step-1] = temperature;
		
	}
	
	public float getTemperature(int index){
		return temperatures[index];
	}

}
