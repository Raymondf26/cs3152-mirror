package edu.cornell.gdiac.molechelindescent.falling;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.molechelindescent.obstacle.BoxObstacle;
import edu.cornell.gdiac.molechelindescent.obstacle.WheelObstacle;

public class CookingPlatform extends BoxObstacle {

    /** The texture for this map ingredient */
    private TextureRegion texture;

    /** Location of this map ingredient */
    private Vector2 location;
    /** Array of moley inventory*/
    private Array<String> inventory;

    public CookingPlatform(float x, float y, float width, float height, TextureRegion texture, Vector2 scale, Array<String> inventory) {
        super(x, y, width, height );
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(1f);
        setFriction(0f);
        setRestitution(0f);
        setSensor(true);
        setDrawScale(scale);
        setTexture(texture);
        this.texture = texture;
        location = new Vector2 (x, y);
        this.inventory = inventory;
        }

    public void setInventory(Array<String> inventory){
        this.inventory = inventory;
    }

    public Array<String> resolveInventory(){
        if (inventory.contains("blue", true) && inventory.contains("yellow", true)){
            inventory.removeValue("blue", true);
            inventory.removeValue("yellow", true);
            inventory.add("green");
        }
        if (inventory.contains("blue", true) && inventory.contains("red", true)){
            inventory.removeValue("blue", true);
            inventory.removeValue("red", true);
            inventory.add("purple");
        }
        return inventory;
    }
}

