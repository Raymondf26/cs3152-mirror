package edu.cornell.gdiac.molechelinmadness.model.interactor;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import edu.cornell.gdiac.molechelinmadness.model.GameObject;
import edu.cornell.gdiac.molechelinmadness.model.Interactive;
import edu.cornell.gdiac.molechelinmadness.model.Mole;
import edu.cornell.gdiac.molechelinmadness.model.event.Event;

public abstract class Interactor2 extends MessageDispatcher implements Interactive {

    ArrayMap<GameObject, Event> events;
    boolean triggered;
    boolean contact;

    public Interactor2() {
        events = new ArrayMap<>();
        triggered = false;
        contact = false;
    }

    public void addEvent(GameObject object, Event event) {
        events.put(object, event);
    }

    @Override
    public void resolveBegin(Mole mole) {
        contact = true;
    }

    @Override
    public void resolveEnd(Mole mole) {
        contact = false;
    }

    @Override
    public abstract void refresh(float dt);


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
        for (ObjectMap.Entry<GameObject, Event> entry : events) {
            GameObject obj = entry.key;
            Event event = entry.value;
            dispatchMessage(this, obj, 0, event);
        }
    }

    private void deactivate() {
        for (ObjectMap.Entry<GameObject, Event> entry : events) {
            GameObject obj = entry.key;
            Event event = entry.value;
            dispatchMessage(this, obj, 1, event);
        }
    }
}
