package edu.cornell.gdiac.molechelinmadness.model.event;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.molechelinmadness.model.GameObject;
import edu.cornell.gdiac.molechelinmadness.model.RotatingPlatform;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public class DoorCloseMiddle implements Event{

    RotatingPlatform door;

    @Override
    public void activate() {
        door.rotateBack();
        door.translate(new Vector2(0.5f, 0.5f));
    }

    @Override
    public void linkObject(GameObject obs) {
        door = (RotatingPlatform) obs;
    }

}
