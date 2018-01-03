package main;

import liang_monte_carlo.PopulationAbstract;
import mutation.MutationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * A population of protein conformations for a Genetic Algorithm.
 */
@SuppressWarnings("serial")
public class Population extends PopulationAbstract {

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
    public Population(Configuration config, Random rand, MutationManager mutationManger) {
        super(config, rand, mutationManger);
        this.config = config;
        random = rand;
        size = config.populationSize;

        dimensions = config.dimensions;
        this.mutationManger = mutationManger;
        sequence = new Sequence(config.sequence);

        grid = Grid.getInstance(config.sequence.length(), dimensions);

        this.reference = new ArrayList<Reference>();
        for (int i = 0; i < size; i++) {
            Protein protein = new Protein(dimensions, sequence, random, grid,
                                          "protein_" + i);

            add(protein);
            Reference r = new Reference(i, protein.getFitness());
            this.reference.add(r);
            grid.reset(protein);
        }

        Collections.sort(this.reference, Collections.reverseOrder());
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
        int i = this.reference.get(index).getIndex();
        return this.get(i);
    }

    /**
     * Gets the first.
     *
     * @return the first
     */
    public Protein getFirst() {
        sort();
        return getByRef(0);
    }

    /**
     * Mutate.
     */
    public void mutate() {
        Protein in = chooseProtein();
        Protein out = getLast();
        mutationManger.mutate(in, out, 10);
        this.reference.get(size() - 1).setFitness(out.getFitness());

        // Collections.sort(this);
        Collections.sort(this.reference);
    }

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

    public void updateLastTwo() {

        int s = size();
        int last = this.reference.get(s - 1).getIndex();
        this.reference.get(s - 1).setFitness(get(last).getFitness());
        int seconedToLast = this.reference.get(s - 2).getIndex();
        this.reference.get(s - 2).setFitness(get(seconedToLast).getFitness());
    }

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

    public void sort() {
        Collections.sort(this.reference, Collections.reverseOrder());
    }
}
