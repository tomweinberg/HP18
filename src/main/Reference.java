package main;

public class Reference implements Comparable<Object> {

	private double _fitness;
	private final int _index;

	/**
	 * makes new reference
	 * 
	 * @param index
	 *            the index of the reference
	 * @param fitness
	 *            the fitness of the refernce
	 */
	public Reference(int index, double fitness) {
		this._fitness = fitness;
		this._index = index;
	}

	public int compareTo(Object arg0) {
		if (arg0 instanceof Reference) {
			return compareTo((Reference) arg0);
		}
		throw new RuntimeException("cannot compare between reference and "
				+ arg0.getClass() + " arg0 was:" + arg0);
	}

	public int compareTo(Reference arg0) {
		Reference r = (Reference) arg0;
		double delta = _fitness - r._fitness;
		if (delta < 0)
			return -1;
		if (delta == 0)
			return 0;
		return 1;
	}

	/**
	 * Getter
	 * 
	 * @return the index of the reference
	 */
	public int getIndex() {
		return this._index;
	}

	public double getFitness() {
		return this._fitness;
	}

	public void setFitness(double fitness) {
		this._fitness = fitness;
	}

}
