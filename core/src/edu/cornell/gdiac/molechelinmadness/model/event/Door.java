package edu.cornell.gdiac.molechelinmadness.model.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.GameObject;
import edu.cornell.gdiac.molechelinmadness.model.RotatingPlatform;

public class Door implements Event{

    float degree;

    public Door() {
        degree = 0f;
    }


    @Override
    public void initialize(AssetDirectory directory, JsonValue json) {
        degree = json.get("angle").asFloat();
    }
}
