package EDA;

import main.MonomerDirection;


import main.Protein;
import main.Dimensions;
import main.Grid;
import main.Conformation;
import main.Sequence;
import main.Monomer;


import java.util.ArrayList;
import java.util.Random;

public class EDAProtein extends Protein{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public ProbabilityMatrix probabilityMatrix;
	private int k_MarkovOrder;
	private DirectionProbability[] options;
	private int count;
	
	public EDAProtein(Dimensions dimensions, Sequence sequence, Random random,
			Grid grid, String name, ProbabilityMatrix probabilityMatrix, int k_MarkovOrder) {
		super(dimensions, sequence, random, grid, name,false); 
		this.probabilityMatrix = probabilityMatrix;
		this.k_MarkovOrder = k_MarkovOrder;
		this.conformation = new Conformation(sequence.size());
		options = new DirectionProbability[3];
		options[MonomerDirection.LEFT.ordinal()] = new DirectionProbability(0,MonomerDirection.LEFT); 
		options[MonomerDirection.RIGHT.ordinal()] = new DirectionProbability(0,MonomerDirection.RIGHT); 
	    options[MonomerDirection.FORWARD.ordinal()] = new DirectionProbability(0,MonomerDirection.FORWARD);  	    
	    initConformation();
		if (conformation.size() == 1) {
			throw new RuntimeException("Weird conformation "+conformation+"\n"+probabilityMatrix);
		}
		
	}
	
		
	public void initConformation() {
		conformation.clear();
		getConformationByProbability();
		evaluateEnergy();
		updateFitness();
		conformation.setFitness(fitness);
		conformation.setEnergy(energy);
		grid.reset(this);
		
	}


	
	
	
	public void getConformationByProbability(){
		ArrayList<MonomerDirection> k_probs = new ArrayList<MonomerDirection>(k_MarkovOrder);
		
	 	Monomer monomer = this.get(0);
					
		monomer.setRelativeDirection(MonomerDirection.FIRST);
		if (conformation.size() != 0) 
			throw new RuntimeException("This is weird "+conformation);
		conformation.add(monomer.getRelativeDirection());

		
	//	DirectionProbability options[] = new DirectionProbability[3];
		this.count = 0;
		options[MonomerDirection.LEFT.ordinal()].setProbabilty(0); 
		options[MonomerDirection.RIGHT.ordinal()].setProbabilty(0); 
	    options[MonomerDirection.FORWARD.ordinal()].setProbabilty(0); 
	    
		
		//k_probs.add( MonomerDirection.FIRST);
		while(!setConformation(monomer.getNext() ,1, options , k_probs)){
			this.count = 0;
			options[MonomerDirection.LEFT.ordinal()].setProbabilty(0); 
			options[MonomerDirection.RIGHT.ordinal()].setProbabilty(0); 
		    options[MonomerDirection.FORWARD.ordinal()].setProbabilty(0); 	
		}
		

	}
  private boolean setConformation(Monomer monomer ,int index, DirectionProbability[] options,ArrayList<MonomerDirection> k_probs) {
		boolean success = false;
		boolean set = false;
		int nZeros = 0;
		/*if (index == sequence.size()-1){  
			monomer.setRelativeDirection(MonomerDirection.END_OF_CHAIN);
			conformation.add(monomer.getRelativeDirection());
			success = true;
		}*/
		//else{
		count++;
		if (count > 10000) {
			return false;
		}
			MonomerDirection dir = null;
			if (index < k_MarkovOrder + 1 || k_MarkovOrder == 0){
				probabilityMatrix.setOptions(index, options);
				
			}
			else {
				nZeros = probabilityMatrix.setOptions(index, k_probs, options);
				
			}
			while (!success && nZeros < 3){
				dir = probabilityMatrix.choooseDirection(options ,nZeros);
				k_probs.add(dir);
				set = monomer.setRelativeDirection(dir);
				conformation.add(monomer.getRelativeDirection());
				DirectionProbability[] newOptions = new DirectionProbability[3];
				for(int i = 0 ; i < options.length;i++){
					newOptions[i] = new DirectionProbability(options[i]);
					}
				if (index == sequence.size() - 1){
					success = set;
				}
				else{
				success = (set && setConformation(monomer.getNext(), index + 1, newOptions, k_probs));
				}
				if (!success){
				monomer.reset();
				if (set){
				grid.reset(monomer);
				}
				conformation.remove(conformation.size()-1);
				if (k_probs.size() > 0){
				k_probs.remove(k_probs.size()-1);
				}
				nZeros = updateOptions(dir, options);
				}
				
			}
			return success;
	}
		
				
	
			
	public EDAProtein duplicate(ProbabilityMatrix probabilityMatrix, int K_MarkovOrder) {
		return new EDAProtein(dimensions, sequence, random, grid, name, probabilityMatrix, K_MarkovOrder);
	}


/**
 * this function update the probabilities in the option array after the dir direction failed
 * @param dir
 * @param options
 */
	private int updateOptions(MonomerDirection dir, DirectionProbability[] options) {
		int nZeros=0;
		if (dir.equals(MonomerDirection.LEFT)){
			if (options[1].getProbability() == 0 && options[2].getProbability() == 0)
				options[0].setProbabilty(0);
			else if (options[1].getProbability() == 0){
				options[2].setProbabilty(1);
				
			}
			else if (options[2].getProbability() == 0){
				options[1].setProbabilty(1);
				
			}
			else{
				float newProb = options[0].getProbability()/2;
				float firstProbability = newProb + options[1].getProbability();
				float secondProbability = newProb + options[2].getProbability();
				options[1].setProbabilty(firstProbability);
				options[2].setProbabilty(secondProbability);
			}
			options[0].setProbabilty(0);
			
		}
			
		if (dir.equals(MonomerDirection.RIGHT)){
			if (options[0].getProbability() == 0 && options[2].getProbability() == 0)
				options[1].setProbabilty(0);
				else if (options[0].getProbability() == 0){
					options[2].setProbabilty(1);
					
				}
				else if (options[2].getProbability() == 0){
					options[0].setProbabilty(1);
					
				}
				else{
					float newProb = options[1].getProbability()/2;
					float firstProbability = newProb + options[0].getProbability();
					float secondProbability = newProb + options[2].getProbability();
					options[0].setProbabilty(firstProbability);
					options[2].setProbabilty(secondProbability);
				}
				options[1].setProbabilty(0);
			
		}
		if (dir.equals(MonomerDirection.FORWARD)){
			if (options[0].getProbability() == 0 && options[1].getProbability() == 0)
				options[2].setProbabilty(0);
				else if (options[0].getProbability() == 0){
					options[1].setProbabilty(1);
					
				}
				else if (options[1].getProbability() == 0){
					options[0].setProbabilty(1);
					
				}
				else{
					float newProb = options[2].getProbability()/2;
					float firstProbability = newProb + options[0].getProbability();
					float secondProbability = newProb + options[1].getProbability();
					options[0].setProbabilty(firstProbability);
					options[1].setProbabilty(secondProbability);
				}
				options[2].setProbabilty(0);
		}
		for (int i = 0 ; i< options.length;i++){
			if (options[i].getProbability() == 0){
				nZeros++;
			}
		}
		return nZeros;
	}
	


	
	
}
