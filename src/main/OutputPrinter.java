package main;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;


public abstract class OutputPrinter {
	protected PrintWriter writer;
	protected File resultFile;
	protected Configuration config;
	protected int seed;

	public OutputPrinter(Configuration config) throws IOException {
		this.seed = config.seed;
		this.resultFile = new File(config.prefixForOutputFiles + seed + ".xml");
		this.writer = new PrintWriter(resultFile);
		this.config = config;
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		writer.println("<runList "+
		               "      name=\""+config.prefixForOutputFiles+"\" "+
		               "      seed=\""+seed+"\""+
		               " sequence =\""+config.sequence+"\" "+
		               "dimensions=\""+config.dimensions+"\">");
	}
	
	public abstract void printRun(int runNumber,Log log);
	
	public void close() throws IOException {
		writer.println("</runList>");
		writer.close();
	}
}
