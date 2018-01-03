package mutation;

import main.Protein;


/**
 * An operator that impose conformational changes (mutations in the GA jargon)
 * on proteins. To this end it uses libraries of predefined, self avoiding local
 * mutations. Each library includes mutations of the same length.
 */
public class MutationManager {

	private MutationAlgorithm algorithm;
	/**
	 * creates a new mutation manager from the config file
	 * 
	 * @param config
	 *            the configuration class
	 */
	public MutationManager(MutationAlgorithm algorithm) {
        this.algorithm = algorithm;
	}
	
	public int getNumOfFailures (){
		return algorithm.getNumOfFailures();
	}

	public int getNumOfIterations(){
		return algorithm.getNumOfIterations();
	}

	public void mutate(Protein protein, Protein out, int max_tries) {
		algorithm.mutate(protein, out, max_tries);
	}
}
