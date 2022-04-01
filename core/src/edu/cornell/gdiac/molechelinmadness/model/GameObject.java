package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;

/**
 * All game objects should implement this interface, except Platform and Wall.
 */
public interface GameObject extends Telegraph {

    /**
     * This method is called every frame in the main update loop of the game.
     *
     * @param dt the time passed in seconds since the previous frame
     */
    public void refresh(float dt);

    /**
     * Initializes this game object via the given JSON value
     *
     * The JSON value has been parsed and is part of a bigger level file.  However,
     * this JSON value is limited to the exit subtree
     *
     * @param directory the asset manager
     * @param json		the JSON subtree defining the exit
     */
    public void initialize(AssetDirectory directory, JsonValue json);

}
