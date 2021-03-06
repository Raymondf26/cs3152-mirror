package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.GameCanvas;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;

public class CookingStation extends BoxObstacle implements GameObject{

    /** Whether it's in the state of being interacted with */
    private boolean interacting;

    /** How far along cooking has gone */
    private float progress;

    /** Required time to cook */
    private float timeReq;

    /** Ingredients currently at the cooking station*/
    private Array<Ingredient> ingredients;

    protected BitmapFont displayFont;

    /**
     * Handles the telegram just received.
     *
     * @param msg The telegram
     * @return {@code true} if the telegram has been successfully handled; {@code false} otherwise.
     */
    @Override
    public boolean handleMessage(Telegram msg) {
        System.err.println("No events currently affecting CookingStation");
        return false;
    }

    /** What type of station is this */
    public enum stationType{
        CHOPPING,
        COOKING
    }

    private stationType type;

    /** Get the ingredients in this stations*/
    public Array<Ingredient> getIngredients(){
        return this.ingredients;
    }

    /**Get the station type*/
    public stationType getStationType(){
        return this.type;
    }

    @Override
    public void refresh(float dt) {
        if (this.type == stationType.CHOPPING) {
            if (isContacting() && getContactMole().isInteracting() && !getContactMole().isEmpty() && !getContactMole().getInventory().getChopped()) {
                interacting = true;
                if (progress > timeReq) {
                    getContactMole().getInventory().setChopped(true);
                    System.out.println("veggie chopped");
                    progress = 0;
                }
                else {
                    progress += dt;
                }
            }
            else {
                interacting = false;
                progress = 0;
            }
        }
        else if (this.type == stationType.COOKING) {
            if (isContacting() && getContactMole().isContacting() && !getContactMole().isEmpty() && getContactMole().getInventory().getChopped()) {
                this.ingredients.add(getContactMole().drop());
            }
        }
    }

    /**
     * Creates degenerate cooking station.
     * Sets body type to 0, which means only detects hand collisions.
     */
    public CookingStation() {
        super(0, 0, 1f, 1f);
        this.type = null;
        ingredients = new Array<>();
        setType(0);
    }

    public void initialize(AssetDirectory directory, JsonValue json) {
        setName(json.name());
        String type = json.get("type").asString();
        float[] pos = json.get("pos").asFloatArray();
        this.setPosition(pos[0],pos[1]);
        float[] size = json.get("size").asFloatArray();
        this.setDimension(size[0], size[1]);

        setBodyType(BodyDef.BodyType.StaticBody);

        displayFont = directory.getEntry("shared:retro", BitmapFont.class);

        if(type.equals("chopping")){
            this.type = CookingStation.stationType.CHOPPING;
        }
        else{
            this.type = CookingStation.stationType.COOKING;
        }

        timeReq = json.get("cooking_time").asFloat();

        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        setTexture(texture);

    }

    public void draw(GameCanvas canvas) {
        super.draw(canvas);
        if (interacting) {
            displayFont.setColor(Color.YELLOW);
            canvas.drawText(String.valueOf((int)((timeReq - progress) * 100)), displayFont, getX()*drawScale.x, (getY() + getHeight()) *drawScale.y);
        }
    }
}
