package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.Game;
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
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.ComplexObstacle;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.WheelObstacle;

public class RotatingPlatform extends BoxObstacle implements GameObject {

    float newAngle = 0;
    Vector2 newVector = new Vector2(0, 0);

    public RotatingPlatform () {
        super(0, 0, 1, 1);
        // Set values
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(1.0f);
        setFriction(1.0f);
        setRestitution(0.0f);
    }

    public void initialize(AssetDirectory directory, JsonValue json) {

        setName("test");
        float[] pos = json.get("pos").asFloatArray();
        setPosition(pos[0], pos[1]);
        float[] size = json.get("size").asFloatArray();
        setDimension(size[0], size[1]);

        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        setTexture(texture);
    }

    public void rotate() {
       newAngle = 1.57f;
    }

    public void translate(Vector2 vector) {
        newVector = vector;
    }

    public void rotateBack() {
        newAngle = 0;
    }

    @Override
    public void refresh(float dt) {
        setAngle(newAngle);
        setPosition(getPosition().add(newVector));
        translate(newVector.scl(0));
    }

    /**
     * Draws the outline of the physics body.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        if (texture != null) {
            canvas.draw(texture, Color.WHITE, origin.x, origin.y,getX()*drawScale.x,getY()*drawScale.y, getAngle(),getWidth() * 0.65f ,getHeight());
        }
    }
}
