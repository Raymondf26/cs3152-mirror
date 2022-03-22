package edu.cornell.gdiac.molechelinmadness.model.event;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.molechelinmadness.model.RotatingPlatform;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public class DoorOpenMiddle implements Event{

    RotatingPlatform door;

    public void activate() {
        door.rotate();
        door.translate(new Vector2(-0.5f, -0.5f));
    }

    public void linkObject(Obstacle obs) {
        door = (RotatingPlatform) obs;
    }

}
