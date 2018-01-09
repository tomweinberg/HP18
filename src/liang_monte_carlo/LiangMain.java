package liang_monte_carlo;

import main.Configuration;
import main.HP;
import main.Optimizer;
import main.OutputPrinter;
import mutation.MutationManager;
import mutation.MutationPreDefined;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Tom Weinberg and Jonathan Rosenberg on 26/12/2017
 */
public class LiangMain extends HP {

    /**
     * @param argv The config.ini path (you can always
     *             use the one in the project out folder by run with
     *             ..\out\config.ini as first arg
     * @throws IOException
     */
    public static void main(String[] argv) throws IOException {
        startTime = new Date().getTime();
        if (argv.length != 1)
            throw new RuntimeException("Must get the path to the configuration file.");
        loadConfGui(argv[0]);
        Configuration config = getConfiguration(argv);
        OutputPrinter outWriter = getOutWriter(config);
        MutationManager mutationManager = getMutationManager(config);
        Optimizer optimizer = new LiangOptimizer(outWriter, config, mutationManager);

        run(config, optimizer, outWriter);

        long runningTime = (new Date().getTime() - startTime) / 1000;
        System.out.println("Done. Time: " + runningTime
                                   + " seconds. Faild " + mutationManager.getNumOfFailures()
                                   + " out of " + mutationManager.getNumOfIterations());
        System.out.println("finish");
    }



    private static MutationManager getMutationManager(Configuration config) {
        System.out.print("Loading Mutation Manager ...");
        MutationManager mutationManager = new MutationManager(new MutationPreDefined(config));
        System.out.println("OK!");
        return mutationManager;
    }

}
