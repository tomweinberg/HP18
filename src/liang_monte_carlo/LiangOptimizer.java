package liang_monte_carlo;

import main.Configuration;
import main.Grid;
import main.OutputPrinter;
import main.Protein;
import main.Sequence;
import mutation.MutationManager;

import java.io.IOException;

/**
 * Created by Tom Weinberg and Jonathan Rosenberg on 26/12/2017
 */
public class LiangOptimizer extends LiangOptimizerAbstract {

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
        Protein fakeProtein = new Protein(config.dimensions, new Sequence(
                config.sequence), random, Grid.getInstance((config.sequence.length() * 2) - 1,
                                                           config.dimensions), "fake Protein");
        for (currentGenerarionNum = 0; currentGenerarionNum < config.numberOfGenerations; currentGenerarionNum++) {
            int randomRefPlace = random.nextInt(population.reference.size());

            float temperature = population.reference.get(randomRefPlace).getTemperature();
            Protein randomProtein = population.getByRef(population.findRefPlace(randomRefPlace));
            int indexRandomProtein = population.reference.get(population.findRefPlace(randomRefPlace)).getIndex();
            float bestEnergy = population.getBestEnergy();

            mutationManager.mutate(randomProtein, fakeProtein, 10);

            if (mutateProbability(fakeProtein, temperature, randomProtein) && mutateSucceed(fakeProtein)) {
                population.get(indexRandomProtein).setConformation(fakeProtein.getConformation());
                population.reference.get(randomRefPlace).setEnergy(fakeProtein.getEnergy());
                if (population.getBestEnergy() > bestEnergy && config.finalTemperature <= 0.01)
                    throw new RuntimeException("This is weird: the energy was " + population.getBestEnergy() + " but now is " + bestEnergy +
                                                       " energy should not get bigger");
            }

            int index1InPop = random.nextInt(population.size());
            int index2InPop = chooseNextIndex(index1InPop);

            int index1InRef = population.findRefPlace(index1InPop);
            int index2InRef = population.findRefPlace(index2InPop);
            if (population.exchangeProbability(index1InRef, index2InRef))
                population.exchangeProtein(index1InPop, index2InPop, index1InRef, index2InRef);

            runningTime = (System.currentTimeMillis() - startTime);
            if (currentGenerarionNum % config.reportEvery == 0) {
                log.collectStatistics(population, currentGenerarionNum, numberOfGenerations, runningTime);
            }
        }
        log.printRun();
        runNumber++;
    }

    private boolean mutateProbability(Protein fakeProtein, float temperature, Protein randomProtein) {
        float fakeProteinEnergy = fakeProtein.getEnergy();
        float randomProteinEnergy = randomProtein.getEnergy();

        float probability = Math.min((float) Math.exp((-(fakeProteinEnergy - randomProteinEnergy))
                                                              / temperature), 1);
        return probability >= Math.random();
    }

    private int chooseNextIndex(int index1) {
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
        return index2;
    }

    private boolean mutateSucceed(Protein fakeProtein) {
        return !fakeProtein.getConformation().isEmpty();
    }
}