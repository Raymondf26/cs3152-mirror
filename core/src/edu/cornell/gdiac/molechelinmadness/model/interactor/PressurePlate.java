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

public class PressurePlate extends Interactor implements GameObject {

    BoxObstacle body;

    public PressurePlate(){
        body = new BoxObstacle(0, 0, 1, 1);
    }

    @Override
    public void refresh(float dt) {
        if (contact) {
            triggerOn();
        }
        else {
            triggerOff();
        }
    }

    @Override
    public void setDrawScale(Vector2 scale) {
        body.setDrawScale(scale);
    }

    @Override
    public Obstacle getBody() {
        return body;
    }

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
