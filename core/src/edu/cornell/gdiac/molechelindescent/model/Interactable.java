package edu.cornell.gdiac.molechelindescent.model;

public interface Interactable {

    public enum InteractableType {
        BUTTON,
        PRESSURE_PLATE
    }

    public InteractableType getType();

    public void setType(InteractableType type);

}
