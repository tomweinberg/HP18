package main;

import gui.GrapgView;
import gui.RunningView;
import org.jfree.ui.RefineryUtilities;
import temperature.TemperatureManager;

import java.io.IOException;

/**
 * Any information that has no effect on the simulation itself. 
 * @author chen
 *
 */
public class Log {
	 	RunningView gui;
	 	protected int step;
	
	 	//Trajectory data
	 	/** The best energy. */
		protected float[] bestEnergies;


		/** The average energy. */
		protected float[] averageEnergies;

		/** The worst energy. */
		protected float[] worstEnergies;

		/** The best conformation per generation */
		protected Conformation[] bestConformations;
		
		protected int generation[];
		
		
		/** The id for the run */
		protected String runID;

		
		private GrapgView graph;
		
		private float bestEnergySoFar = 100;
		
		
		protected TemperatureManager temperaturemanager;
		protected int runNumber = -1;
		protected int trajectorySize = -1;
		
		public Log(int trajectorySize, OutputPrinter outWriter, RunningView gui) {
			this.trajectorySize = trajectorySize;
			this.gui = gui;
			initGraph();
			bestEnergies = new float[trajectorySize];
			averageEnergies = new float[trajectorySize];
			worstEnergies = new float[trajectorySize];
			bestConformations = new Conformation[trajectorySize];
			generation = new int[trajectorySize];
		}
			
		
		public void initialize(int runNumber) {
			this.runNumber = runNumber;
			for (int i = 0; i < trajectorySize; i++) {
				bestEnergies[i] = 0;
				averageEnergies[i] = 0;
				worstEnergies[i] = 0;
				bestConformations[i] = null;
			}
			step = 0;			
		}
		/**
		 * Gets the best energy per generation array.
		 * 
		 * @return the best energy
		 */
		public float[] getBestEnergy() {
			return bestEnergies;
		}

		

		/**
		 * Gets the average energy per generation array.
		 * 
		 * @return the average energy
		 */
		public float[] getAverageEnergy() {
			return averageEnergies;
		}

		

		/**
		 * Gets the worst energy per generation array.
		 * 
		 * @return the worst energy array
		 */
		public float[] getWorstEnergy() {
			return worstEnergies;
		}

		


		

		/**
		 * creates a new graph and add it to the system
		 */
		private void initGraph() {
			graph = new GrapgView("details");
			graph.pack();
			RefineryUtilities.centerFrameOnScreen(graph);
			graph.setVisible(true);

		}

		/**
		 * add a coordinate of (genNum,BestEnergyVal) to the graph
		 * 
		 * @param genNum
		 *            the current generation of this update
		 * @param bestEnergyVal
		 *            the best individual in this generation
		 */
		public void updateGraph(int genNum, double bestEnergyVal,int runNumber) {
			graph.getDataSet().addValue(bestEnergyVal, Integer.toString(runNumber), Integer.toString(genNum));
		}
		
		public Conformation getFitestOfRun() {
			Conformation best = bestConformations[0];
			for (int i = 0; i < bestConformations.length; i++) {
				if (bestConformations[i] != null) {
					if (bestConformations[i].getFitness() > best.getFitness())
						best = bestConformations[i];
				}
			}
			return best;
		}
	
		public int getGneration(int step){
			return generation[step];
		}
		public void collectStatistics(Protein protein, float bestEnergy, 
				                                                 float averageEnergy, float worstEnergy, 
				                                                 int currentGenerarionNum, int numberOfGenerations, Long runningTime, float temperature) throws IOException{
			if (protein.getEnergy() != bestEnergy) 
				throw new RuntimeException("This is weird: "+protein.getEnergy()+" "+bestEnergy);
			
			bestEnergies[step]      = bestEnergy;
			averageEnergies[step]   = averageEnergy;
			worstEnergies[step]     = worstEnergy;
			bestConformations[step] = protein.conformation;
			generation[step]     = currentGenerarionNum;
		    updateGraph(currentGenerarionNum, bestEnergy,runNumber);
			System.out.println("Generation  " + currentGenerarionNum
					+ "  out of   " + numberOfGenerations
					+ "  Time is:  " + runningTime
					+ "  msec.      Fittest \n" + protein.conformation);
			System.out.println(gui);
			gui.buildCurrentGrid(protein);
			gui.setCurrentLabelDetails("Lowest energy="
					+ Double.toString(bestEnergy) + "    Average energy="+String.format("%-10.2f", averageEnergy)+" , Genaration="
					+ currentGenerarionNum+" temperature="+temperature);
			if (bestEnergySoFar>bestEnergy) {
				bestEnergySoFar = bestEnergy;
				gui.buildBestGrid(protein);
				gui.setBestLabelDetails("energy="
						+ Double.toString(bestEnergy) + " , Genaration="
						+ currentGenerarionNum);
			}

			System.out.println("Free memory = "+Runtime.getRuntime().freeMemory());
			step++;
		}
		
		

	
}
