package edu.cornell.gdiac.molechelinmadness.model.interactee;

public interface Interactee {

    public enum InteracteeType {
        DOOR,
        DUMBWAITER
    }

    public InteracteeType getType();

    public void setType(InteracteeType type);

}
