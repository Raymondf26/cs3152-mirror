package edu.cornell.gdiac.molechelinmadness.model.event;

import com.badlogic.gdx.utils.JsonValue;

public class Door implements Event{

    float degree;

    public Door() {
        degree = 0f;
    }

    public float getDegree() {return degree;}

    @Override
    public void initialize(JsonValue json) {
        degree = json.get("angle").asFloat();
    }
}
