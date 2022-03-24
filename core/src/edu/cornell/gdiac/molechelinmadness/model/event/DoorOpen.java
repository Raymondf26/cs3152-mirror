package edu.cornell.gdiac.molechelinmadness.model.event;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.molechelinmadness.model.GameObject;
import edu.cornell.gdiac.molechelinmadness.model.RotatingPlatform;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public class DoorOpen implements Event{

    RotatingPlatform door;
    Vector2 translation = new Vector2();

    @Override
    public void activate() {
        door.rotate();
        door.translate(translation);
    }

    public void setTranslation(Vector2 vector) {
        translation = vector;
    }

    @Override
    public void linkObject(GameObject obs) {
        door = (RotatingPlatform) obs;
    }
}
