package mutation;

import java.util.Iterator;
import java.util.Vector;
import javax.vecmath.Vector3f;

import main.Pair;
import main.MonomerDirection;

/**
 * A possible conformational change (mutation in the GA jargon) in a toy protein
 * conformation.
 */
public class Mutation {

	/** The first monomer vector. */
	private Vector3f _firstMonomerVector;

	/** The last monomer vector. */
	private Vector3f _lastMonomerVector;

	/** The confomation. */
	private MonomerDirection[] _confomation;

	/** The length. */
	private int _length;

	/** The probability. */
	private float _probability;

	/** The adjacency list. */
	private Vector<Pair<Integer, Integer>> _adjacencyList;

	/**
	 * Instantiates a new mutation.
	 * 
	 * @param firstMonomerVector
	 *            the first monomer vector
	 * @param lastMonomerVector
	 *            the last monomer vector
	 * @param conformationString
	 *            the conformation string
	 * @param probability
	 *            the probability
	 */
	public Mutation(Vector3f firstMonomerVector, Vector3f lastMonomerVector,
			String conformationString, float probability) {
		this._firstMonomerVector = firstMonomerVector;
		this._lastMonomerVector = lastMonomerVector;

		this._length = conformationString.length();
		this._confomation = new MonomerDirection[_length];
		for (int i = 0; i < this._confomation.length; i++) {
			this._confomation[i] = MonomerDirection
					.byNumber((int) conformationString.charAt(i) - 48);
		}

		this._probability = probability;
		this._adjacencyList = new Vector<Pair<Integer, Integer>>();
	}

	public String toString() {
		String ans = "";
		ans = "{";
		for (int i = 0; i < _length; i++) {
			ans += _confomation[i].oneLetter;
		}
		ans += "}\t<" + _firstMonomerVector + ">\t<" + _lastMonomerVector
				+ ">\t<";
		Iterator<Pair<Integer, Integer>> iter = _adjacencyList.listIterator();
		Pair<Integer, Integer> pair;
		while (iter.hasNext()) {
			pair = iter.next();
			ans += "<" + pair.getFirst() + "," + pair.getSecond() + ">:";
		}
		if (_adjacencyList.size() != 0) {
			ans = ans.substring(0, ans.length() - 1);
		}
		ans += ">";
		return ans;
	}

	/**
	 * Gets the confomation.
	 * 
	 * @return the confomation
	 */
	public MonomerDirection[] getConfomation() {
		return _confomation;
	}

	/**
	 * Gets the first monomer vector.
	 * 
	 * @return the first monomer vector
	 */
	public Vector3f getFirstMonomerVector() {
		return _firstMonomerVector;
	}

	/**
	 * Sets the first monomer vector.
	 * 
	 * @param firstMonomerVector
	 *            the new first monomer vector
	 */
	public void setFirstMonomerVector(Vector3f firstMonomerVector) {
		this._firstMonomerVector = firstMonomerVector;
	}

	/**
	 * Gets the last monomer vector.
	 * 
	 * @return the last monomer vector
	 */
	public Vector3f getLastMonomerVector() {
		return _lastMonomerVector;
	}

	/**
	 * Sets the last monomer vector.
	 * 
	 * @param lastMonomerVector
	 *            the new last monomer vector
	 */
	public void setLastMonomerVector(Vector3f lastMonomerVector) {
		this._lastMonomerVector = lastMonomerVector;
	}

	/**
	 * Gets the length.
	 * 
	 * @return the length
	 */
	public int getLength() {
		return _length;
	}

	/**
	 * Sets the length.
	 * 
	 * @param length
	 *            the new length
	 */
	public void setLength(int length) {
		this._length = length;
	}

	/**
	 * Gets the probability.
	 * 
	 * @return the probability
	 */
	public float getProbability() {
		return _probability;
	}

	/**
	 * Sets the probability.
	 * 
	 * @param probability
	 *            the new probability
	 */
	public void setProbability(float probability) {
		this._probability = probability;
	}

	/**
	 * Gets the adjencies list.
	 * 
	 * @return the adjencies list
	 */
	public Vector<Pair<Integer, Integer>> getAdjacencyList() {
		return _adjacencyList;
	}

	/**
	 * add a pair to the adjency list
	 * 
	 * @param pair
	 *            the pair to be added
	 */
	public void addToAdjacencyList(Pair<Integer, Integer> pair) {
		this._adjacencyList.add(pair);
	}
}
