package edu.cornell.gdiac.molechelinmadness.model.interactor;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import edu.cornell.gdiac.molechelinmadness.model.GameObject;
import edu.cornell.gdiac.molechelinmadness.model.event.Event;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public abstract class Interactor extends MessageDispatcher implements GameObject{

    Array<Event> events;
    boolean triggered;
    boolean contact;

    public Interactor() {
        events = new Array<>();
        triggered = false;
        contact = false;
    }

    @Override
    public abstract void refresh(float dt);

    public abstract void setDrawScale(Vector2 scale);

    public abstract Obstacle getBody();

    public void addEvent(Event event) {
        events.add(event);
    }

    protected void triggerOn() {
        if (!triggered) {
            triggered = true;
            activate();
        }
    }

    protected void triggerOff() {
        if (triggered) {
            triggered = false;
            deactivate();
        }
    }

    private void activate() {
        int i = 1;
        for (Event event : events) {
            dispatchMessage(this, i, event); //Event numbers 1 and up. Positive means activate.
            i++;
        }
    }

    private void deactivate() {
        int i = 1;
        for (Event event : events) {
            dispatchMessage(this, -i, event); //Event numbers -1 and down. Negative means deactivate.
            i++;
        }
    }
}
