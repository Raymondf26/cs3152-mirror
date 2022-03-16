package edu.cornell.gdiac.molechelinmadness.model;

import edu.cornell.gdiac.molechelinmadness.obstacle.CapsuleObstacle;

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
     * @param x      Initial x position of the box center
     * @param y      Initial y position of the box center
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public Mole(float x, float y, float width, float height) {
        super(x, y, width, height);
    }
}
