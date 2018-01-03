package EDA;

import main.Configuration;
import main.Grid;
import main.Protein;
import main.Population;
import main.Reference;


import java.util.Random;

public class EDAPopulation extends Population {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProbabilityMatrix probabilityMatrix;
	private int k_MarkovOrder;

	public EDAPopulation(Configuration config, Random rand,
			 ProbabilityMatrix probabilityMatrix, int k_MarkovOrder) {
		super (config, rand , null);
		this.probabilityMatrix = probabilityMatrix;
		this.k_MarkovOrder = k_MarkovOrder;
		clear();
		initPopulation();
		

}
	
	public void sort() {
		reference.clear();
		for (int i = 0; i < size(); i++) {
			Reference r = new Reference(i, get(i).getFitness());
			this.reference.add(r);
		}
		super.sort();
	}
	

	private void initPopulation() {
		for (int i1 = 0 ; i1 < size ;i1++ ){
			//if (!grid.testEmpty())
			//	throw new RuntimeException("Grid is not empty #2");
	
			Protein protein = new EDAProtein(dimensions, sequence, random, grid, Integer.toString(i1), probabilityMatrix, k_MarkovOrder);
				Reference newReference = new Reference(i1, protein.getFitness());
			reference.add(newReference);
			add(protein);
			grid.reset(protein);
//			grid = new Grid(config.sequence.length(), dimensions);
		}
		
	}
	
}