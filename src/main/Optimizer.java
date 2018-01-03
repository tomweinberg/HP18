package main;

import java.io.IOException;

public interface Optimizer {

	/**
	 * 
	 * @param runNumber
	 *            the number of the current run for output and logs
	 */
	public void initiate(int runNumber);

	/**
	 * Execute the run.
	 * 
	 * any class implement this interface suppose to calculate and show is
	 * result in this method
	 */
	public void execute() throws IOException;

}
