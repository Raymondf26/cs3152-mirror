package edu.cornell.gdiac.molechelinmadness.model.event;

import edu.cornell.gdiac.molechelinmadness.model.GameObject;
import edu.cornell.gdiac.molechelinmadness.model.RotatingPlatform;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public class DoorClose implements Event{

    RotatingPlatform door;

    @Override
    public void activate() {
        door.rotateBack();
    }

    @Override
    public void linkObject(GameObject obs) {
        door = (RotatingPlatform) obs;
    }

}
