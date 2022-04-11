package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.AIController;
import edu.cornell.gdiac.molechelinmadness.GameCanvas;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.CapsuleObstacle;
import edu.cornell.gdiac.util.FilmStrip;

import java.lang.reflect.Field;

public class Mole extends CapsuleObstacle {

    public enum IdleAction {

        LEFT,
        RIGHT,
        JUMP,
        INTERACT,
        IDLE

    }

    public static class IdleUnit {
        public IdleAction idle;
        public float time;
    }


    /** Possibly temporary storage for ai controller*/
    AIController ai;
    /** Cache for internal force calculations */
    private final Vector2 forceCache = new Vector2();
    /** Name of sensor object*/
    String sensorName;
    /** The sensor shape for the feet*/
    PolygonShape sensorShapeF;
    /** The sensor shape for the hands*/
    PolygonShape sensorShapeH;
    /** Sensor fixure Left */
    Fixture left;
    Fixture right;
    FixtureDef sensorDefHand;
    /** Texture to indicate which mole is in control */
    TextureRegion controlTexture;
    /** The amount to slow the character down */
    private float damping;
    /** The maximum character speed */
    private float maxspeed;

    /** Cooldown (in animation frames) for jumping */
    private int jumpLimit;

    /** The impulse for the character jump */
    private float jumppulse;

    /** How long until we can jump again */
    private final int jumpCooldown;

    /** The factor to multiply by the input */
    private float force;

    /** float representing left/right movement */
   private float movement;

   /** which direction the character is facing */
   private boolean faceRight;

    /** Whether it's in the state of interacting or not*/
    private boolean interacting;

    /** Currently being controlled */
    private boolean controlled;

    /** Moles inventory */
    private Ingredient inventory;

    /** Mole can jump status */
    private boolean canJump;

    /** Mole trying to jump */
    private boolean jumping;

    /** Mole trying to drop */
    private boolean dropping;

    /** Sensors for this specific mole */
    private final ObjectSet<Fixture> sensorFixtures;

    /** Reference to mole's sprite for drawing */
    private FilmStrip moleStrip;
    /** Reference to mole's sprite for drawing */
    private int currFrame;
    /** Reference to mole's sprite for drawing */
    private float tSince;
    /** filmstrip width/height */
    private int fSize;
    /** filmstrip width/height */
    private Double frameRate;




    /** Get interacting */
    public boolean isInteracting() {return interacting;}

    /** Set interacting */
    public void setInteracting(boolean bool) {interacting = bool;}

    /**
     *
     * Return whether this mole can jump or not.
     *
     */
    public boolean canJump() {
        return this.canJump;
    }

    /**
     *
     * Set the jump status of this mole.
     *
     * @param canJump Boolean value of whether or not  mole can jump
     */
    public void setCanJump(boolean canJump) {
        this.canJump = canJump;

    }

    /**
     * Returns true if the dude is actively jumping.
     *
     * @return true if the dude is actively jumping.
     */
    public boolean isJumping() {
        return jumping && jumpCooldown <= 0 && canJump;
    }

    /**
     *
     * Set whether this mole is trying to jump
     *
     * */
    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    /**
     *
     * Add to specific mole's sensor fixture array.
     * @param toAdd Fixture to be added
     *
     */
    public void addSensorFixtures(Fixture toAdd) {
        sensorFixtures.add(toAdd);
    }

    /**
     *
     * Remove specific mole's sensor fixture array.
     * @param toRemove Fixture to be removed
     *
     */
    public void removeSensorFixtures(Fixture toRemove) {
        sensorFixtures.remove(toRemove);
    }

    /**
     *
     * Return the number of sensor fixtures for this current mole.
     *
     */
    public int countFixtures() {
        return sensorFixtures.size;
    }
    /**
     *
     * Return the inventory of this mole.
     *
     */
    public Ingredient getInventory() {
        return this.inventory;
    }

    /**
     * Creates the physics Body(s) for moles, adding them to the world.
     *
     * @param world Box2D world to store body
     *
     * @return true if object allocation succeeded
     */
    public boolean activatePhysics(World world) {
        // create the box from our superclass
        if (!super.activatePhysics(world)) {
            return false;
        }

        // Ground Sensor
        // -------------
        // We only allow the Mole to jump when he's on the ground so that double jumping is not allowed.
        //
        // To determine whether the Mole is on the ground,
        // we create a thin sensor under his feet, which reports
        // collisions with the world but has no collision response.
        Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.density = 1.0f;
        sensorDef.isSensor = true;
        sensorShapeF = new PolygonShape();
        sensorShapeF.setAsBox(0.45f*getWidth(), 0.05f, sensorCenter, 0.0f);
        sensorDef.shape = sensorShapeF;

        // Ground sensor to represent our feet
        Fixture sensorFixture = body.createFixture( sensorDef );
        sensorFixture.setUserData("feet");

        // Sensor for interactions with Interactables
        sensorCenter = new Vector2(-getWidth() / 2, 0); // change to something to do with height of mole
        sensorDefHand = new FixtureDef();
        sensorDefHand.density = 1.0f;
        sensorDefHand.isSensor = true;
        sensorShapeH = new PolygonShape();
        sensorShapeH.setAsBox(0.05f, 0.6f*getWidth(), sensorCenter, 0f); // angle should be 90 degrees
        sensorDefHand.shape = sensorShapeH;

        // Side sensor to represent our front facing hand
        left = body.createFixture( sensorDefHand );

        sensorShapeH.setAsBox(0.05f, 0.6f*getWidth(), sensorCenter.scl(-1), 0f); // angle should be 90 degrees
        sensorDefHand.shape = sensorShapeH;
        right = body.createFixture(sensorDefHand);

        left.setUserData("hands");
        right.setUserData("hands");

        return true;
    }

    /**
     *
     * Get if mole is currently being controlled
     *
     * */
    public boolean isControlled() {return controlled;}

    /**
     *
     * Set mole control status
     *
     * */
    public void setControlled(boolean control) {
        controlled = control;
    }

    /**
     * Returns how much force to apply to get the dude moving
     *
     * Multiply this by the input to get the movement value.
     *
     * @return how much force to apply to get the dude moving
     */
    public float getForce() {
        return force;
    }

    /** Possibly temporary method getting AI Controller from mole */
    public AIController getAIController() {
        return ai;
    }

    /** Possibly temporary method attaching AI Controller to mole */
    public void setAIController(AIController ai) {
        this.ai = ai;
    }

    /**
     * Applies the force to the body of this dude
     *
     * This method should be called after the force attribute is set.
     */
    public void applyForce() {
        if (!isActive()) {
            return;
        }

        // Don't want to be moving. Damp out player motion
        if (getMovement() == 0f) {
            forceCache.set(-getDamping()*getVX(),0);
            body.applyForce(forceCache,getPosition(),true);
        }

        // Velocity too high, clamp it
        if (Math.abs(getVX()) >= getMaxSpeed()) {
            setVX(Math.signum(getVX())*getMaxSpeed());
        }

        forceCache.set(getMovement(),0);
        body.applyForce(forceCache,getPosition(),true);

        // Jump!
        if (isJumping()) {
            forceCache.set(0, jumppulse);
            body.applyLinearImpulse(forceCache,getPosition(),true);
        }
    }

    /**
     * Returns how hard the brakes are applied to get a dude to stop moving
     *
     * @return how hard the brakes are applied to get a dude to stop moving
     */
    public float getDamping() {
        return damping;
    }

    /**
     * Returns the upward impulse for a jump.
     *
     * @return the upward impulse for a jump.
     */
    public float getJumpPulse() {
        return jumppulse;
    }

    /**
     * Returns left/right movement of this character.
     *
     * This is the result of input times dude force.
     *
     * @return left/right movement of this character.
     */
    public float getMovement() {
        return movement;
    }

    /**
     * Sets left/right movement of this character.
     *
     * This is the result of input times dude force.
     *
     * @param value left/right movement of this character.
     */
    public void setMovement(float value) {
        movement = value;
        // Change facing if appropriate
        if (movement < 0) {
            faceRight = false;
        } else if (movement > 0) {
            faceRight = true;
        }
    }

    /**
     *
     * Set inventory will set the moles inventory to
     * Ingredient toAdd if the mole doesn't have
     * something in inventory already and return true.
     * Otherwise, it will return false and not add the ingredient to inventory.
     *
     * @param toAdd       Ingredient to be added to mole inventory.
     *
     */
    public boolean addToInventory(Ingredient toAdd) {

        if (this.inventory == null) {
            this.inventory = toAdd;
            return true;
        } else {
            return false;
        }

    }

    /**
     * Return the ingredient that is currently in inventory
     * and remove it from inventory.
     */
    public Ingredient drop() {
        Ingredient temp = this.inventory;
        this.inventory = null;
        return temp;
    }

    /**
     * Set whether mole is trying to drop an item or not
     *
     * @param bool whether dropping or not
     */
    public void setDropping(boolean bool) {
        dropping = bool;
    }

    /**
     * Possibly drop what the mole is holding to the world
     * depending on whether we are trying to drop
     */
    public void applyDrop() {
        if (dropping) {
            Ingredient ingr = drop();
            float offset = faceRight ? 1.25f : -1.25f;
            if (ingr != null) {
                ingr.setPosition(getX(), getY() + 0.25f); //for now the vertical offset is just a hard-coded 0.25f value
                ingr.getBody().applyLinearImpulse(3.0f * offset + body.getLinearVelocity().x, 0, 0, 0, true); //same for the impulse, essentially hard-coded rn
                ingr.setInWorld(true);
            }
        }
    }

    /**
     * @return Whether inventory is empty or not
     */
    public boolean isEmpty() {
        return (inventory == null);
    }

    /**
     * Updates whether hand sensor should be facing left or right of mole
     */
    public void updateHand() {
        Vector2 sensorCenter = new Vector2(getWidth() / 2, 0);
        if (this.faceRight) {
            sensorShapeH.setAsBox(0.05f, 0.6f*getWidth(), sensorCenter, 0f);
        }
        else {
            sensorShapeH.setAsBox(0.05f, 0.6f*getWidth(), sensorCenter.scl(-1), 0f);
        }
    }

    /**
     * Creates a degenerate Mole with default settings
     */
    public Mole() {
        super(0, 0, 1, 1);
        setFixedRotation(true);
        inventory = null;
        canJump = false;
        jumping = false;
        faceRight = true;
        jumpCooldown = 0;
        sensorFixtures = new ObjectSet<>();
        moleStrip = null;
        currFrame = 0;
        tSince = 0;
        fSize = 300;
        frameRate = 0.12;
    }

    /**
     * Parse the given string array from the JSON file into meaningful IdleUnit objects
     *
     * @param idle Example: [left, 0.5, idle, 0.05, right, 0.5, idle, 0.05]
     * @return Example: [(left, 0.5), (idle, 0.05), (right, 0.5), (idle, 0.05)]
     */
    private IdleUnit[] parseIdleBehavior (String[] idle) {
        assert idle.length % 2 == 0;

        IdleUnit[] idleActions = new IdleUnit[idle.length/2];
        IdleUnit temp = new IdleUnit();
        int first;
        int second;

        for (int i = 0; i < idle.length/2; i++) {

            first = i * 2; // Position of IdleAction in pair
            second = first + 1; // Position of time in pair

            // String to action logic, assuming actions are in all lower case
            if (idle[first].equals("left")){
                temp.idle = IdleAction.LEFT;

            } else if (idle[first].equals("right")){
                temp.idle = IdleAction.RIGHT;

            } else if (idle[first].equals("jump")){
                temp.idle = IdleAction.JUMP;

            } else if (idle[first].equals("interact")){
                temp.idle = IdleAction.INTERACT;

            } else {
                temp.idle = IdleAction.IDLE;

            }

            temp.time = Float.parseFloat(idle[second]);
            idleActions[i] = temp;
            temp = new IdleUnit();

        }

        return idleActions;

    }

    /**
     * Sets the upward impulse for a jump.
     *
     * @param value	the upward impulse for a jump.
     */
    public void setJumpPulse(float value) {
        jumppulse = value;
    }

    /**
     * Sets how much force to apply to get the dude moving
     *
     * Multiply this by the input to get the movement value.
     *
     * @param value	how much force to apply to get the dude moving
     */
    public void setForce(float value) {
        force = value;
    }

    /**
     * Sets how hard the brakes are applied to get a dude to stop moving
     *
     * @param value	how hard the brakes are applied to get a dude to stop moving
     */
    public void setDamping(float value) {
        damping = value;
    }

    /**
     * Returns the upper limit on dude left-right movement.
     *
     * This does NOT apply to vertical movement.
     *
     * @return the upper limit on dude left-right movement.
     */
    public float getMaxSpeed() {
        return maxspeed;
    }
    /**
     * Returns the current frame
     * @return the current frame.
     */
    public int getCurrFrame() {
        return currFrame;
    }
    /**
     * Sets the upper limit on dude left-right movement.
     *
     * This does NOT apply to vertical movement.
     *
     * @param value	the upper limit on dude left-right movement.
     */
    public void setMaxSpeed(float value) {
        maxspeed = value;
    }

    /**
     * Returns the cooldown limit between jumps
     *
     * @return the cooldown limit between jumps
     */
    public int getJumpLimit() {
        return jumpLimit;
    }

    /**
     * Sets the cooldown limit between jumps
     *
     * @param value	the cooldown limit between jumps
     */
    public void setJumpLimit(int value) {
        jumpLimit = value;
    }

    public void initialize(AssetDirectory directory, JsonValue json) {
        setName(json.name());
        float[] pos  = json.get("pos").asFloatArray();
        float[] size = json.get("size").asFloatArray();
        setPosition(pos[0],pos[1]);
        setDimension(size[0],size[1]);

        // Technically, we should do error checking here.
        // A JSON field might accidentally be missing
        setBodyType(json.get("bodytype").asString().equals("static") ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody);
        setDensity(json.get("density").asFloat());
        setFriction(json.get("friction").asFloat());
        setRestitution(json.get("restitution").asFloat());
        setForce(json.get("force").asFloat());
        setDamping(json.get("damping").asFloat());
        setMaxSpeed(json.get("maxspeed").asFloat());
        setJumpPulse(json.get("jumppulse").asFloat());
        setJumpLimit(json.get("jumplimit").asInt());

        //Idle behavior
        String[] idle = json.get("idle behavior").asStringArray();
        IdleUnit[] behavior = parseIdleBehavior(idle);
        /** The idle behavior of this mole */


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
        setDebugColor(debugColor);

        // Now get the texture from the AssetManager singleton
        //  Setting filmstrip
        directory.loadAssets();
        String key = json.get("filmstrip").asString();

        Texture moleTexture = directory.getEntry(key, Texture.class);
        moleStrip = new FilmStrip(moleTexture, 4, 4, 16, 0, 0, fSize, fSize);

        moleStrip.setFrame(currFrame);
        setTexture(moleStrip);

        String key2 = json.get("control").asString();
        controlTexture = new TextureRegion(directory.getEntry(key2, Texture.class));

        // Regular texture
        /*String key3 = json.get("texture").asString();
        TextureRegion texture = new TextureRegion(directory.getEntry(key3, Texture.class));
        setTexture(texture);*/

        // Get the sensor information
        /*Vector2 sensorCenter = new Vector2(0, -getHeight()/2);
        float[] sSize = json.get("sensorsize").asFloatArray();
        sensorShape = new PolygonShape();
        sensorShape.setAsBox(sSize[0], sSize[1], sensorCenter, 0.0f);*/

        // Reflection is best way to convert name to color
        Color sensorColor;
        try {
            String cname = json.get("sensorcolor").asString().toUpperCase();
            Field field = Class.forName("com.badlogic.gdx.graphics.Color").getField(cname);
            sensorColor = new Color((Color)field.get(null));
        } catch (Exception e) {
            sensorColor = null; // Not defined
        }
        opacity = json.get("sensoropacity").asInt();
        assert sensorColor != null;
        sensorColor.mul(opacity/255.0f);
        sensorName = json.get("sensorname").asString();

        this.ai = new AIController(behavior);

    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        if (texture != null) {
            float effect = faceRight ? 1.0f : -1.0f;
            canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),effect,1.0f);
            if (this.controlled) {
                canvas.draw(controlTexture, Color.WHITE, origin.x / 2, origin.y-texture.getRegionHeight()*1.2f, getX()*drawScale.x, getY()*drawScale.y, getAngle(), 1f, 1f);
            }
            if (inventory != null) {
                TextureRegion texture = inventory.getTexture();
                canvas.draw(texture, Color.WHITE, origin.x /2, origin.y, getX()*drawScale.x, getY()*drawScale.y, getAngle(), 1f, 1f);
            }
        }
    }

    /**
     * Draws the outline of the physics body.
     *
     * This method can be helpful for understanding issues with collisions.
     *
     * @param canvas Drawing context
     */
    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        canvas.drawPhysics(sensorShapeF,Color.RED,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
        canvas.drawPhysics(sensorShapeH,Color.RED,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
    }

    /**
     * returns frame to corresponding walk cycle
     */
    public void setFrame(float dt){
        tSince += dt;
        if (movement == 0) {
            currFrame = 0;
            moleStrip.setFrame(currFrame);
        } else if (this.isJumping()){
            // Jumping animation needs to be added
            currFrame = 0;
            moleStrip.setFrame(currFrame);
            tSince -= frameRate;
        } else if (currFrame < 8 && tSince > frameRate) {
            currFrame++;
            moleStrip.setFrame(currFrame);
            tSince -= frameRate;
        } else if (currFrame == 8){
            currFrame = 0;
            moleStrip.setFrame(currFrame);
            tSince -= frameRate;
        }
            // Alternative
            /*if (this.jumping || this.movement == 0) {
                currFrame = 0;
                moleStrip.setFrame(currFrame);
                tSince = 0;
            } else if (currFrame + 1 < 9 && tSince > 0.25) {
                currFrame++;
                moleStrip.setFrame(currFrame);
                tSince += -0.25;
            } else {
                currFrame = 0;
                moleStrip.setFrame(currFrame);
                tSince = 0;
            }*/
    }
}
