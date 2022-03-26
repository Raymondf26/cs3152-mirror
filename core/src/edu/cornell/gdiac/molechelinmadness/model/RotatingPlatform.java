package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.GameCanvas;
import edu.cornell.gdiac.molechelinmadness.model.event.Door;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.ComplexObstacle;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.WheelObstacle;

import java.lang.reflect.Field;

public class RotatingPlatform extends ComplexObstacle implements GameObject {

    private final BoxObstacle platform;
    private final WheelObstacle pin;
    private float angle;
    Vector2 anchor;

    public RotatingPlatform() {
        platform = new BoxObstacle(0, 0, 1, 1);
        pin = new WheelObstacle(0);
        bodies.add(platform);
        bodies.add(pin);
        anchor = new Vector2();
        angle = 0;
        pin.setSensor(true);
    }

    public void refresh(float dt) {
        if (angle != -360) {
           platform.setAngle(angle);
           angle = -360;
        }
    }

    /**
     * Creates the joints for this object.
     * <p>
     * This method is executed as part of activePhysics. This is the primary method to
     * override for custom physics objects.
     *
     * @param world Box2D world to store joints
     * @return true if object allocation succeeded
     */
    @Override
    protected boolean createJoints(World world) {
        assert bodies.size > 0;

        //pin.activatePhysics(world);

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = pin.getBody();
        jointDef.bodyB = platform.getBody();
        jointDef.collideConnected = false;
        jointDef.localAnchorA.set(anchor);
        jointDef.localAnchorB.set(anchor.scl(-1));
        jointDef.upperAngle = 1.57f;
        jointDef.lowerAngle = -1.57f;
        Joint joint = world.createJoint(jointDef);
        joints.add(joint);

        return true;
    }

    public void setTexture(TextureRegion texture) {
        platform.setTexture(texture);
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
        float[] pos1 = json.get("pos").asFloatArray();
        //float[] pos2 = json.get("pin_pos").asFloatArray();
        float radius = json.get("pin_radius").asFloat();
        float[] size = json.get("size").asFloatArray();
        platform.setPosition(pos1[0], pos1[1]);
        platform.setDimension(size[0], size[1]);
        pin.setPosition(pos1[0], pos1[1]);
        pin.setRadius(radius);
        float[] offset = json.get("pin_offset").asFloatArray();
        anchor.set(offset[0], offset[1]);

        pin.setBodyType(BodyDef.BodyType.StaticBody);
        platform.setBodyType(BodyDef.BodyType.DynamicBody);

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
        assert debugColor != null;
        debugColor.mul(opacity/255.0f);
        platform.setDebugColor(debugColor);
        pin.setDebugColor(debugColor);

        // Now get the texture from the AssetManager singleton
        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        platform.setTexture(texture);

    }

    /**
     * Draws the outline of the physics body.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {

        //temporary code
        canvas.draw(platform.getTexture(), Color.WHITE, platform.getTexture().getRegionWidth()/2.0f, platform.getTexture().getRegionHeight()/2.0f, platform.getX()*drawScale.x,platform.getY()*drawScale.y, platform.getAngle(), platform.getDimension().x, platform.getDimension().y);
    }


    /**
     * Handles the telegram just received.
     *
     * @param msg The telegram
     * @return {@code true} if the telegram has been successfully handled; {@code false} otherwise.
     */
    @Override
    public boolean handleMessage(Telegram msg) {
        int message = msg.message;
        try {
            Door event = (Door) msg.extraInfo;
            angle = event.getDegree();
            return true;
        }
        catch(Exception e) {
            System.err.println("Unable to cast to door related event");
            return false;
        }

    }
}
