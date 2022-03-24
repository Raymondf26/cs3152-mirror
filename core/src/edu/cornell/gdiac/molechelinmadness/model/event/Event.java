package edu.cornell.gdiac.molechelinmadness.model.event;

import edu.cornell.gdiac.molechelinmadness.model.GameObject;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public interface Event {

    public void activate();

    public void linkObject(GameObject obj);

}
