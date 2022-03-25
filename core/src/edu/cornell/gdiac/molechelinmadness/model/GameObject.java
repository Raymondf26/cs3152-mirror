package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.ai.msg.Telegraph;

public interface GameObject extends Telegraph {

    public void refresh(float dt);

}
