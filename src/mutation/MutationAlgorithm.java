/**
 * 
 */
package mutation;

import main.Protein;

/**
 * Every mutation algorithm must implement this interface.
 * mutate is the public method for applying mutation on the input protein.
 *
 */
public interface MutationAlgorithm {
	public void mutate(Protein inProtein,Protein outProtein, int maxTries);
	public int getNumOfFailures ();
	public int getNumOfIterations ();
}
