package edu.cornell.gdiac.molechelinmadness.model.interactor;

import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.molechelinmadness.model.event.Event;

public class Button implements Interactor {


    @Override
    public InteractorType getType() {
        return null;
    }

    @Override
    public Array<Event> getTriggerLinks() {
        return null;
    }

    @Override
    public Array<Event> getDetriggerLinks() {
        return null;
    }

    @Override
    public void addTriggerEvent(Event toAdd) {

    }

    @Override
    public void addDetriggerEvent(Event toAdd) {

    }
}
