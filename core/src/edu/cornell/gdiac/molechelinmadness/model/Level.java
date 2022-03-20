package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.GameCanvas;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;
import edu.cornell.gdiac.util.PooledList;

public class Level {

    //To be made editable in config later
    /** Width of the game world in Box2d units */
    protected static final float DEFAULT_WIDTH  = 32.0f;
    /** Height of the game world in Box2d units */
    protected static final float DEFAULT_HEIGHT = 18.0f;
    /** The default value of gravity (going down) */
    protected static final float DEFAULT_GRAVITY = 0f;

    /** All the objects in the world. */
    protected PooledList<Obstacle> objects  = new PooledList<Obstacle>();
    /** Queue for adding objects */
    protected PooledList<Obstacle> addQueue = new PooledList<Obstacle>();

    /** The Box2D world */
    protected World world;
    /** The boundary of the world */
    protected Rectangle bounds;
    /** The world scale */
    protected Vector2 scale;

    /** Whether or not debug mode is active */
    private boolean debug;

    //Physics objects for the game
    /** Reference to the moles */
    private Array<Mole> moles;
    /** Reference to the ingredients */
    private Array<Ingredient> ingredients;
    /** Reference to the final cooking station to win the level */
    private FinalStation goal;

    public Level() {
        world  = null;
        bounds = new Rectangle(0,0,1,1);
        scale = new Vector2(1,1);
        debug  = false;
    }

    /**
     * Lays out the game geography from the given JSON file
     *
     * @param directory 	the asset manager
     * @param levelFormat	the JSON file defining the level
     */
    public void populate(AssetDirectory directory, JsonValue levelFormat) {

        float gravity = levelFormat.getFloat("gravity");
        float[] pSize = levelFormat.get("physicsSize").asFloatArray();
        int[] gSize = levelFormat.get("graphicSize").asIntArray();

        world = new World(new Vector2(0,gravity),false);
        bounds = new Rectangle(0,0,pSize[0],pSize[1]);
        scale.x = gSize[0]/pSize[0];
        scale.y = gSize[1]/pSize[1];

        assert(directory != null);

        //Add all platforms
        JsonValue floor = levelFormat.get("platforms").child();
        while (floor != null) {
            Platform obj = new Platform();
            obj.initialize(directory,floor);
            obj.setDrawScale(scale);
            activate(obj);
            floor = floor.next();
        }

        //Add all interactive elements
        JsonValue interactable = levelFormat.get("interactive elements").child();
        while (interactable != null) {
            initializeInteractive(directory, interactable);
            interactable = interactable.next();
        }

        //Add all moles
        moles = new Array<>();
        JsonValue chefList = levelFormat.get("moles").get("list");
        //JsonValue chef = chefList;
        for (JsonValue chef : chefList) {
            System.out.println("1");
            Mole mole = new Mole();
            mole.initialize(directory, chef);
            mole.setDrawScale(scale);
            moles.add(mole);
            activate(mole);
        }
        moles.first().setControlled(true);

        //Add all ingredients
        ingredients = new Array<>();
        JsonValue ings = levelFormat.get("ingredient");

        for(JsonValue i : ings){
            System.out.println(2);
            Ingredient ingredient = new Ingredient();
            ingredient.initialize(directory, i);
            ingredient.setDrawScale(scale);
            ingredients.add(ingredient);
            activate(ingredient);

        }

    }


    /** Initialize all interactive elements like buttons, dumbwaiters, etc. */
    private void initializeInteractive(AssetDirectory directory, JsonValue json) {
        String type = json.getString("type");
        if (type.equals("dumbwaiter")) {
            Dumbwaiter dumbwaiter = new Dumbwaiter();
            dumbwaiter.initialize(directory, json);
            dumbwaiter.setDrawScale(scale);
            activate(dumbwaiter);
        }
    }

    public void dispose() {
        for(Obstacle obj : objects) {
            obj.deactivatePhysics(world);
        }
        objects.clear();
        if (world != null) {
            world.dispose();
            world = null;
        }
    }


    /**
     * Immediately adds the object to the physics world
     *
     * param obj The object to add
     */
    protected void activate(Obstacle obj) {
        assert inBounds(obj) : "Object is not in bounds";
        objects.add(obj);
        obj.activatePhysics(world);
    }


    /**
     * Returns true if the object is in bounds.
     *
     * This assertion is useful for debugging the physics.
     *
     * @param obj The object to check.
     *
     * @return true if the object is in bounds.
     */
    private boolean inBounds(Obstacle obj) {
        boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x+bounds.width);
        boolean vert  = (bounds.y <= obj.getY() && obj.getY() <= bounds.y+bounds.height);
        return horiz && vert;
    }


    /**
     * Draws the level to the given game canvas
     *
     * If debug mode is true, it will outline all physics bodies as wireframes. Otherwise
     * it will only draw the sprite representations.
     *
     * @param canvas	the drawing context
     */
    public void draw(GameCanvas canvas) {
        canvas.clear();

        canvas.begin();
        for(Obstacle obj : objects) {
            obj.draw(canvas);
        }
        canvas.end();

        if (debug) {
            canvas.beginDebug();
            for(Obstacle obj : objects) {
                obj.drawDebug(canvas);
            }
            canvas.endDebug();
        }
    }



    /**
     * Returns the bounding rectangle for the physics world
     *
     * The size of the rectangle is in physics, coordinates, not screen coordinates
     *
     * @return the bounding rectangle for the physics world
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Returns the scaling factor to convert physics coordinates to screen coordinates
     *
     * @return the scaling factor to convert physics coordinates to screen coordinates
     */
    public Vector2 getScale() {
        return scale;
    }


    /**
     * Returns a reference to the Box2D World
     *
     * @return a reference to the Box2D World
     */
    public World getWorld() {
        return world;
    }

    /**
     * Returns a reference to the chef moles
     *
     * @return a reference to the player avatar
     */
    public Array<Mole> getMoles() {
        return moles;
    }

    /**
     * Returns a reference to the exit door
     *
     * @return a reference to the exit door
     */
    public FinalStation getExit() {
        return goal;
    }

    /**
     * Returns whether this level is currently in debug node
     *
     * If the level is in debug mode, then the physics bodies will all be drawn as
     * wireframes onscreen
     *
     * @return whether this level is currently in debug node
     */
    public boolean getDebug() {
        return debug;
    }

    /**
     * Sets whether this level is currently in debug node
     *
     * If the level is in debug mode, then the physics bodies will all be drawn as
     * wireframes onscreen
     *
     * @param value	whether this level is currently in debug node
     */
    public void setDebug(boolean value) {
        debug = value;
    }



}
