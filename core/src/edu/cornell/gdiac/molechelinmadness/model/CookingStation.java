package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;

public class CookingStation extends BoxObstacle {

    /** Whether it's in the state of being interacted with */
    private boolean interacting;


    /** How far along cooking has gone */
    private float progress;

    /**What type of station is this */
    private enum stationType{
        CHOPPING,
        COOKING
    }

    private stationType type;
    /**
     * Creates a new box at the origin.
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     */
    public CookingStation() {
        super(0, 0, 0.45f, 0.65f);
        this.type = null;
    }
    public void initialize(AssetDirectory directory, JsonValue json) {
        setName(json.name());
        String type = json.get("type").asString();
        float[] pos = json.get("pos").asFloatArray();
        this.setPosition(pos[0],pos[1]);

        if(type == "chopping"){
            this.type = CookingStation.stationType.CHOPPING;
        }
        else{
            this.type = CookingStation.stationType.COOKING;
        }
        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        setTexture(texture);

    }
}
