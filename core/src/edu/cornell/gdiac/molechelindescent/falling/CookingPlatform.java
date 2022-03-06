package edu.cornell.gdiac.molechelindescent.falling;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.molechelindescent.obstacle.BoxObstacle;
import edu.cornell.gdiac.molechelindescent.obstacle.WheelObstacle;

public class CookingPlatform extends WheelObstacle {

    /** The texture for this map ingredient */
    private TextureRegion texture;

    /** Location of this map ingredient */
    private Vector2 location;
    /** Array of moley inventory*/
    private Array<String> inventory;

    public CookingPlatform(float x, float y, float radius, TextureRegion texture, Vector2 scale, Array<String> inventory) {
        super(x, y, radius);
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(0f);
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

        return inventory;
        }

    }

