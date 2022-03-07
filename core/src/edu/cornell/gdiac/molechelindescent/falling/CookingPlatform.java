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
        setRestitution(-1000f);
        setSensor(false);
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

        boolean cooked = false;

        System.out.println(inventory.contains("yellow", false));
        System.out.println(inventory.contains("blue", false));
        if (inventory.contains("blue", false) && inventory.contains("yellow", false)){
            //This first if-statement doesn't check for !cooked condition b/c it's the first one, so it will always be false
            inventory.removeValue("blue", false);
            inventory.removeValue("yellow", false);
            inventory.add("green");
            cooked = true;
        }
        if (inventory.contains("blue", false) && inventory.contains("red", false) && !cooked){
            inventory.removeValue("blue", false);
            inventory.removeValue("red", false);
            inventory.add("purple");
            cooked = true;
        }
        if (inventory.contains("purple", false) && inventory.contains("green", false) && !cooked){
            inventory.removeValue("purple", false);
            inventory.removeValue("green", false);
            inventory.add("white");
            cooked = true;
        }
        return inventory;
    }
}

