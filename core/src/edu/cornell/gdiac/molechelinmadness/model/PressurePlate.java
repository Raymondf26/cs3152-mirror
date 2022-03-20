package edu.cornell.gdiac.molechelinmadness.model;

import edu.cornell.gdiac.molechelinmadness.model.event.Event;

import java.util.ArrayList;

public class PressurePlate {

    ArrayList<Event> TriggerEventList = new ArrayList<Event>();
    ArrayList<Event> DetriggerEventList = new ArrayList<Event>();

    public void activate(){
        for (Event temp : TriggerEventList) {
            temp.activate();
        }
    }

    public void deactivate() {
        for (Event temp : DetriggerEventList) {
            temp.activate();
        }
    }

    public void addTriggerEvent(Event toAdd) {
        TriggerEventList.add(toAdd);
    }

    public void addDetriggerEvent(Event toAdd) {
        DetriggerEventList.add(toAdd);
    }
}
