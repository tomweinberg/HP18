package EDA;

import main.MonomerDirection;
import main.Configuration;
import main.Protein;
import main.Sequence;
import main.Conformation;
import temperature.TemperatureManager;


import java.util.*;

public class ProbabilityMatrix {
	public final float FRACTION = (float) 0.1;
	private Random random;
	private EDAPopulation population;
	final int k_MarkovOrder; 
	private TemperatureManager temperatureManager;
	private Sequence sequence;

	private float[] proteinsProbabilities;
	private float [][][] k_probabilities;
	private float[][][] probabilities;
	private float [][] first_K_Probs;
	private int populationSize;
	private int eliteSize;
	private Configuration config;
	


	public ProbabilityMatrix(Configuration config, TemperatureManager temprature){
		this.config = config;
		this.sequence = new Sequence(config.sequence);
		this.k_MarkovOrder = config.K_MarkovOrder;
		this.random = config.random;
		this.temperatureManager = temprature;
		this.populationSize = config.populationSize;
		if (k_MarkovOrder > 0){
			k_probabilities = new float [sequence.size() - k_MarkovOrder -1 ][(int) Math.pow(3, k_MarkovOrder-1)][3];
		}
		else{
			k_probabilities = new float [sequence.size() - k_MarkovOrder -1 ][1][3];

		}
		probabilities = new float [sequence.size() - k_MarkovOrder - 1][(int) Math.pow(3, k_MarkovOrder)][3];
		first_K_Probs = new float[k_MarkovOrder ][3];
		if(k_MarkovOrder == 0){
			first_K_Probs = new float[sequence.size()][3];
		}
		initiateProbabilities();
		eliteSize = (int) ((int) populationSize*FRACTION);
		proteinsProbabilities = new float [eliteSize];


		population = new EDAPopulation(config, random, this, k_MarkovOrder);

	}
	
	

	public String toString() {	
		if (1 == 1)throw new RuntimeException();
		String out =  "ProbabilityMatrix config="+config+" TemperatureManager="+temperatureManager+"  "+k_MarkovOrder+"\n";
		for (int i = 0; i < probabilities.length; i++){
			out += "****** "+i+" ******\n";
			for (int j = 0; j< probabilities[i].length; j++) {
				for (int k = 0; k < probabilities[i][j].length; k++) 
					out += " "+probabilities[i][j][k];
				out+="            ";
				for (int k = 0; k < probabilities[i][j].length; k++) 
					out += " "+k_probabilities[i][j][k];
				out+="            ";
				for (int k = 0; k < probabilities[i][j].length; k++) 
					out += " "+first_K_Probs[i][k];
				out +="\n";
			}
		}
		return out;
	}

	public void initiateProbabilities(){
		for (int i = 0 ; i< probabilities.length ; i++){
			for (int j = 0 ; j < probabilities[i].length ; j++){
				for (int k = 0; k < probabilities[i][j].length; k++){
					probabilities[i][j][k] = (float) (1)/3;
				}
			}
		}
		for (int i = 0 ; i < first_K_Probs.length ; i++){
			for (int j = 0 ; j < first_K_Probs[i].length ; j++){
				first_K_Probs[i][j] = (float) (1)/3;
			}
		}
		for (int i = 0 ; i < k_probabilities.length ; i++){
			for (int j = 0 ; j < k_probabilities[i].length ; j++){
				for (int k = 0; k < k_probabilities[i][j].length; k++){
					k_probabilities[i][j][k] = 0;
				}
			}
		}


	}

	/**
	 * compute all the probabilities and build a new population 
	 */
	public void computeProbabilities(float temperature){
	computeBoltzmannProbabilites(temperature);
		computeKProbabilities();
		computeConditionalProbabilities();

//		population = new EDAPopulation(config, random, this, k_MarkovOrder);
	}
	/**
	 * calculates the probability for each protein in the population by the Boltzmann distribution
	 */

	private void computeBoltzmannProbabilites(float temperature){
	//	int index = 0;
		float denominator = 0;
		float probability = 0;
		for (int iProtein = 0; iProtein < eliteSize; iProtein++){
			denominator += calculateBoltzmann(population.getByRef(iProtein) ,temperature);
		}
//		for (Protein protein : population){
//			denominator += calculateBoltzmann(protein,temperature);
//		}
		for (int iProtein = 0; iProtein < eliteSize; iProtein++){
			probability = calculateBoltzmann(population.getByRef(iProtein),temperature) / denominator;
			proteinsProbabilities [iProtein] = probability;
		}
//		for (Protein protein : population){
	//		probability = calculateBoltzmann(protein,temperature) / denominator;
	//		proteinsProbabilities [index] = probability;
	//		index++;
	//	}
	}	

	private float calculateBoltzmann (Protein protein, float temperature){
		float energy = protein.getEnergy();
		float probability = (float) Math.exp(-energy / temperature);
		if (Float.isNaN(probability) || Float.isInfinite(probability) ){
			throw new RuntimeException( "Boltzmann probability is "+ probability+" "+energy+" "+temperature);
		
		}
		return probability;
	}



	private void computeConditionalProbabilities(){
		int index = 0;
		int k_index;
		int y;
		float proteinProbability = 0;
		float newProbability;
		float oldProbability;
		ArrayList<MonomerDirection> k_prev = new ArrayList<MonomerDirection>(k_MarkovOrder);
		float sumOfProbs;
		if (k_MarkovOrder > 0){
			for (int iProtein = 0; iProtein < eliteSize; iProtein++){
//			for (Protein protein : population){
				Protein protein = population.getByRef(iProtein);
				Conformation conformation = protein.getConformation();
				proteinProbability = proteinsProbabilities[iProtein];

				for (int i = k_MarkovOrder + 1 ; i < protein.size() ; i++){
					if (k_MarkovOrder > 0){
						for (int j = i - k_MarkovOrder ; j < i ; j++){
							k_prev.add(conformation.get(j));
						}
					}
					k_index = findIndex(k_prev);
					
					
					oldProbability = probabilities[i - k_MarkovOrder - 1][k_index][conformation.get(i).ordinal()];
					k_prev.remove(k_prev.size() - 1);
					y =  findIndex(k_prev);
					
					sumOfProbs = k_probabilities[i - k_MarkovOrder - 1][y][conformation.get(i-1).ordinal()];

					
					newProbability = oldProbability + (proteinProbability ) / sumOfProbs;
					if (newProbability > 1 || newProbability < 0){

						//throw new RuntimeException("illegal probabilty: "+ newProbability);
					}
					probabilities[i - k_MarkovOrder -1][k_index][conformation.get(i).ordinal()] = newProbability;
					k_prev.clear();


					checkSumOfProbs(i);
				}
				index++;
			}
		}
	}





	private void computeKProbabilities(){ //marginal probabilities for all the K's monomers directions options
		int index = 0;
		float proteinProbability = 0;
		
		ArrayList<MonomerDirection> k_prev = new ArrayList<MonomerDirection>(k_MarkovOrder );
		
		for (int i = 0; i < first_K_Probs.length; i++)
			for (int j = 0; j < first_K_Probs[0].length; j++)
				first_K_Probs[i][j]= 0;
		
		for (int i = 0; i < k_probabilities.length; i++)
			for (int j = 0; j < k_probabilities[0].length; j++)
				for (int k = 0; k < k_probabilities[0][0].length; k++)
					k_probabilities[i][j][k]= 0;
		
		
		int k_index;
		if (k_MarkovOrder == 0){
			for (int iProtein = 0; iProtein<eliteSize; iProtein++) {
				Protein protein = population.getByRef(iProtein);
				//for (Protein protein : population){
				proteinProbability = proteinsProbabilities[iProtein];

				for (int i = 1 ; i < protein.size() ; i++){
					first_K_Probs [i -1][protein.getConformation().get(i).ordinal()] += proteinProbability;
				}
				index++;
			}
		}
		else{
			for (int iProtein = 0; iProtein<eliteSize; iProtein++) {
				Protein protein = population.getByRef(iProtein);
				//for (Protein protein : population){
				proteinProbability = proteinsProbabilities[iProtein];
				for (int i = 1 ; i <= k_MarkovOrder ; i++){
					first_K_Probs [i - 1][protein.getConformation().get(i).ordinal()] += proteinProbability;

				}


				for (int i = k_MarkovOrder + 1 ; i < protein.size() ; i++){ 
					if (k_MarkovOrder > 0){
						for (int j = i - k_MarkovOrder  ; j < i -1 ; j++){
							k_prev.add(protein.getConformation().get(j));
						}
					}
					k_index = findIndex(k_prev); 
					//System.out.println(k_index+" "+ k_probabilities [i - k_MarkovOrder - 1].length+"!!!!!!!!!!!!!! "+k_prev+ " "+ k_MarkovOrder);
					k_probabilities [i - k_MarkovOrder - 1][k_index][protein.getConformation().get(i -1).ordinal()] += proteinProbability;
					k_prev.clear();
					
				}
				index++;
			}


		}

	}

	private void checkSumOfProbs(int i) {
		int sum = 0;
		for( int j = 0; j < probabilities[i - k_MarkovOrder -1].length ; j++){
			for ( int k = 0 ; k< probabilities[i - k_MarkovOrder -1][j].length ; k++){
				if (probabilities[i - k_MarkovOrder -1][j][k] < 0 || probabilities[i - k_MarkovOrder -1][j][k] > 1 ){
				//	throw new RuntimeException("probability is " + probabilities[i - k_MarkovOrder -1][j][k]+ " and not between 0 to 1" );
				}
				sum += probabilities[i - k_MarkovOrder -1][j][k];
				//	System.out.println(probabilities[i - k_MarkovOrder -1][j][k]+"  "+i+" " + j+ " "+ k);
				if (sum > 1){
					throw new RuntimeException("sum of probs larger than 1, it is " + sum+ " ,and the index is "+ i);
				}
				sum = 0;
			}

		}
	}



	/**
	 * this method set the probabilities for the directions in the option array for the first k monomers
	 * @param index
	 * @param options
	 * @return 
	 */



	public void setOptions(int index ,DirectionProbability[] options ){
		options[MonomerDirection.LEFT.ordinal()].setProbabilty(first_K_Probs[index - 1][MonomerDirection.LEFT.ordinal()]);
		options[MonomerDirection.RIGHT.ordinal()].setProbabilty(first_K_Probs[index -1 ][MonomerDirection.RIGHT.ordinal()]);
		options[MonomerDirection.FORWARD.ordinal()].setProbabilty(first_K_Probs[index -1 ][MonomerDirection.FORWARD.ordinal()]);
		if ( options[0].getProbability() + options [1].getProbability() + options [2].getProbability() == 0){
			throw new RuntimeException(" All  the direction probabilities are 0");
		}
	


	}

	/**
	 *  this method set the probabilities for the directions in the options array after the first k monomers
	 * @param index
	 * @param k_probs
	 * @param options
	 * @return 
	 */
	public int setOptions(int index, ArrayList<MonomerDirection> k_probs, DirectionProbability[] options){
		int nZeros = 0;
		if (k_probs.size() > k_MarkovOrder){
			k_probs.remove(0);
		}
		int k_probsIndex = findIndex(k_probs) ;
		options[0].setProbabilty( probabilities[index - k_MarkovOrder -1][k_probsIndex][0]);
		options[1].setProbabilty( probabilities[index - k_MarkovOrder -1][k_probsIndex][1]);
		options[2].setProbabilty( probabilities[index - k_MarkovOrder-1][k_probsIndex][2]);
		
		for (int i = 0 ; i< options.length;i++){
			if (options[i].getProbability() == 0){
				nZeros++;
			}
		}
		return nZeros;
	}
	/**
	 * This method finds the index of the k-mer in the probabilities matrix
	 * @param k_probs
	 * @return
	 */
	private int findIndex(ArrayList<MonomerDirection> k_probs) {
		if (k_probs.size()!= 0 && k_probs.get(0).equals(MonomerDirection.FIRST)){
			throw new RuntimeException("First shouldn't be in k probs");
		}
		int index = 0;
		
		if (k_probs.size() == 0){
			index = 0;
		}
		else if (k_probs.size() == 1){
			index = k_probs.get(0).ordinal();
		}
		else{
			for (int i = 0 ; i < k_probs.size(); i++){
			index +=  (int) (k_probs.get(i).ordinal() * Math.pow(3,k_probs.size() - i -1));
			
					
			}
		}
		//System.out.println(k_probs+ " "+ index);
		return index;
	}




	/**
	 * This method choose direction according to the options probabilities
	 * @param options
	 * @param nZeros
	 * @return
	 */

	public  MonomerDirection choooseDirection(DirectionProbability[] options, int nZeros ) {
		MonomerDirection dir =null;
		float rnd = random.nextFloat();
		if (options[0].getProbability() + options[1].getProbability() + options[2].getProbability() == 0){
			//printProbabailities();
			throw new RuntimeException("All the options probabilities are 0" );
		}
		if (nZeros >= 3){
			throw new RuntimeException("nZeros is "+ nZeros +", can't choose direction");
		}
		if(nZeros == 2){
			for (int i = 0; i< options.length; i++){
				if (options[i].getProbability() != 0){
					return options[i].getDir();
				}
			}
		}

		else{
					dir = roulette(options ,rnd);

		}
		return dir;

	}


	/**
	 * This method choose direction according to the options probabilities
	 * @param options
	 * @param rnd
	 * @return
	 */


	private MonomerDirection roulette(DirectionProbability[] options, float rnd) {
		float rand;
		int k;
		MonomerDirection dir = null;
		boolean accept = false;
		while (!accept){
			
			k = (int)(3 * random.nextFloat());
			rand = random.nextFloat();
		//	System.out.println("** "+ k +" "+ rand + " "+  options[k].getProbability());
			if ( rand < options[k].getProbability()){
				accept = true;
				dir = options[k].getDir();
			}

		}
		return dir;
	}

	public void clearProbabilities(){
		for (int i = 0; i < probabilities.length; i++){
			for (int j = 0 ; j < probabilities[i].length ; j++){
				for (int k = 0 ; k < probabilities[i][j].length; k++){
					probabilities[i][j][k] = 0;
				}
			}
		}
		for (int i = 0; i< first_K_Probs.length ; i++){
			for (int j = 0 ;j < first_K_Probs[i].length ; j++){
				first_K_Probs[i][j] = 0;
			}
		}
		for (int i = 0; i < k_probabilities.length; i++){
			for (int j = 0 ; j < k_probabilities[i].length ; j++){
				for (int k = 0 ; k < k_probabilities[i][j].length; k++){
					k_probabilities[i][j][k] = 0;
				}
			}
		}
	}


	public void printProbabailities(){
		//System.out.print("first probs:");
		for (int i = 0; i < first_K_Probs.length; i++) {
			for (int j = 0 ; j < first_K_Probs[i].length; j++){
				//System.out.print(first_K_Probs[i][j]+" ");
			}
			//System.out.println("");
		}

		//System.out.println("probabailities:");

		for (int i = 0; i < probabilities.length; i++){
			for (int j = 0 ; j < probabilities[i].length ; j++){
				for (int k = 0 ; k < probabilities[i][j].length; k++){
					//System.out.print(probabilities[i][j][k]+" " ) ;
				}

			}
			//System.out.println( " index: "+ i);
		}
		//System.out.println("k probabailities:");
		for (int i = 0; i < k_probabilities.length; i++){
			for (int j = 0 ; j < k_probabilities[i].length ; j++){
				for (int k = 0 ; k < k_probabilities[i][j].length; k++){
					//System.out.print(k_probabilities[i][j][k]+" " ) ;
				}

			}
			//System.out.println( " index: "+ i);
		}
		float y =0;
		//System.out.println("proteinsProbabilities");
		for(int i = 0 ; i < proteinsProbabilities.length;i++){
			y = y + proteinsProbabilities[i];
			//System.out.print(proteinsProbabilities[i] +" ");
		}
		//System.out.println("sum of proteins probs: "+ y);	

	}



	public Protein findBest() {
		Protein best = population.get(0);
		for (Protein protein : population){
			if (protein.getFitness() > best.getFitness()){
				best = protein;
			}
		}
		return best;
	}



	public EDAPopulation getPopulation() {
		return population;
	}



	public void updateTempratureSetter(TemperatureManager temprature) {
			this.temperatureManager = temprature;		
	}
	
}	












