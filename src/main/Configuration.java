package main;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class Configuration {

	private Properties properties;

	public final float crossoverRate;
	public final int populationSize;
	public final int numberOfGenerations;
	public final int numberOfRepeats;
	public final int reportEvery;
	public final int seed;
	public final Dimensions dimensions;
	public final Random random;
	public final String mutationsFileName;
	public final String prefixForOutputFiles;
	public final String sequence;
	public final float initialTemperature;
	public final float finalTemperature;

	public int K_MarkovOrder;

	public Configuration(String fileName) throws IOException {

		properties = new Properties();
		properties.load(new FileInputStream(fileName));
		System.out.println(properties);
		System.out.println(properties.getProperty("PopulationSize"));
		
		crossoverRate = parseFloat(properties.getProperty("CrossoverRate"));
		populationSize = parseInt(properties.getProperty("PopulationSize"));
		numberOfGenerations = parseInt(properties.getProperty("Generations"));
		numberOfRepeats = parseInt(properties.getProperty("RepeatTimes"));
		reportEvery = parseInt(properties.getProperty("ReportEvery"));
		mutationsFileName = properties.getProperty("MutationFileName");
		prefixForOutputFiles = properties.getProperty("PrefixOutputFiles");
		sequence = properties.getProperty("Sequence");
		initialTemperature = parseFloat(properties.getProperty("InitialTemperature"));
		finalTemperature  = parseFloat(properties.getProperty("FinalTemperature"));
		this.seed = parseInt(properties.getProperty("Seed"));
		K_MarkovOrder = parseInt(properties.getProperty("K_MarkovOrder"));
		random = new Random(seed);

		try {
			int dim = parseInt(properties.getProperty("Dimensions"));
			if (dim == 2)
				dimensions = Dimensions.TWO;
			else {
				if (dim == 3)
					dimensions = Dimensions.THREE;
				else
					throw new RuntimeException("Unsupported " + dim
							+ " dimensions");
			}
		} catch (NumberFormatException ex) {
			throw new RuntimeException("Cannot parse "
					+ properties.getProperty("Dimensions") + " as dimensions"
					+ "\n" + ex);
		}
	}
}
