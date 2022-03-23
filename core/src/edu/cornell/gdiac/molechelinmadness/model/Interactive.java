package edu.cornell.gdiac.molechelinmadness.model;

public interface Interactive {

    /** 0 refers to hands, 1 refers to feet */
    public int getType();

    public void resolveBegin(Mole mole);

    public void resolveEnd(Mole mole);

}
