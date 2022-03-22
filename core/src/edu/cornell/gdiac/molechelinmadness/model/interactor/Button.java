package edu.cornell.gdiac.molechelinmadness.model.interactor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.Mole;
import edu.cornell.gdiac.molechelinmadness.model.event.Event;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;

public class Button extends BoxObstacle implements Interactor {


    Array<Event> triggers;
    Array<Event> detriggers;
    Boolean contact;
    Boolean triggered;

    /**
     * Degenerate button
     */
    public Button() {
        super(0, 0, 1, 1);
        triggers = new Array<>();
        detriggers = new Array<>();
        contact = false;
        triggered = false;
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

        setBodyType(BodyDef.BodyType.StaticBody);
        setSensor(true);

        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        setTexture(texture);
    }

    public void setContact(boolean bool) {
        contact = bool;
    }

    public boolean getContact (){return contact;}

    Mole mole;

    public void setContactMole(Mole mole) {this.mole = mole;}

    public Mole getContactMole() {return mole;}

    public void update(){
        if (contact) {
            //System.out.println("contacted");
            if (mole != null) {
                if (mole.isInteracting()) {
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
        else {
            triggerOff();
        }
    }

    private void triggerOn() {
        if (!triggered) {
            triggered = true;
            activate();
        }
    }

    private void triggerOff() {
        if (triggered) {
            triggered = false;
            deactivate();
        }
    }

    public void activate() {
        for (Event temp : triggers) {
            temp.activate();
        }
    }

    public void deactivate() {
        for (Event temp : detriggers) {
            temp.activate();
        }
    }
}
