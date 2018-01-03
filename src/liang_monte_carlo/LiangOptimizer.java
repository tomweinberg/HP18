package liang_monte_carlo;

import main.Configuration;
import main.OutputPrinter;
import mutation.MutationManager;

import java.io.IOException;
import java.util.Random;

/**
 * Created by tomw on 26/12/2017
 */
public class LiangOptimizer extends Linagabstract {

    /**
     * the graph that represent's the system
     */
    private LiangPopulationLog log;
    private int runNumber;

    /**
     * Instantiates a new Genetic Algorithm (GA) run.
     *
     * @param fileWriter      the file writer to write the stored data in the run
     * @param config
     * @param mutationManager
     */
    public LiangOptimizer(OutputPrinter fileWriter, Configuration config, MutationManager mutationManager) {
        super(fileWriter, config, mutationManager);
        log = new LiangPopulationLog(config.numberOfGenerations / config.reportEvery + 1, fileWriter, gui);
        runNumber = 0;
    }

    @Override
    public void execute() throws IOException {
        long startTime = System.currentTimeMillis();
        long runningTime;
        log.initialize(runNumber);
        for (currentGenerarionNum = 0; currentGenerarionNum < config.numberOfGenerations; currentGenerarionNum++) {

//            in1 =  population.chooseProtein() ;
//            if (in1.getConformation().size() == 0)
//                throw new RuntimeException("in1.conformation.size() == 0\n"+"energy = "+in1.getEnergy());
//            out1 = population.getLast();
//            mutationManager.mutate(in1, out1, 10);

//
//            bestProtein.reset();
//            int randomPlace = new Random().nextInt(population.reference.size());
//
//            float temperature=population.reference.get(randomPlace).getTemperature();
//            Protein randomProtein =population.getByRef(population.findRealPlace(randomPlace));
//            mutationManager.mutate(randomProtein,bestProtein,10);
//
//            float probability = Math.min((float) Math.exp((-bestProtein.getEnergy() - randomProtein.getEnergy())
//                                                                  /temperature), 1);
//
//            if(probability>=Math.random())
//                {
//                population.set(population.findRealPlace(randomPlace),bestProtein);
//                population.reference.get(randomPlace).setEnergy(bestProtein.getEnergy());
//
//                }


            int index1 = new Random().nextInt(population.size());
            int index2;
            if (index1 == 0)
                index2 = 1;
            else if (index1 == population.size - 1)
                index2 = population.size - 2;
            else {
                if (Math.random() < 0.5)
                    index2 = index1 + 1;
                else {
                    index2 = index1 - 1;
                }
            }
            index1=population.findRealPlace(index1);
            index2=population.findRealPlace(index2);
            if(population.exchangeProbability(index1, index2))
                population.exchangeProtein(index1,index2);




//            float BoltzmannBeforeExchange = population.PopulationBoltzmann();
//            population.exchangeProtein(index1, index2);
//            float BoltzmannAfterExchange = population.PopulationBoltzmann();
//            if (BoltzmannAfterExchange / BoltzmannBeforeExchange < config.crossoverRate)
//                population.exchangeProtein(index2, index1);










            runningTime = (System.currentTimeMillis() - startTime);
            if (currentGenerarionNum % config.reportEvery == 0) {
                log.collectStatistics(population, currentGenerarionNum, numberOfGenerations, runningTime);
            }
        }
        log.printRun();
        runNumber++;
    }
}
