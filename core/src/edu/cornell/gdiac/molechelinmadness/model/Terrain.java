package edu.cornell.gdiac.molechelinmadness.model;

import edu.cornell.gdiac.molechelinmadness.obstacle.PolygonObstacle;

public class Terrain extends PolygonObstacle {
    /**
     * Creates a (not necessarily convex) polygon at the origin.
     * <p>
     * The points given are relative to the polygon's origin.  They
     * are measured in physics units.  They tile the image according
     * to the drawScale (which must be set for drawing to work
     * properly).
     *
     * @param points The polygon vertices
     */
    public Terrain(float[] points) {
        super(points);
    }
}
