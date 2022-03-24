package edu.cornell.gdiac.molechelinmadness.model;

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

public class CookingStation extends BoxObstacle implements Interactive, GameObject{

    /** Whether it's in the state of being interacted with */
    private boolean interacting;

    /** How far along cooking has gone */
    private float progress;

    /** Required time to cook */
    private float timeReq;

    /** Reference to interacting mole */
    private Mole mole;

    /** Ingredients currently at the cooking station*/
    private Array<Ingredient> ingredients;

    protected BitmapFont displayFont;

    /**What type of station is this */
    public enum stationType{
        CHOPPING,
        COOKING
    }

    private stationType type;
    /**
     * Creates a new box at the origin.
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     */

    /** Get the ingredients in this stations*/
    public Array<Ingredient> getIngredients(){
        return this.ingredients;
    }

    /**Get the station type*/
    public stationType getStationType(){
        return this.type;
    }

    public int getType() {return 0;}

    @Override
    public void resolveBegin(Mole mole) {
        this.mole = mole;
    }

    @Override
    public void resolveEnd(Mole mole) {
        this.mole = null;
    }

    @Override
    public void refresh(float dt) {
        if (this.type == stationType.CHOPPING) {
            if (mole != null && mole.isInteracting() && !mole.isEmpty() && !mole.getInventory().getChopped()) {
                interacting = true;
                if (progress > timeReq) {
                    mole.getInventory().setChopped(true);
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
            if (mole != null && mole.isInteracting() && !mole.isEmpty() && mole.getInventory().getChopped()) {
                this.ingredients.add(mole.drop());
            }
        }
    }

    public void Cook (Mole m){
        if(this.type == stationType.CHOPPING){
            if (m.getInventory() != null) {
                m.getInventory().setChopped(true);
            }
        }
        if(this.type == stationType.COOKING){
            if (m.getInventory() != null) {
                if(m.getInventory().getChopped()){
                    this.ingredients.add(m.getInventory());
                    m.drop();
                }
            }
        }
    }

    public CookingStation() {
        super(0, 0, 0.45f, 0.65f);
        this.type = null;
        ingredients = new Array<>();
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
