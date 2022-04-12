package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.GameCanvas;
import edu.cornell.gdiac.molechelinmadness.model.event.EDumbwaiter;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.ComplexObstacle;

import java.lang.reflect.Field;

public class Dumbwaiter extends ComplexObstacle implements GameObject{

    /**
     * This method is called every frame in the main update loop of the game.
     *
     * @param dt the time passed in seconds since the previous frame
     */
    @Override
    public void refresh(float dt) {
        head.refresh(dt);
        tail.refresh(dt);
    }

    /**
     * Handles the telegram just received.
     *
     * @param msg The telegram
     * @return {@code true} if the telegram has been successfully handled; {@code false} otherwise.
     */
    @Override
    public boolean handleMessage(Telegram msg) {
        try {
            EDumbwaiter event = (EDumbwaiter) msg.extraInfo;
            if (event.getDir()) {
                sendUp();
            }
            else {
                sendDown();
            }
            return true;
        }
        catch(Exception e) {
            System.err.println("Unable to cast to dumbwaiter related event");
            return false;
        }
    }

    /**
     * The object used to represent the head, and the tail, of the dumbwaiter.
     */
    public class DumbHead extends BoxObstacle {

        Ingredient ingr;
        float timeLeft;
        float cooldown;

        /**
         * Degenerate dumbwaiter head.
         * Sets body type to 0, which means only detects hand collisions.
         */
        public DumbHead() {
            super(0,0,1,1);
            cooldown = 0f;
            timeLeft = 0f;
            setType(0);
        }

        /**
         * This method is called every frame in the main update loop of the game.
         *
         * @param dt the time passed in seconds since the previous frame
         */
        public void refresh(float dt) {
            if (timeLeft < 0) {
                if (isContacting()) {
                    if (getContactMole().isInteracting()) {
                        if (!getContactMole().isEmpty()) {
                            if (ingr == null) {
                                ingr = getContactMole().drop();
                                timeLeft = cooldown;
                            }
                        }
                        else {
                            if (ingr != null) {
                                getContactMole().addToInventory(ingr);
                                ingr = null;
                                timeLeft = cooldown;
                            }
                        }
                    }
                }
            }
            else {
                timeLeft -= dt;
            }
        }

        @Override
        public void draw(GameCanvas canvas) {
            super.draw(canvas);
            if (ingr != null) {
                TextureRegion texture = ingr.getTexture();
                canvas.draw(texture, Color.WHITE, origin.x / 2, origin.y /2, getX()*drawScale.x, getY()*drawScale.y, getAngle(), 1f, 1f);
            }
        }

    }

    /**
     * Send ingredient from tail to head event
     */
    public void sendUp() {
        if (tail.ingr != null) {
            if (head.ingr == null) {
                head.ingr = tail.ingr;
                tail.ingr = null;
            }
            else {
                //Let the player know the dumbwaiter must be empty before sending up
                System.err.println("We should probably send an event to the gameplay controller here to play some different type of audio");
            }
        }
    }

    /**
     * Send ingredient from tail to head event
     */
    public void sendDown() {
        if (head.ingr != null) {
            if (tail.ingr == null) {
                tail.ingr = head.ingr;
                head.ingr = null;
            }
            else {
                //Let the player know the dumbwaiter must be empty before sending down
                System.err.println("We should probably send an event to the gameplay controller here to play some different type of audio");
            }
        }
    }

    /** References to the head and tail */
    protected DumbHead head;
    protected DumbHead tail;

    /**
     * Create a new Dumbwaiter with degenerate settings
     * */
    public Dumbwaiter() {
        head = new DumbHead();
        tail = new DumbHead();
        bodies.add(head);
        bodies.add(tail);
        head.setSensor(true);
        tail.setSensor(true);
    }

    /**
     * Initializes the dumbwaiter via the given JSON value
     *
     * The JSON value has been parsed and is part of a bigger level file.  However,
     * this JSON value is limited to the exit subtree
     *
     * @param directory the asset manager
     * @param json		the JSON subtree defining the exit
     */
    public void initialize(AssetDirectory directory, JsonValue json) {
        setName(json.name());
        float[] pos1 = json.get("pos1").asFloatArray();
        float[] pos2 = json.get("pos2").asFloatArray();
        float[] size = json.get("size").asFloatArray();
        head.setPosition(pos1[0], pos1[1]);
        tail.setPosition(pos2[0], pos2[1]);
        head.setDimension(size[0], size[1]);
        tail.setDimension(size[0], size[1]);

        head.cooldown = json.get("cooldown").asFloat();
        tail.cooldown = json.get("cooldown").asFloat();

        // Technically, we should do error checking here.
        // A JSON field might accidentally be missing
        head.setBodyType(json.get("bodytype").asString().equals("static") ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody);
        head.setDensity(json.get("density").asFloat());
        head.setFriction(json.get("friction").asFloat());
        head.setRestitution(json.get("restitution").asFloat());

        tail.setBodyType(json.get("bodytype").asString().equals("static") ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody);
        tail.setDensity(json.get("density").asFloat());
        tail.setFriction(json.get("friction").asFloat());
        tail.setRestitution(json.get("restitution").asFloat());

        // Reflection is best way to convert name to color
        Color debugColor;
        try {
            String cname = json.get("debugcolor").asString().toUpperCase();
            Field field = Class.forName("com.badlogic.gdx.graphics.Color").getField(cname);
            debugColor = new Color((Color)field.get(null));
        } catch (Exception e) {
            debugColor = null; // Not defined
        }
        int opacity = json.get("debugopacity").asInt();
        debugColor.mul(opacity/255.0f);
        head.setDebugColor(debugColor);
        tail.setDebugColor(debugColor);

        // Now get the texture from the AssetManager singleton
        String key = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key, Texture.class));
        head.setTexture(texture);
        tail.setTexture(texture);
    }

    /**
     * Creates the joints for this object.
     * <p>
     * This method is executed as part of activePhysics. This is the primary method to
     * override for custom physics objects.
     *
     * @param world Box2D world to store joints
     * @return true if object allocation succeeded
     */
    @Override
    protected boolean createJoints(World world) {
        return true;
    }
}
