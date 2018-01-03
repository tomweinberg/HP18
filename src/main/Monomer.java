package main;

import javax.vecmath.Vector3f;

/**
 * The basic building block of HP-models.
 */
public class Monomer {
	public final MonomerType type;
	public final int number;
	public final Protein protein;
	// private Grid grid = null;
	private int x, y, z; // Coordinates from vector r.
	private MonomerDirection relativeDirection;
	private Vector3f r = new Vector3f(); // Monomer position
	private Vector3f o = new Vector3f(); // help vector to compute position.
	private Vector3f s = new Vector3f(); // help vector to compute position.
	private Monomer next;
	private Monomer prev;

	// location in the chain -1 if not set

	public Monomer(MonomerType type, int number, Protein protein) {
		this.protein = protein;
		this.type = type;
		this.number = number;
		relativeDirection = MonomerDirection.UNKNOWN;
	}

	/**
	 * The direction of the vector from the previous monomer to this one. A
	 * direction must be legal. That is it cannot be UP or DOWN in a 2D world.
	 * Any change in the value of this field requires recalculation of all the
	 * vectors. Both restrictions are enforced by restricting access to this
	 * field to the set command the explicitly test that the direction is legal
	 * and recalculate the vectors.
	 */
	public MonomerDirection getRelativeDirection() {
		return relativeDirection;
	}

	public void reset() {
		this.relativeDirection = MonomerDirection.UNKNOWN;
	}

	public boolean setRelativeDirection(MonomerDirection relativeDirection) {
		if ((prev == null) & (relativeDirection != MonomerDirection.FIRST))
			throw new RuntimeException(
					"First Monomer direction should be FIRST (f) and not "
							+ relativeDirection);
		relativeDirection.test(protein.dimensions);
		this.relativeDirection = relativeDirection;
		return (calculateVectors());
	}

	
	private boolean calculateVectors() {
		if (prev == null) { // The first Monomer
			r.set(0, 0, 0);
			o.set(0, 0, 1);
			s.set(0, 1, 0);
			x = y = z = 0;
		} else {
			switch (relativeDirection) {
			case FORWARD:
				o.set(prev.o);
				s.set(prev.s);
				break;
			case LEFT:
				o.set(prev.o);
				s.cross(prev.o, prev.s);
				break;
			case RIGHT:
				o.set(prev.o);
				s.cross(prev.s, prev.o);
				break;
			case UP:
				o.negate(prev.s);
				s.set(prev.o);
				break;
			case DOWN:
				o.set(prev.s);
				s.negate(prev.o);
				break;
			default:
				if (relativeDirection != MonomerDirection.END_OF_CHAIN)
					throw new RuntimeException(
							"Weird direction for this method "
									+ relativeDirection);
				if (this != protein.get(protein.size() - 1))
					throw new RuntimeException(
							"Weird conformation with END_OF_CHAIN in the middle ");
			}
			r.add(prev.r, s);
			x = (int) r.x;
			y = (int) r.y;
			z = (int) r.z;
		}

		return protein.updateMonomerOnGrid(this);
	}

	/**
	 * Finds whether this monomer overlaps with one of its predecessors.
	 * 
	 * @return true if overlap ocures
	 */
	public boolean isClashBack() {
		Monomer monomer = prev;
		while (monomer != null) {
			if (r.equals(monomer.r))
				return true;
			monomer = monomer.prev;
		}
		return false;
	}

	/**
	 * Two monomers are the same iff their 'r' vector and their relative
	 * Directions are the same.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Monomer) {
			Monomer other = (Monomer) obj;
			if (this.r.equals(other.r)
					&& this.relativeDirection == other.relativeDirection) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return "{" + type.toString() + number + relativeDirection.oneLetter
				+ "}";
	}

	// -------------------------------- Getters and Setters
	// -----------------------------------------
	public Vector3f getO() {
		return o;
	}

	public void setO(Vector3f o) {
		this.o = o;
	}

	public Vector3f getR() {
		return r;
	}

	public void setR(Vector3f r) {
		this.r = r;
	}

	public Vector3f getS() {
		return s;
	}

	public void setS(Vector3f s) {
		this.s = s;
	}

	public Monomer getNext() {
		return next;
	}

	public void setNext(Monomer next) {
		this.next = next;
	}

	public Monomer getPrev() {
		return prev;
	}

	public void setPrev(Monomer prev) {
		this.prev = prev;
	}

	public int getNumber() {
		return number;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
}


// Asaf's unused method 

/*
// return calculateVectorWithout3f();//TODO:may be consider to abandon
// the use of vector3f and do it manually
* BoardDireaction turningTo; private boolean calculateVectorWithout3f() {
* 
* switch (relativeDirection){ case FORWARD: this.turningTo=prev.turningTo;
* switch (this.prev.turningTo){ case FORWARD: this.y=prev.y++; break; case
* BACKWARD: this.y=prev.y--; break; case LEFT: this.x=prev.x--; break; case
* RIGHT: this.x=prev.x++; break; case UP: this.z=prev.z++; break; case
* DOWN: this.z=prev.z--; break; default:
* 
* }
* 
* break; case LEFT: if (this.prev.turningTo.equals(BoardDireaction.RIGHT))
* this.turningTo=BoardDireaction.FORWARD; else
* this.turningTo=BoardDireaction.values()[this.prev.turningTo.ordinal()+1];
* 
* switch (this.prev.turningTo){ case UP:
* 
* case FORWARD: this.x=prev.x--; break; case DOWN: case BACKWARD:
* this.x=prev.x++; break; case LEFT: this.y=prev.y--; break; case RIGHT:
* this.y=prev.y++; break;
* 
* default:
* 
* } break; case RIGHT: if
* (this.prev.turningTo.equals(BoardDireaction.FORWARD))
* this.turningTo=BoardDireaction.RIGHT; else
* this.turningTo=BoardDireaction.values()[this.prev.turningTo.ordinal()-1];
* 
* switch (this.prev.turningTo){ case UP:
* 
* case FORWARD: this.x=prev.x++; break; case DOWN: case BACKWARD:
* this.x=prev.x--; break; case LEFT: this.y=prev.y++; break; case RIGHT:
* this.y=prev.y--; break;
* 
* default:
* 
* } break; case UP: this.turningTo=BoardDireaction.UP;
* 
* z++;
* 
* break; case DOWN: this.turningTo=BoardDireaction.DOWN; z--; break;
* default: if (relativeDirection != MonomerDirection.END_OF_CHAIN) throw
* new
* RuntimeException("Weird direction for this method "+relativeDirection);
* if (this != protein.get(protein.size()-1)) throw new
* RuntimeException("Weird conformation with END_OF_CHAIN in the middle ");
* }
* 
* 
* 
* return protein.updateMonomerOnGrid(this);
* 
* }
*/
