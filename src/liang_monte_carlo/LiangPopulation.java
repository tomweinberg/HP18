package liang_monte_carlo;

import main.Configuration;
import main.Protein;
import mutation.MutationManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by tomw on 26/12/2017
 */
public class LiangPopulation extends PopulationAbstract {

    private static float temperature;
    public ArrayList<LiangReference> reference;

    /**
     * Instantiates a new population. filling the population with random (Legal)
     * Proteins
     *
     * @param config
     * @param rand           the random number to be used in the population
     * @param mutationManger the mutation manger
     */
    public LiangPopulation(Configuration config, Random rand, MutationManager mutationManger) {
        super(config, rand, mutationManger);
        temperature = config.finalTemperature;

        this.reference = new ArrayList<LiangReference>();
        for (int i = 0; i < size; i++) {
            Protein protein = new Protein(dimensions, sequence, random, grid,
                                          "protein_" + i);

            add(protein);
            LiangReference r = new LiangReference(i, protein.getEnergy(), config.finalTemperature / size);
            this.reference.add(r);
            grid.reset(protein);
        }
    }

    @Override
    public Protein getFirst() {
        return getByRef(0);
    }

    public float getPopulationTemperature() {
        return temperature;
    }

    public void switchProtein(Protein protein) {
        for (int i = 0; i < size(); i++) {
            if (get(i).equals(protein)) {
                set(i, protein);
                this.reference.get(i).setEnergy(protein.getEnergy());
            }
        }
    }

    public int findRefPlace(int index) {
        int realIndex = 0;

        for (int i = 0; i < reference.size(); i++) {
            if (reference.get(i).getIndex() == index)
                realIndex = i;
        }
        return realIndex;
    }
    
    @Override
    public Protein getByRef(int index) {
        int i = reference.get(index).getIndex();
        return this.get(i);
    }

    @Override
    public void updateLastTwo() {
        int s = size();
        int last = reference.get(s - 1).getIndex();
        reference.get(s - 1).setEnergy(get(last).getEnergy());
        int seconedToLast = reference.get(s - 2).getIndex();
        reference.get(s - 2).setEnergy(get(seconedToLast).getEnergy());
    }

    public void exchangeProtein(int index1Pop,int index2Pop,int index1Ref, int index2Ref) {
        Protein P1 = get(index1Pop);
        set(index1Pop, get(index2Pop));
        set(index2Pop, P1);
        reference.get(index1Ref).setEnergy(get(index1Pop).getEnergy());
        reference.get(index1Ref).setIndex(index2Ref);
        reference.get(index2Ref).setEnergy(get(index2Pop).getEnergy());
        reference.get(index2Ref).setIndex(index1Ref);
    }

    public boolean exchangeProbability(int index1, int index2) {
        float e1 = reference.get(index1).getEnergy();
        float t1 = 1 / reference.get(index1).getTemperature();

        float e2 = reference.get(index2).getEnergy();
        float t2 = 1 / reference.get(index2).getTemperature();

        float probability = Math.min((float) Math.exp((e1 - e2) * (t1 - t2)), 1);

        if (probability >= Math.random())
            return true;
        return false;
    }

    public float PopulationBoltzmann() {
        float BoltzmannP = 0;
        for (int i = 0; i < reference.size(); i++) {
            BoltzmannP = BoltzmannP + calculateBoltzmann(reference.get(i));
        }
        return BoltzmannP;
    }

    private float calculateBoltzmann(LiangReference protein) {
        float energy = protein.getEnergy();
        float temperature = protein.getTemperature();
        float probability = (float) Math.exp(-energy / temperature);
        if (Float.isNaN(probability) || Float.isInfinite(probability)) {
            throw new RuntimeException("Boltzmann probability is " + probability + " " + energy + " " + temperature);
        }
        return probability;
    }
}
