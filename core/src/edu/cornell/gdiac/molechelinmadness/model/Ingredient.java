package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;

public class Ingredient extends BoxObstacle {
    private static float SIZE = 5.0f;

    public enum IngType {
        TOMATO,
        SCALLION,
        EGG,
        NOODLES
    }
    //Type of ingredient
    public IngType type;


    //Gets the ingredients type
    public IngType getIngredientType(){
        return this.type;
    }


    //Sets the ingredients type
    public void setIngType(IngType ing){
        this.type = ing;
    }

    /**
     * Creates a new box at the origin.
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public Ingredient() {
        super(0, 0, 0.45f, 0.65f);


    }

    public void initialize(AssetDirectory directory, JsonValue json) {
        setName(json.name());
        String type = json.get("type").asString();
        float[] pos = json.get("pos").asFloatArray();
        this.setPosition(pos[0],pos[1]);

        if(type == "tomato"){
            this.type = IngType.TOMATO;
        }
        else if(type == "scallion"){
            this.type = IngType.SCALLION;
        }
        else if(type == "egg"){
            this.type = IngType.EGG;
        }
        else{
            this.type = IngType.NOODLES;
        }
        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        setTexture(texture);

    }
}
