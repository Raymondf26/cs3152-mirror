package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.GameCanvas;
import edu.cornell.gdiac.molechelinmadness.model.event.Event;
import edu.cornell.gdiac.molechelinmadness.model.interactor.Interactor;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;

public class PressurePlate extends BoxObstacle implements Interactor, Interactive, GameObject {

    Array<Event> TriggerEventList;
    Array<Event> DetriggerEventList;

    boolean contact;

    public PressurePlate(){
        super(0,0,1,1);
        TriggerEventList = new Array<Event>();
        DetriggerEventList = new Array<Event>();

        // Set values
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(1.0f);
        setFriction(1.0f);
        setRestitution(0.0f);

    }

    @Override
    public void refresh(float dt) {
        if (contact) {
            activate();
        }
        else {
            deactivate();
        }
    }

    /**
    *
    * This method activates all events that are linked to the pressure plate.
    *
    */
    public void activate(){
        for (Event temp : TriggerEventList) {
            temp.activate();
        }
    }

    /**
     *
     * This method deactivates all events that are linked to the pressure plate.
     *
     */
    public void deactivate() {
        for (Event temp : DetriggerEventList) {
            temp.activate();
        }
    }

    /**
     *
     * This method adds an event to the activate triggered events of this pressure plate.
     *
     */
    public void addTriggerEvent(Event toAdd) {
        TriggerEventList.add(toAdd);
    }

    /**
     *
     * This method adds an event to the deactivate triggered events of this pressure plate.
     *
     */
    public void addDetriggerEvent(Event toAdd) {
        DetriggerEventList.add(toAdd);
    }

    public int getType() {return 1;}

    @Override
    public void resolveBegin(Mole mole) {
        contact = true;
    }

    @Override
    public void resolveEnd(Mole mole) {
        contact = false;
    }

    /**
     *
     * Return the trigger event list
     *
     */
    public Array<Event> getTriggerLinks() {
        return TriggerEventList;
    }

    /**
     *
     * Return the detrigger event list
     *
     */
    public Array<Event> getDetriggerLinks() {
        return DetriggerEventList;
    }

    public void initialize(AssetDirectory directory, JsonValue json) {

        setName(json.name());
        float[] pos  = json.get("pos").asFloatArray();
        setPosition(pos[0],pos[1]);
        float[] size  = json.get("size").asFloatArray();
        setDimension(size[0],size[1]);

        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        setTexture(texture);

    }

    /**
     * Draws the outline of the physics body.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        if (texture != null) {
            canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),getDimension().x,getDimension().y);
        }
    }
}
