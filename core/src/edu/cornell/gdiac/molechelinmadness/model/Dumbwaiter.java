package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.GameCanvas;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

import java.lang.reflect.Field;

public class Dumbwaiter extends Obstacle{

    /** References to the head and tail */
    protected BoxObstacle head;
    protected BoxObstacle tail;

    /**
     * Create a new Dumbwaiter with degenerate settings
     * */
    public Dumbwaiter() {
        head = new BoxObstacle(0, 0, 1, 1);
        tail = new BoxObstacle(0, 0, 1, 1);
    }

    /**
     * Initializes the dumbwaiter via the given JSON value
     *
     * The JSON value has been parsed and is part of a bigger level file.  However,
     * this JSON value is limited to the exit subtree
     *
     * @param directory the asset manager
     * @param json		the JSON subtree defining the exit
     */
    public void initialize(AssetDirectory directory, JsonValue json) {
        setName(json.name());
        float[] pos1 = json.get("pos1").asFloatArray();
        float[] pos2 = json.get("pos2").asFloatArray();
        float[] size = json.get("size").asFloatArray();
        head.setPosition(pos1[0], pos1[1]);
        tail.setPosition(pos2[0], pos2[1]);
        head.setDimension(size[0], size[1]);
        tail.setDimension(size[0], size[1]);

        // Technically, we should do error checking here.
        // A JSON field might accidentally be missing
        head.setBodyType(json.get("bodytype").asString().equals("static") ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody);
        head.setDensity(json.get("density").asFloat());
        head.setFriction(json.get("friction").asFloat());
        head.setRestitution(json.get("restitution").asFloat());

        tail.setBodyType(json.get("bodytype").asString().equals("static") ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody);
        tail.setDensity(json.get("density").asFloat());
        tail.setFriction(json.get("friction").asFloat());
        tail.setRestitution(json.get("restitution").asFloat());

        // Reflection is best way to convert name to color
        Color debugColor;
        try {
            String cname = json.get("debugcolor").asString().toUpperCase();
            Field field = Class.forName("com.badlogic.gdx.graphics.Color").getField(cname);
            debugColor = new Color((Color)field.get(null));
        } catch (Exception e) {
            debugColor = null; // Not defined
        }
        int opacity = json.get("debugopacity").asInt();
        debugColor.mul(opacity/255.0f);
        head.setDebugColor(debugColor);
        tail.setDebugColor(debugColor);

        // Now get the texture from the AssetManager singleton
        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        head.setTexture(texture);
        tail.setTexture(texture);
    }


    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     * <p>
     * Implementations of this method should NOT retain a reference to World.
     * That is a tight coupling that we should avoid.
     *
     * @param world Box2D world to store body
     * @return true if object allocation succeeded
     */
    @Override
    public boolean activatePhysics(World world) {
        return head.activatePhysics(world) && tail.activatePhysics(world);
    }

    /**
     * Destroys the physics Body(s) of this object if applicable,
     * removing them from the world.
     *
     * @param world Box2D world that stores body
     */
    @Override
    public void deactivatePhysics(World world) {
        head.deactivatePhysics(world);
        tail.deactivatePhysics(world);
    }

    /**
     * Draws the texture physics object.
     *
     * @param canvas Drawing context
     */
    @Override
    public void draw(GameCanvas canvas) {
        head.draw(canvas);
        tail.draw(canvas);
    }

    /**
     * Draws the outline of the physics body.
     * <p>
     * This method can be helpful for understanding issues with collisions.
     *
     * @param canvas Drawing context
     */
    @Override
    public void drawDebug(GameCanvas canvas) {
        head.drawDebug(canvas);
        tail.drawDebug(canvas);
    }



}
