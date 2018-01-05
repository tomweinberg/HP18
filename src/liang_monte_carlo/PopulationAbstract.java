package liang_monte_carlo;

import main.Configuration;
import main.Dimensions;
import main.Grid;
import main.Protein;
import main.Reference;
import main.Sequence;
import mutation.MutationManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by tomw on 26/12/2017
 */
public abstract class PopulationAbstract extends ArrayList<Protein> {

    /**
     * The random number to used in the population.
     */
    public final Random random;
    /**
     * The size.
     */
    public final int size;
    /**
     * The mutation manger.
     */
    public final MutationManager mutationManger;
    public final Dimensions dimensions;
    public final Sequence sequence;
    public ArrayList<Reference> reference;
    protected Grid grid;
    protected Configuration config;

    /**
     * Instantiates a new population. filling the population with random (Legal)
     * Proteins
     *
     * @param rand           the random number to be used in the population
     * @param mutationManger the mutation manger
     */
    public PopulationAbstract(Configuration config, Random rand, MutationManager mutationManger) {
        this.config = config;
        random = rand;
        size = config.populationSize;

        dimensions = config.dimensions;
        this.mutationManger = mutationManger;
        sequence = new Sequence(config.sequence);

        grid = Grid.getInstance(config.sequence.length(), dimensions);
    }

    /**
     * Gets the last.
     *
     * @return the last
     */
    public Protein getLast() {

        // return get(size()-1);
        return getByRef(size() - 1);
    }

    /**
     * Gets the last.
     *
     * @return the last
     */
    public Protein[] getLastTwo() {
        Protein[] out = new Protein[2];
        // out[0] = get(size()-1);
        // out[1] = get(size()-2);
        out[0] = getByRef(size() - 1);
        out[1] = getByRef(size() - 2);
        return out;
    }

    // our addition
    public Protein getByRef(int index) {
        int i = reference.get(index).getIndex();
        return this.get(i);
    }

    /**
     * Gets the first.
     *
     * @return the first
     */
    public abstract Protein getFirst();

    /**
     * Mutate.
     */
    //        public void mutate() {
    //            Protein in = chooseProtein();
    //            Protein out = getLast();
    //            mutationManger.mutate(in, out, 10);
    //            this.reference.get(size() - 1).setFitness(out.getFitness());
    //
    //            // Collections.sort(this);
    //            Collections.sort(this.reference);
    //        }

    /**
     * Choose one of the proteins, with higher probability to the lower list
     * position (better fitness)
     *
     * @return the selected protein
     */
    public Protein chooseProtein() {
        float rnd = random.nextFloat(); // Evenly distributed between 0 and 1
        rnd = 1 - ((2 * rnd) / (rnd + 1));// Between 0 & 1, but Biased towards higher values
        Protein out = getByRef((int) (rnd * (size() - 1)));
        return out;
    }

    /**
     * Gets the best energy.
     *
     * @return the best energy
     */
    public float getBestEnergy() {
        float best = this.getFirst().getEnergy();
        for (Protein protein : this) {
            if (protein.getEnergy() < best) {
                best = protein.getEnergy();
            }
        }
        return best;
    }

    /**
     * Gets the best fitness.
     *
     * @return the best fitness
     */
    public float getBestFitness() {
        float best = this.getFirst().getFitness();
        for (Protein protein : this) {
            if (protein.getFitness() > best) {
                best = protein.getFitness();
            }
        }
        return best;
    }

    /**
     * Gets the worst energy.
     *
     * @return the worst energy
     */
    public float getWorstEnergy() {
        float worst = this.getFirst().getEnergy();
        for (Protein protein : this) {
            if (protein.getEnergy() > worst) {
                worst = protein.getEnergy();
            }
        }
        return worst;
    }

    /**
     * Gets the worst fitness.
     *
     * @return the worst fitness
     */
    public float getWorstFitness() {
        float best = this.getFirst().getFitness();
        for (Protein protein : this) {
            if (protein.getFitness() < best) {
                best = protein.getFitness();
            }
        }
        return best;
    }

    /**
     * Gets the average fitness.
     *
     * @return the average fitness
     */
    public float getAverageFitness() {
        float totalFitness = 0;
        for (Protein protein : this) {
            totalFitness += protein.getFitness();
        }
        return totalFitness / size;
    }

    /**
     * Gets the average energy.
     *
     * @return the average energy
     */
    public float getAverageEnergy() {
        float totalEnergy = 0;
        for (Protein protein : this) {
            totalEnergy += protein.getEnergy();
        }
        return totalEnergy / size;
    }

    public abstract void updateLastTwo();

    /**
     * Gets the lowest energy.
     *
     * @return the lowest energy
     */
    public Protein getLowestEnergy() {
        Protein lowestProtein = getByRef(0);
        for (Protein protein : this) {
            if (lowestProtein.getEnergy() < protein.getEnergy())
                lowestProtein = protein;
        }
        return lowestProtein;
    }

    //public abstract void sort();
}







