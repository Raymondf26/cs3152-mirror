package edu.cornell.gdiac.molechelinmadness.model;

import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;

public class FinalStation extends BoxObstacle {


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
    public FinalStation(float width, float height) {
        super(width, height);
    }
}
