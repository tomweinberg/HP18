package main;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gui.DynamiclyConfGui;

public abstract class HP {
	protected static long startTime;
	
	public static void run(String[] argv) {


	}
	
	protected static void loadConfGui(String filename) {
		Properties properties = new Properties();
		Lock lock = new ReentrantLock();
		Condition waitForGui = lock.newCondition();
		try {
			properties.load(new FileInputStream(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
		DynamiclyConfGui dc = new DynamiclyConfGui(filename, waitForGui, lock);
		dc.setVisible(true);
		lock.lock();
		try {

			waitForGui.await();
		} catch (InterruptedException e) {
			System.err.println("we were interputed");
		} finally {
			lock.unlock();
		}

	}
	
	/**
	 * extract the configuration from the argument given to the program
	 * 
	 * @param argv
	 *            a string array of length 1 for the config.ini path
	 * @return an object contains all the data from the configuration file
	 */
	protected static Configuration getConfiguration(String[] argv) {
		if (argv.length != 1) {
			throw new RuntimeException(
					"Cannot run GA \n Usage: java main.GA <config file name> ");
		}
		Configuration config;
		System.out.print("Loading Configuration ...");
		String configFileName = argv[0];
		System.out.println("Configuration file name " + configFileName);
	

		try {
			config = new Configuration(configFileName);
		} catch (IOException ex1) {
			throw new RuntimeException(
					"Failed to find, open or read configuration file: "
							+ configFileName + "\n" + ex1);
		}

		System.out.println("OK!");
		return config;
	}

	/**
	 * 
	 * @param config
	 *            the config file of this run
	 * @return new output file writer from the config file
	 */
	protected static OutputPrinter getOutWriter(Configuration config) {
		System.out.print("Creating output file writers ...");
		OutputPrinter outWriter;
		try {
			outWriter = new PopulationOutputPrinter(config);
		} catch (IOException ex1) {
			throw new RuntimeException("Cannot open output file\n" + ex1);
		}
		System.out.println("OK!");
		return outWriter;
	}

	protected static void run(Configuration config, Optimizer optimizer, OutputPrinter outWriter ) throws IOException{
		for (int i = 0; i < config.numberOfRepeats; i++) {
			Runtime.getRuntime().gc();
			System.out.println("Free memory = "+Runtime.getRuntime().freeMemory());
			optimizer.initiate(i); // An independent run
			System.out.println("Starting Run " + i + " ...");
			optimizer.execute();
		}
		try {
			outWriter.close();
		} catch (IOException ex1) {
			throw new RuntimeException("Failed to close output file\n" + ex1);
		}
		
	}
	

}
