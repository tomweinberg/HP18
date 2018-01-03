package main;

import java.util.Iterator;
import java.util.Random;

import simpleGA.GAOptimizer;

import java.util.ArrayList;

/**
 * An HP - model of toy protein.
 */
public class Protein extends ArrayList<Monomer> implements Comparable<Protein> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2614207202416458330L;
	protected float fitness;
	protected float energy;
	private static final int MAX_TRIES = 10;
	public final Sequence sequence;
	protected Grid grid;
	private int size;
	public final Random random;
	public final Dimensions dimensions;
	public final String name;
	private int id;
	private static Conformation originalConformation1 = null;
	private static Conformation originalConformation2 = null;

	/**
	 * A temporary object will serve several methods. It is intended to save the
	 * object creation time for frequently used methods. it is expected that
	 * every method clears this object before it is used.
	 */
	private static Conformation tempConformation = null;

	protected Conformation conformation;

	public Protein(Dimensions dimensions, Sequence sequence, Random random,
			Grid grid, String name) {
		this(dimensions, sequence, random, grid, name, true);
	}
	public Protein(Dimensions dimensions, Sequence sequence, Random random,
			Grid grid, String name, boolean createFlag) {
		super();
		this.sequence = sequence;
		this.grid = grid;
		this.random = random;
		id = this.random.nextInt();
		this.dimensions = dimensions;
		this.name = name;
		size = sequence.size();
		int i = 0;
		for (MonomerType type : sequence) {
			add(new Monomer(type, i, this));
			i++;
		}
		Monomer prev = null;
		for (Monomer monomer : this) {
			if (prev != null)
				prev.setNext(monomer);
			monomer.setPrev(prev);
			prev = monomer;
		}
		if (createFlag) 
			initiateRandomConformation();
		
	}
	
	public Conformation getConformation() {
		return conformation;
	}
	
	private void initiateRandomConformation(){ //initiate the confirmation ,the code from the constructor
		conformation = getRandomConformation(dimensions, random, MAX_TRIES
				* MAX_TRIES*sequence.size()); // An arbitrary numbers of attempts before we give
								// up.
		updateFitness();
		conformation.setFitness(fitness);
		conformation.setEnergy(energy);
		if (tempConformation == null)
			tempConformation = new Conformation(size);
	}

	/**
	 * Grows a random protein conformation one monomer. after the other The
	 * conformation needs to be self-avoiding and thus a random building process
	 * may end-up in a dead end. In that case the building process is repeated.
	 * 
	 * @param maxTries
	 *            maximal numbers of tries before we give up.
	 */
	private Conformation getRandomConformation(Dimensions dimensions,
			Random random, int maxTries) {
		Conformation out = new Conformation(size);
		boolean success = false;
		int nTries = 0;
		while ((!success) && (nTries < maxTries)) { // Each round of this loop
													// is an attempt to build
													// the model
			success = getRandomConformation(dimensions, random, out);
			nTries++;
		}
		if (!success)
			throw new RuntimeException("Failed to create a random conformation");
		return out;
	}

	/**
	 * An attempts to grow a random protein conformation one monomer. after the
	 * other.
	 * 
	 * @return true if succeeded, false if reached a dead-end.
	 */
	private boolean getRandomConformation(Dimensions dimensions, Random random,
			Conformation conformation) {
		reset();
		conformation.clear();
		boolean first = true;
		for (Monomer monomer : this) {
			if (first) {
				conformation.add(MonomerDirection.FIRST);
				if (!monomer.setRelativeDirection(MonomerDirection.FIRST))
					throw new RuntimeException(
							"Why not accept first direction?");
				first = false;
			} else {
				int nTries = 0;
				boolean success = false;
				MonomerDirection newDirection;
				while ((!success) && (nTries < MAX_TRIES)) {
					newDirection = MonomerDirection.getRandomDirection(
							dimensions, random);
					success = monomer.setRelativeDirection(newDirection);
					nTries++;
				}
				if (nTries >= MAX_TRIES)
					return false; // Apparently we are in a dead end
				conformation.add(monomer.getRelativeDirection());
			}
		}
		return true;
	}

	/**
	 * Assign a conformation to the protein. The directions of the monomers are
	 * set iteratively and each monomer checkes that the direction assined to it
	 * is valid. The correctness of this method depends on the assumption that
	 * the grid is empty (all its cells are null) at the beginning of the
	 * execution.
	 * 
	 * @param conformation
	 * @return true if succeeded.
	 */
	public int setConformation(Conformation conformation) {
		if (GAOptimizer.debug)
			if (!grid.testEmpty())
				throw new RuntimeException("Grid must be empty at this stage");
		Boolean success;
		reset();
		this.conformation.clear();
		int i = 0;

		for (Monomer monomer : this) {
			this.conformation.add(conformation.get(i));
			success = monomer.setRelativeDirection(conformation.get(i));
			if (!success) {
				fitness = Float.MIN_VALUE;
				grid.reset(this, monomer);
				return i;
			}
			i++;
		}
		updateFitness();
		this.conformation.setFitness(fitness);
		this.conformation.setEnergy(energy);
		grid.reset(this);
		return i;
	}
	
	public void copyConformation(Protein other){
		setConformation(other.conformation);
	}

	public void reset() {
		for (Monomer monomer : this) {

			monomer.reset();

		}
		fitness = Float.MIN_VALUE;
		energy =Float.MAX_VALUE;

		if (conformation != null)
			conformation.clear();
		if (grid.getCurrentProteinOnGrid() != null
				&& grid.getCurrentProteinOnGrid().equals(this))
			grid.reset(this);
	}

	/*
	 * private void cleanGrid() { // for (Monomer monomer:this) {
	 * grid.reset(this); // } }
	 */

	public float evaluateEnergy() {
		energy = 0;
		for (Monomer monomer : this) {
			energy -= grid.countContacts(monomer);
		}
		conformation.setEnergy(energy);
		return energy;
	}

	public float updateFitness() {
		fitness = -evaluateEnergy();
		conformation.setFitness(fitness);
		return fitness;
	}

	public int compareTo(Protein protein) {
		float delta = fitness - protein.getFitness();
		if (delta == Float.NaN)
			throw new RuntimeException("Weird delta");
		if (delta < 0)
			return -1;
		if (delta > 0)
			return 1;
		return 0;
	}

	/**
	 * Crossover event between two proteins.(in1 & in2) into two other proteins
	 * (out1 & out2). If the crossover fails the out proteins are reset.
	 * 
	 * @param in1
	 *            the first protein that will be crossedOver
	 * @param in2
	 *            the second protein that will be crossedOver
	 * @param out1
	 *            will be written with the result will be reset upon failure
	 * @param out2
	 *            will be written with the result will be reset upon failure
	 */
	public static void crossover(Protein in1, Protein in2, Protein out1,
			Protein out2, Random random) {
		if (originalConformation1 == null) {
			originalConformation1 = new Conformation(in1.size());
			originalConformation2 = new Conformation(in1.size());
		}
		tempConformation.clear();
		originalConformation1.copy(out1.conformation);
		originalConformation2.copy(out2.conformation);
		int size = in1.size(); // It is the size of all proteins anyway.

		int tempPoint;
		int point = (int) (random.nextFloat() * size);

		for (int i = 0; i < point; i++) {
			Monomer monomer = in1.get(i);
			tempConformation.add(i, monomer.getRelativeDirection());
		}
		for (int i = point; i < size; i++)
			tempConformation.add(i, in2.get(i).getRelativeDirection());
		tempPoint = out1.setConformation(tempConformation);
		if (tempPoint < point)
			throw new RuntimeException(
					"One could expect that copying directions from a protein would not create any problem.");
		if (tempPoint < size) {// Cross over failed for out1. Its conformation is
								// meaningless and it can be recycled.
			out1.reset();
			out1.setConformation(originalConformation1);
		}
		tempConformation.clear();
		for (int i = 0; i < point; i++)
			tempConformation.add(i, in2.get(i).getRelativeDirection());
		for (int i = point; i < size; i++)
			tempConformation.add(i, in1.get(i).getRelativeDirection());
		tempPoint = out2.setConformation(tempConformation);
		if (tempPoint < point)
			throw new RuntimeException(
					"One could expect that copying directions from a protein would not create any problem.");
		if (tempPoint < size) {// Cross over failed for out2. Its conformation is
								// meaningless and it can be recycled.
			out2.reset();
			out2.setConformation(originalConformation2);
		}
	}

	public String toString() {
		String out = name + "\nfitness:" + fitness + "\n";
		out += "energy: " + energy + "\n";
		out += "sequnce: " + sequence + "\n";
		out += conformation + "\n";
		int x, y, prevX = '.', prevY = '.';
		Iterator<Monomer> iterator = iterator();
		while (iterator.hasNext())
			out += iterator.next().toString();
		out += "\n\n";
		if (dimensions == Dimensions.TWO) {
			MinMax minMax = new MinMax(this);
			char[][] charMatrix = new char[2 * (minMax.lengthX() + 1)][2 * (minMax
					.lengthY() + 1)];
			Monomer prev = null;
			for (char[] row : charMatrix)
				for (int i = 0; i < row.length; i++)
					row[i] = ' ';
			boolean first = true;
			for (Monomer monomer : this) {
				x = 2 * (monomer.getX() - minMax.minX);
				y = 2 * (monomer.getY() - minMax.minY);
				if (monomer.type == MonomerType.H) {
					if (first)
						charMatrix[x][y] = 'H';
					else
						charMatrix[x][y] = 'h';
				} else {
					if (first)
						charMatrix[x][y] = 'P';
					else
						charMatrix[x][y] = 'p';
				}
				first = false;
				if (prev != null) {
					if (prevX != x)
						charMatrix[(x + prevX) / 2][y] = '|';
					else
						charMatrix[x][(y + prevY) / 2] = '-';
				}
				prevX = x;
				prevY = y;
				prev = monomer;
			}
			for (char[] row : charMatrix) {
				for (int i = 0; i < row.length; i++)
					out += row[i];
				out += "\n";
			}
		}
		return out;
	}

	/**
	 * delegator for the grid function update monomer
	 * 
	 * @param monomer
	 *            the monomer that should be updated on the grid
	 * @return true if the update succeed
	 */
	public boolean updateMonomerOnGrid(Monomer monomer) {
		return grid.update(monomer);
	}

	/**
	 * public void calculateVectors() { for (Monomer monomer:this)
	 * monomer.calculateVectors(); }
	 **/

	// ---------------------------------------------------- getters &setters
	// ----------------------------------------------
	public Grid getGrid() {
		return grid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((conformation == null) ? 0 : conformation.hashCode());
		result = prime * result
				+ ((dimensions == null) ? 0 : dimensions.hashCode());
		result = prime * result + Float.floatToIntBits(energy);
		result = prime * result + Float.floatToIntBits(fitness);
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Protein other = (Protein) obj;
		if (conformation == null) {
			if (other.conformation != null)
				return false;
		} else if (!conformation.equals(other.conformation))
			return false;
		if (dimensions != other.dimensions)
			return false;
		if (Float.floatToIntBits(energy) != Float.floatToIntBits(other.energy))
			return false;
		if (Float.floatToIntBits(fitness) != Float
				.floatToIntBits(other.fitness))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public float getEnergy() {
		return energy;
	}

	public float getFitness() {
		return fitness;
	}
}
