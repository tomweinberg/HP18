package main;


import java.io.IOException;
import java.util.Date;

import mutation.MutationManager;
import mutation.MutationPreDefined;
import simpleGA.GAOptimizer;

//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
/**
 * Main class where the program starts from
 * 
 * @author Chen
 * 
 */
public class HPsimpleGA extends HP{

	

	/**
	 * 
	 * @param argv
	 *            get's two parameters 1) the config.ini path (you can always
	 *            use the one in the project out folder by run with
	 *            ..\out\config.ini as first arg 2)the seed file that will be
	 *            use for the random calculation <a
	 *            href="http://en.wikipedia.org/wiki/Random_seed">for more
	 *            information about seeding</a>
	 * @throws IOException 
	 * 
	 */
	public static void main(String[] argv) throws IOException {
		startTime = new Date().getTime();
		if (argv.length != 1)
			throw new RuntimeException("Must get the path to the configuration file.");
		loadConfGui(argv[0]);
		Configuration config = getConfiguration(argv);
		OutputPrinter outWriter = getOutWriter(config);
		MutationManager mutationManager = getMutationManager(config);
		Optimizer optimizer = new GAOptimizer(outWriter, config, mutationManager);

		run(config, optimizer, outWriter);
		
		long runningTime = (new Date().getTime() - startTime) / 1000;
		System.out.println("Done. Time: " + runningTime
				+ " seconds. Faild " + mutationManager.getNumOfFailures()
				+ " out of " + mutationManager.getNumOfIterations());
	}

	
	
	private static MutationManager getMutationManager(Configuration config) {
		System.out.print("Loading Mutation Manager ...");
		MutationManager mutationManager = new MutationManager(new MutationPreDefined(config));
		System.out.println("OK!");
		return mutationManager;
	}
}
