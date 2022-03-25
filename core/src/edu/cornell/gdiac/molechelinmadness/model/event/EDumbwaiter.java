package edu.cornell.gdiac.molechelinmadness.model.event;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;

public class EDumbwaiter implements Event{

    boolean up;

    public EDumbwaiter() {
        up = true;
    }

    @Override
    public void initialize(AssetDirectory directory, JsonValue json) {
        up = json.get("effect").asBoolean();
    }
}
