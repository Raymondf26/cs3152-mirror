package edu.cornell.gdiac.molechelinmadness.model.interactor;

import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.molechelinmadness.model.event.Event;

public interface Interactor {

    public enum InteractorType {
        BUTTON,
        PRESSURE_PLATE
    }

    /**
     *
     * Give the Array of trigger linked events.
     *
     */
    public Array<Event> getTriggerLinks();

    /**
     *
     * Give the Array of detrigger linked events.
     *
     */
    public Array<Event> getDetriggerLinks();

    /**
     * Trigger represents the event that occurs when the interactor starts being interacted with.
     *
     * @param toAdd Event to be added to the trigger events.
     */
    public void addTriggerEvent(Event toAdd);

    /**
     * Detrigger represents the event that occurs when the interactor stops being interacted with.
     *
     * @param toAdd Event to be added to the detrigger events.
     */
    public void addDetriggerEvent(Event toAdd);

}
