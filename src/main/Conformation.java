package main;

import java.util.ArrayList;

/**
 * 
 */
public class Conformation extends ArrayList<MonomerDirection> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6741557062826032591L;
	/**
	 * 
	 */

	private float fitness;
	private boolean ended;

	private float energy;

	public Conformation(int size) {
		super(size);
		fitness = Float.MIN_VALUE;
		energy = Float.MAX_VALUE;
		ended = false;
	}

	public void clear() {
		super.clear();
		fitness = Float.MIN_VALUE;
		energy = Float.MAX_VALUE;
	}
	
	public void copy(Conformation other) {
		clear();
		for (MonomerDirection monomerDirection:other)
			add(monomerDirection);
	}

	/**
	 * add a new direction to the conformation
	 * 
	 */
	@Override
	public boolean add(MonomerDirection direction) {
		if (ended)
			throw new RuntimeException(
					"Cannot add a direction after END_OF_CHAIN " + this);
		if (direction.equals(MonomerDirection.UNKNOWN))
			throw new RuntimeException("Weird direction to add: " + direction);
		if (direction.equals(MonomerDirection.END_OF_CHAIN))
			ended = true;
		if ((size() == 0) & (direction != MonomerDirection.FIRST))
			throw new RuntimeException(
					"The first direction must be of FIRST type (f)  and not "
							+ direction);
		return super.add(direction);
	}

	@Override
	public String toString() {
		String out = "Conformation " + fitness + " " + energy + " ";
		for (MonomerDirection md : this) {
			out += md.oneLetter;
		}
		return out;
	}

	public float getFitness() {
		return fitness;
	}

	public void setFitness(float fitness) {
		this.fitness = fitness;
	}

	public float getEnergy() {
		return energy;
	}

	public void setEnergy(float energy) {
		this.energy = energy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (ended ? 1231 : 1237);
		result = prime * result + Float.floatToIntBits(energy);
		result = prime * result + Float.floatToIntBits(fitness);
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
		Conformation other = (Conformation) obj;
		if (ended != other.ended)
			return false;
		if (Float.floatToIntBits(energy) != Float.floatToIntBits(other.energy))
			return false;
		if (Float.floatToIntBits(fitness) != Float
				.floatToIntBits(other.fitness))
			return false;
		return true;
	}

	@Override
	public Conformation clone() {
		Conformation ans = new Conformation(this.size());
		ans.ended = this.ended;
		ans.energy = this.energy;
		ans.fitness = this.fitness;
		for (int i = 0; i < this.size(); i++) {
			ans.add(this.get(i));
		}
		return ans;

	}
}
