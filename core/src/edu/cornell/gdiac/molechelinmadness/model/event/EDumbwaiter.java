package edu.cornell.gdiac.molechelinmadness.model.event;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;

public class Dumbwaiter implements Event{

    boolean up;

    public Dumbwaiter () {
        up = true;
    }

    @Override
    public void initialize(AssetDirectory directory, JsonValue json) {
        up = json.get("effect").asBoolean();
    }
}
