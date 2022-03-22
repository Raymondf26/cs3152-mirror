package edu.cornell.gdiac.molechelinmadness.model.event;

import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public interface Event {

    public void activate();

    public void linkObject(Obstacle obs);

}
