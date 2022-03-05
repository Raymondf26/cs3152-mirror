package edu.cornell.gdiac.molechelindescent.falling;


// The model class representing the ingredients found on the map.

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.molechelindescent.obstacle.WheelObstacle;

public class MapIngredient extends WheelObstacle {
    /** The texture for this map ingredient */
    private TextureRegion texture;

    /** Location of this map ingredient */
    private Vector2 location;

    private Array<String> drops;

    private String name;

    public MapIngredient(float x, float y, float radius, TextureRegion texture, Vector2 scale, String name) {
        super(x, y, radius);
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(0f);
        setFriction(0f);
        setRestitution(0f);
        setSensor(true);
        setDrawScale(scale);
        setTexture(texture);
        setName(name);
        this.texture = texture;
        location = new Vector2 (x, y);
        this.name = name;
    }

    /** Get a list of what inventory ingredients this map ingredient drops*/
    public Array<String> getDrops() {return drops;}

    /** Set the list of what inventory ingredients this map ingredient drops*/
    public void setDrops(Array<String> drops) {this.drops = drops;}

    public String getName() {return name;}
}
