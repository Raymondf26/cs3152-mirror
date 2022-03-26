package edu.cornell.gdiac.molechelinmadness.model.event;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.GameObject;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public interface Event {

    public void initialize(JsonValue json);

}
