package edu.cornell.gdiac.molechelinmadness.model.event;

import edu.cornell.gdiac.molechelinmadness.model.GameObject;
import edu.cornell.gdiac.molechelinmadness.model.RotatingPlatformV2;

public class DoorV2Close implements Event{

    RotatingPlatformV2 door;

    @Override
    public void activate() {
        door.storeAngle(0f);
    }

    @Override
    public void linkObject(GameObject obj) {
        door = (RotatingPlatformV2) obj;
    }
}
