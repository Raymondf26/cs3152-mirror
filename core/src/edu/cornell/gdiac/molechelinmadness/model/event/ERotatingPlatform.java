package edu.cornell.gdiac.molechelinmadness.model.event;

import com.badlogic.gdx.utils.JsonValue;

public class ERotatingPlatform implements Event{

    float degree;

    /**
     * Create an empty door event with degree set to 0 radians, aka it won't turn the door.
     */
    public ERotatingPlatform() {
        degree = 0f;
    }

    /**
     *
     * @return the degree this event will turn the door
     */
    public float getDegree() {return degree;}

    /**
     *
     * Initialize this event via the subtree in the json.
     *
     * @param json the subtree pertaining to this event
     */
    @Override
    public void initialize(JsonValue json) {
        degree = json.get("angle").asFloat();
    }
}
