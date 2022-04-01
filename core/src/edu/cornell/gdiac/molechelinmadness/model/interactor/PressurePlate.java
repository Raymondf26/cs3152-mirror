package edu.cornell.gdiac.molechelinmadness.model.interactor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.GameObject;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public class PressurePlate extends Interactor {

    BoxObstacle body;

    /**
     * Degenerate pressure plate.
     * Sets body type to 1, which means only detects feet collisions.
     */
    public PressurePlate(){
        super();
        body = new BoxObstacle(0, 0, 1, 1);
        body.setType(1);
    }

    /**
     * This method is called every frame in the main update loop of the game.
     *
     * In the case of an Interactor, all this has to handle is when to call triggerOn() and when to call triggerOff().
     *
     * @param dt the time passed in seconds since the previous frame
     */
    @Override
    public void refresh(float dt) {
        if (body.isContacting()) {
            triggerOn();
        }
        else {
            triggerOff();
        }
    }

    /**
     * Sets the drawscale of the attached Box2D Body
     *
     * @param scale the drawscale
     */
    @Override
    public void setDrawScale(Vector2 scale) {
        body.setDrawScale(scale);
    }

    /**
     * @return the Box2D Body attached to this Interactor object.
     */
    @Override
    public Obstacle getBody() {
        return body;
    }

    /**
     * Initializes this pressure plate via the given JSON value
     *
     * The JSON value has been parsed and is part of a bigger level file.  However,
     * this JSON value is limited to the exit subtree
     *
     * @param directory the asset manager
     * @param json      the JSON subtree defining the exit
     */
    @Override
    public void initialize(AssetDirectory directory, JsonValue json) {

        //Parameters adjustable per level
        body.setName(json.name());
        float[] pos  = json.get("pos").asFloatArray();
        body.setPosition(pos[0],pos[1]);
        float[] size  = json.get("size").asFloatArray();
        body.setDimension(size[0],size[1]);

        //Texture
        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        body.setTexture(texture);

        //Global parameters adjusted via config
        body.setBodyType(BodyDef.BodyType.StaticBody);
        body.setDensity(1.0f);
        body.setFriction(1.0f);
        body.setRestitution(0.0f);

    }
}
