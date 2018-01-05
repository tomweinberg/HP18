package liang_monte_carlo;

import main.Configuration;
import main.Grid;
import main.OutputPrinter;
import main.Protein;
import main.Sequence;
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
            Protein fakeProtein = new Protein(config.dimensions, new Sequence(
                    config.sequence), random, Grid.getInstance((config.sequence.length() * 2) - 1,
                                                               config.dimensions), "fack");
//            Protein in1 =  population.chooseProtein() ;
//            if (in1.getConformation().size() == 0)
//                throw new RuntimeException("in1.conformation.size() == 0\n"+"energy = "+in1.getEnergy());
//            Protein out1 = population.get(population.size()-1);
//            mutationManager.mutate(in1, fakeProtein, 10);
//            population.updateLastTwo();


//            bestProtein=population.chooseProtein();
//
            int randomPlace = new Random().nextInt(population.reference.size());

            float temperature=population.reference.get(randomPlace).getTemperature();
            Protein randomProtein =population.getByRef(population.findRefPlace(randomPlace));
            mutationManager.mutate(randomProtein,fakeProtein,10);


            float probability = Math.min((float) Math.exp((-fakeProtein.getEnergy() - randomProtein.getEnergy())
                                                                  /temperature), 1);

            if(probability>=Math.random()&&fakeProtein.getConformation().size()!=0)
                {
                population.set(population.findRefPlace(randomPlace), fakeProtein);
                population.reference.get(randomPlace).setEnergy(fakeProtein.getEnergy());

                }


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

            int index1InRef=population.findRefPlace(index1);
            int index2InRef=population.findRefPlace(index2);
            if(population.exchangeProbability(index1InRef, index2InRef))
                population.exchangeProtein(index1,index2,index1InRef,index2InRef);




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
