package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.GameCanvas;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;

public class Ingredient extends BoxObstacle implements GameObject{
    private static float SIZE = 5.0f;

    //True if this ingredient chopped
    public Boolean chopped;

    private boolean inWorld;

    /**
     * Handles the telegram just received.
     *
     * @param msg The telegram
     * @return {@code true} if the telegram has been successfully handled; {@code false} otherwise.
     */
    @Override
    public boolean handleMessage(Telegram msg) {
        System.err.println("There should be no events affecting Ingredient");
        return false;
    }

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

    /**
     * This method is called every frame in the main update loop of the game.
     *
     * @param dt the time passed in seconds since the previous frame
     */
    @Override
    public void refresh(float dt) {
        if (isContacting()) {
            if (getContactMole().isEmpty()) {
                getContactMole().addToInventory(this);
                setActive(false);
                inWorld = false;
            }
        }
        else if (inWorld) {
            setActive(true);
        }
    }

    /**
     * Set whether the ingredient is in the world or not.
     *
     * @param bool whether the ingredient is in the world or in something's inventory
     */
    public void setInWorld(boolean bool) {
        inWorld = bool;
    }
    public Ingredient(int x, int y) {
        super(x, y, 0.45f, 0.65f);
        this.holdX = 0;
        this.holdY = 0;
        chopped = false;

    }

    /**
     * Creates a degenerate ingredient.
     * Sets body type to 2, which means detects both hand and feet collisions.
     */
    public Ingredient() {
        super(0, 0, 1f, 1f);
        chopped = false;
        inWorld = true;
        setType(2);
    }

    /**
     * Initializes this game object via the given JSON value
     *
     * The JSON value has been parsed and is part of a bigger level file.  However,
     * this JSON value is limited to the exit subtree
     *
     * @param directory the asset manager
     * @param json		the JSON subtree defining the exit
     */
    @Override
    public void initialize(AssetDirectory directory, JsonValue json) {
        setName(json.name());
        String type = json.get("type").asString();
        float[] pos = json.get("pos").asFloatArray();
        this.setPosition(pos[0],pos[1]);
        boolean isChopped = json.get("chopped").asBoolean();
        this.chopped = isChopped;

        if(type.equals("tomato")) {
            this.type = IngType.TOMATO;
        }
        else if(type.equals("onion")) {
            this.type = IngType.ONION;
        }
        else {
            this.type = IngType.EGGPLANT;
        }

        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        setTexture(texture);
    }

    /**
     * Ensures we only draw the ingredient if it isn't being held by a mole or some other object.
     * @param canvas Drawing context
     */
    @Override
    public void draw(GameCanvas canvas) {
        if (isActive()) {
            super.draw(canvas);
        }
    }
}
