package main;


import java.io.IOException;
import java.util.Date;

import EDA.EDAoptimizer;
import temperature.TemperatureManager;
import temperature.LinearCooling;


public class HP_EDA extends HP {	

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
			TemperatureManager tempratureManager;
			
			startTime = new Date().getTime();
			if (argv.length != 1)
				throw new RuntimeException("Must get the path to the configuration file.");
			loadConfGui(argv[0]);
			Configuration config = getConfiguration(argv);
			OutputPrinter outWriter = getOutWriter(config);
			tempratureManager = new LinearCooling(config);
			Optimizer optimizer = new EDAoptimizer(config, tempratureManager);

			run(config, optimizer, outWriter);
			
			long runningTime = (new Date().getTime() - startTime) / 1000;
			System.out.println("Done. Time: " + runningTime);
		}



}
