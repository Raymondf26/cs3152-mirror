package edu.cornell.gdiac.molechelinmadness.model.event;

import edu.cornell.gdiac.molechelinmadness.model.Dumbwaiter;
import edu.cornell.gdiac.molechelinmadness.model.GameObject;

public class DumbwaiterUp implements Event{

    Dumbwaiter dumb;

    @Override
    public void activate() {
        dumb.sendUp();
    }

    @Override
    public void linkObject(GameObject obj) {
        dumb = (Dumbwaiter) obj;
    }
}
