package edu.cornell.gdiac.molechelinmadness;

import edu.cornell.gdiac.molechelinmadness.model.*;

/**
 * Updates the idle actions for the Mole characters while the player is not controlling them.
 */
public class AIController {

    /** Set of ordered IdleUnits to loop through*/
    private Mole.IdleUnit[] idleUnits;

    /** Position in idleUnits */
    private int index;

    /** Time since we last changed the state */
    private float elapsedTime;

    public AIController(Mole.IdleUnit[] idleUnits) {
        this.idleUnits = idleUnits;
        index = 0;
        elapsedTime = 0.0f;
    }

    /**
     *
     * @return -1 if moving left, 0 if not moving, 1 if moving right
     */
    public int getHorizontal() {
        if (idleUnits[index].idle == Mole.IdleAction.LEFT) return -1;
        else if (idleUnits[index].idle == Mole.IdleAction.RIGHT) return 1;
        return 0;
    }

    /**
     *
     * @return true if jumping, false else
     */
    public boolean getJump() {
        if (idleUnits[index].idle == Mole.IdleAction.JUMP) return true;
        return false;
    }

    /**
     *
     * @return true if interacting, false if otherwise
     */
    public boolean getInteract() {
        if (idleUnits[index].idle == Mole.IdleAction.INTERACT) return true;
        return false;
    }

    /**
     * Update position in idle actions based on amount of time passed
     * @param dt amount of time passed since last frame and this one
     */
    public void update(float dt) {
        elapsedTime += dt;
        if (elapsedTime >= idleUnits[index].time) {
            elapsedTime = 0.0f;
            if (index == (idleUnits.length - 1)) {
                index = 0;
            }
            else {
                index++;
            }
        }
    }
}
