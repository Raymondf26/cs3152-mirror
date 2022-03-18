package edu.cornell.gdiac.molechelinmadness.model.interactor;

import edu.cornell.gdiac.molechelinmadness.model.event.Event;

public interface Interactor {

    public enum InteractableType {
        BUTTON,
        PRESSURE_PLATE
    }

    public InteractableType getType();

    public void setType(InteractableType type);

    public Event[] getLinks();

    public void setLinks(Event[] links);

}
