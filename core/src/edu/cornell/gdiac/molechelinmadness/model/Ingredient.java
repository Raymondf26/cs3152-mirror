package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;

public class Ingredient extends BoxObstacle implements GameObject{
    private static float SIZE = 5.0f;
    private float holdX;
    private float holdY;
    public Boolean moved = false;

    //True if this ingredient chopped
    public Boolean chopped;

    public enum IngType {
        TOMATO,
        ONION,
        EGGPLANT
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

    public Boolean getChopped(){
        return chopped;
    }

    public void setChopped(Boolean c){
        chopped = c;
    }

    public void holdPos(float x, float y){
        this.holdX = x;
        this.holdY = y;
        this.moved = true;
    }

    @Override
    public void refresh(float dt) {
        if (moved) {
            setPosition(holdX, holdY);
            this.moved = false;
        }
    }

    /**
     * Creates a new box at the origin.
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     *
     */
    public Ingredient() {
        super(0, 0, 0.45f, 0.65f);
        this.holdX = 0;
        this.holdY = 0;
        chopped = false;

    }

    public void initialize(AssetDirectory directory, JsonValue json) {
        setName(json.name());
        String type = json.get("type").asString();
        float[] pos = json.get("pos").asFloatArray();
        this.setPosition(pos[0],pos[1]);
        boolean isChopped = json.get("chopped").asBoolean();
        this.chopped = isChopped;

        if(type.equals("tomato")){
            this.type = IngType.TOMATO;
        }
        else if(type.equals("onion")){
            this.type = IngType.ONION;
        }

        else{
            this.type = IngType.EGGPLANT;
        }
        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        setTexture(texture);
    }
}
