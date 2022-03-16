package edu.cornell.gdiac.molechelinmadness.model.interactor;

import edu.cornell.gdiac.molechelinmadness.model.interactee.Interactee;

public interface Interactor {

    public enum InteractableType {
        BUTTON,
        PRESSURE_PLATE
    }

    public class Link {
        public Interactee interactee;
        public int effect;
    }

    public InteractableType getType();

    public void setType(InteractableType type);

    public Link[] getLinks();

    public void setLinks(Link[] links);

}
