package edu.cornell.gdiac.molechelinmadness.model.obstacle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;

public class RotatingPlatform extends ComplexObstacle {

    BoxObstacle platform;
    WheelObstacle pin;

    public RotatingPlatform () {
        platform = new BoxObstacle(0, 0, 1, 1);
        pin = new WheelObstacle(1);
    }

    public void initialize(AssetDirectory directory, JsonValue json) {

        setName(json.name());
        float[] pos = json.get("pos").asFloatArray();
        platform.setPosition(pos[0], pos[1]);
        float[] size = json.get("size").asFloatArray();
        platform.setDimension(size[0], size[1]);
        float[] posPin = json.get("pin_pos").asFloatArray();
        pin.setPosition(posPin[0], posPin[1]);
        float pinRadius = json.get("pin_radius").asFloat();
        pin.setRadius(pinRadius);

        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        platform.setTexture(texture);
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
        pin.activatePhysics(world);

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = pin.getBody();
        jointDef.bodyB = platform.getBody();
        jointDef.collideConnected = false;
        Joint joint = world.createJoint(jointDef);
        joints.add(joint);

        return true;
    }
}
