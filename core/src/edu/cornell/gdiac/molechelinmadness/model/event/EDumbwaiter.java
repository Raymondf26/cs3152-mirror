package edu.cornell.gdiac.molechelinmadness.model.event;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;

public class EDumbwaiter implements Event{

    boolean up;

    /**
     * Create the default dumbwaiter event that sends items up the dumbwaiter.
     */
    public EDumbwaiter() {
        up = true;
    }

    /**
     *
     * @return Whether this event sends items up the dumbwaiter or down the dumbwaiter
     */
    public boolean getDir() {
        return up;
    }

    /**
     *
     * Initialize this event via the subtree in the json.
     *
     * @param json the subtree pertaining to this event
     */
    @Override
    public void initialize(JsonValue json) {
        up = json.get("effect").asBoolean();
    }
}
