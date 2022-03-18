package edu.cornell.gdiac.molechelinmadness.model;

import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;

public class CookingStation extends BoxObstacle {

    /** Whether it's in the state of being interacted with */
    private boolean interacting;

    /** How far along cooking has gone */
    private float progress;

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
    public CookingStation(float width, float height) {
        super(width, height);
    }
}
