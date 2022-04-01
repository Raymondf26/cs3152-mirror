package edu.cornell.gdiac.molechelinmadness.model.interactor;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import edu.cornell.gdiac.molechelinmadness.model.GameObject;
import edu.cornell.gdiac.molechelinmadness.model.event.Event;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

/**
 * A generic Interactor object with no Box2D body attached. All Interactors should extend this class.
 */
public abstract class Interactor extends MessageDispatcher implements GameObject{

    private Array<Event> events;
    private boolean triggered;
    private boolean contact;

    public Interactor() {
        events = new Array<>();
        triggered = false;
        contact = false;
    }

    /**
     * This method is called every frame in the main update loop of the game.
     *
     * In the case of an Interactor, all this has to handle is when to call triggerOn() and when to call triggerOff().
     *
     * @param dt the time passed in seconds since the previous frame
     */
    @Override
    public abstract void refresh(float dt);

    /**
     * Sets the drawscale of the attached Box2D Body
     *
     * @param scale the drawscale
     */
    public abstract void setDrawScale(Vector2 scale);

    /**
     *
     * @return the Box2D Body attached to this Interactor object.
     */
    public abstract Obstacle getBody();

    /**
     * Adds an event to the list of events that will be fired when this Interactor is triggered.
     *
     * @param event the event to be added
     */
    public void addEvent(Event event) {
        events.add(event);
    }

    /**
     * Fires all the events attached to this Interactor if we are not already in the triggered state.
     *
     * Call this method in the refresh(dt) method.
     */
    protected void triggerOn() {
        if (!triggered) {
            triggered = true;
            activate();
        }
    }

    /**
     * Fires all the "reverse" events attached to this Interactor if we are not already in the un-triggered state.
     * For example, if the event is to open a door by 90 degrees, this will dispatch the message to return the door to its original rotation.
     *
     * Call this method in the refresh(dt) method.
     */
    protected void triggerOff() {
        if (triggered) {
            triggered = false;
            deactivate();
        }
    }

    /**
     * This private method actually dispatches the messages to the registered listeners.
     * No need to worry about this method.
     */
    private void activate() {
        int i = 1;
        for (Event event : events) {
            dispatchMessage(this, i, event); //Event numbers 1 and up. Positive means activate.
            i++;
        }
    }

    /**
     * This private method actually dispatches the "deactivate" messages to the registered listeners.
     * No need to worry about this method.
     */
    private void deactivate() {
        int i = 1;
        for (Event event : events) {
            dispatchMessage(this, -i, event); //Event numbers -1 and down. Negative means deactivate.
            i++;
        }
    }
}
