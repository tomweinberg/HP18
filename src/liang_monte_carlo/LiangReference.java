package liang_monte_carlo;

import main.Reference;

/**
 * Created by tomw on 26/12/2017
 */
public class LiangReference implements Comparable<Object> {

    private  int _index;
    private final float _temp;
    private float _energy;

    /**
     * makes new reference
     *
     * @param index  the index of the reference
     * @param energy the energy of the refernce
     */
    public LiangReference(int index, float energy, float temp) {
        this._energy = energy;
        this._index = index;
        this._temp = temp;
    }

    public int compareTo(Object arg0) {
        if (arg0 instanceof Reference) {
            return compareTo((LiangReference) arg0);
        }
        throw new RuntimeException("cannot compare between reference and "
                                           + arg0.getClass() + " arg0 was:" + arg0);
    }

    public int compareTo(LiangReference arg0) {
        LiangReference r = arg0;
        double delta = _energy - r._energy;
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

    public void setIndex(int _index) {
         this._index=_index;
    }

    public float getEnergy() {
        return this._energy;
    }

    public void setEnergy(float energy) {
        this._energy = energy;
    }

    public float getTemperature() {
        return (_index+1)*this._temp;
    }
}
