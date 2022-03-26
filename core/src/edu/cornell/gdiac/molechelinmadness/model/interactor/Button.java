package edu.cornell.gdiac.molechelinmadness.model.interactor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public class Button extends Interactor{

    BoxObstacle body;

    /**
     * Degenerate button
     */
    public Button() {
        super();
        body = new BoxObstacle(0, 0, 1, 1);
    }

    @Override
    public void refresh(float dt){
        if (body.isContacting()) {
            if (body.getContactMole().isInteracting()) {
                triggerOn();
            }
            else {
                triggerOff();
            }
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

        //Level specific attributes
        body.setName(json.name());
        float[] pos = json.get("pos").asFloatArray();
        body.setPosition(pos[0], pos[1]);
        float[] size = json.get("size").asFloatArray();
        body.setDimension(size[0], size[1]);


        //Game wide attributes
        body.setBodyType(BodyDef.BodyType.StaticBody);
        body.setSensor(true);


        //Texture
        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        body.setTexture(texture);
    }
}
