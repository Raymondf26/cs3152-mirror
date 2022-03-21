package edu.cornell.gdiac.molechelinmadness.model.interactor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.event.Event;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;

public class Button extends BoxObstacle implements Interactor {


    Array<Event> triggers;
    Array<Event> detriggers;

    /**
     * Degenerate button
     */
    public Button() {
        super(0, 0, 1, 1);
    }

    @Override
    public InteractorType getType() {
        return InteractorType.BUTTON;
    }

    @Override
    public Array<Event> getTriggerLinks() {
        return triggers;
    }

    @Override
    public Array<Event> getDetriggerLinks() {
        return detriggers;
    }

    @Override
    public void addTriggerEvent(Event toAdd) {
        triggers.add(toAdd);
    }

    @Override
    public void addDetriggerEvent(Event toAdd) {
        detriggers.add(toAdd);
    }

    public void initialize(AssetDirectory directory, JsonValue json) {

        setName(json.name());
        float[] pos = json.get("pos").asFloatArray();
        setPosition(pos[0], pos[1]);
        float[] size = json.get("size").asFloatArray();
        setDimension(size[0], size[1]);

        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        setTexture(texture);
    }
}
