package edu.cornell.gdiac.molechelinmadness.model.event;

import edu.cornell.gdiac.molechelinmadness.model.GameObject;
import edu.cornell.gdiac.molechelinmadness.model.RotatingPlatformV2;

public class DoorV2Open implements Event{

    RotatingPlatformV2 door;
    float angle;

    @Override
    public void activate() {
        door.storeAngle(angle);
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public void linkObject(GameObject obj) {
        door = (RotatingPlatformV2) obj;
    }
}
