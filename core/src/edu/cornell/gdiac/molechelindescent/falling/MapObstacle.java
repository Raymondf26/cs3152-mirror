package edu.cornell.gdiac.molechelindescent.falling;

import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.molechelindescent.obstacle.PolygonObstacle;

public class MapObstacle extends PolygonObstacle {
    /** An earclipping triangular to make sure we work with convex shapes */
    private static final EarClippingTriangulator TRIANGULATOR = new EarClippingTriangulator();

    /** Shape information for this physics object */
    protected PolygonShape[] shapes;
    /** Texture information for this object */
    protected PolygonRegion region;

    /** The polygon vertices, scaled for drawing */
    private float[] scaled;
    /** The triangle indices, used for drawing */
    private short[] tridx;

    /** A cache value for the fixtures (for resizing) */
    private Fixture[] geoms;
    /** The polygon bounding box (for resizing purposes) */
    private Vector2 dimension;
    /** A cache value for when the user wants to access the dimensions */
    private Vector2 sizeCache;
    /** Cache of the polygon vertices (for resizing) */
    private float[] vertices;
    /** Texture for this Obstacle */
    TextureRegion texture;

    public MapObstacle(float[] points, float x, float y, TextureRegion texture, Vector2 scale){
        super(points, x, y);
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(0.0f);
        setFriction(0.0f);
        setRestitution(0.0f);
        setSensor(true);
        setDrawScale(scale);
        setTexture(texture);
        this.texture = texture;

    }

}
