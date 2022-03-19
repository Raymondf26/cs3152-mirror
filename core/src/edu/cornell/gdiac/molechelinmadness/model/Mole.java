package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.molechelinmadness.obstacle.CapsuleObstacle;
import edu.cornell.gdiac.molechelinmadness.obstacle.Obstacle;

public class Mole extends CapsuleObstacle {

    public enum IdleAction {

        LEFT,
        RIGHT,
        JUMP,
        INTERACT,
        IDLE

    }

    public class IdleUnit {
        IdleAction idle;
        float time;
    }

    /** Whether it's in the state of interacting or not*/
    private boolean interacting;

    /** The idle behavior of this mole */
    private IdleUnit[] idleBehavior;

    /** Currently being controlled */
    private boolean controlled;

    /** Moles inventory */
    private Ingredient inventory;

    /** Mole can jump status */
    private boolean jump;

    /** Sensors for this specific mole */
    private ObjectSet<Fixture> sensorFixtures;

    /**
     *
     * Return whether this mole can jump or not.
     *
     */
    public boolean canJump() {
        return this.jump;
    }

    /**
     *
     * Set the jump status of this mole.
     *
     * @param canJump Boolean value of whether or not  mole can jump
     */
    public void setJump(boolean canJump) {
        this.jump = canJump;

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

        PolygonShape sensorShape;

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
        sensorShape = new PolygonShape();
        sensorShape.setAsBox(0.6f*getWidth()*2f, 0.05f, sensorCenter, 0.0f);
        sensorDef.shape = sensorShape;

        // Ground sensor to represent our feet
        Fixture sensorFixture = body.createFixture( sensorDef );
        sensorFixture.setUserData("feet");

        // Sensor for interactions with Interactables
        sensorCenter = new Vector2(-getWidth() / 2, 0); // change to something to do with height of mole
        sensorDef = new FixtureDef();
        sensorDef.density = 1.0f;
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        sensorShape.setAsBox(0.05f, 0.6f*getWidth()*2f, sensorCenter, 1.57f); // angle should be 90 degrees
        sensorDef.shape = sensorShape;

        // Side sensor to represent our front facing hand
        sensorFixture = body.createFixture( sensorDef );
        sensorFixture.setUserData("hands");

        return true;
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
    public boolean setInventory(Ingredient toAdd) {

        if (this.inventory == null) {
            this.inventory = toAdd;
            return true;
        } else {
            return false;
        }

    }

    /**
     *
     * Return the ingredient that is currently in inventory
     * and remove it from inventory.
     *
     */
    public Ingredient drop() {
        Ingredient temp = this.inventory;
        this.inventory = null;
        return temp;
    }

    /**
     * Creates a new capsule object.
     * <p>
     * The orientation of the capsule will be a full capsule along the
     * major axis.  If width == height, it will default to a vertical
     * orientation.
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x       Initial x position of the box center
     * @param y       Initial y position of the box center
     * @param control True for mole that player is controlling
     * @param width   The object width in physics units
     * @param height  The object width in physics units
     * @param idle    The idle action list passed from level creation.
     */
    public Mole(float x, float y, boolean control, float width, float height, String[] idle) {
        super(x, y, width, height);

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

        idleBehavior = idleActions;
        controlled = control;
        inventory = null;
    }
}
