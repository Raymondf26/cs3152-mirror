package edu.cornell.gdiac.molechelindescent.model;

public interface Interactee {

    public enum InteracteeType {
        DOOR,
        DUMBWAITER
    }

    public InteracteeType getType();

    public void setType(InteracteeType type);

}
