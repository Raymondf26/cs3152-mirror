package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.GameCanvas;
import edu.cornell.gdiac.molechelinmadness.model.event.*;
import edu.cornell.gdiac.molechelinmadness.model.event.Door;
import edu.cornell.gdiac.molechelinmadness.model.interactor.Button;
import edu.cornell.gdiac.molechelinmadness.model.interactor.Interactor;
import edu.cornell.gdiac.molechelinmadness.model.interactor.PressurePlate;
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

    /** Texture asset for background image */
    private TextureRegion backgroundTexture;

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
    /** Temporary control mole */
    public int controlMole;

    /** Whether or not debug mode is active */
    private boolean debug;

    //Physics objects for the game
    /** Reference to the moles */
    private Array<Mole> moles;
    /** Reference to the ingredients */
    private Array<Ingredient> ingredients;
    /** Reference to the cooking stations */
    private Array<CookingStation> stations;
    /** Reference to the final cooking station to win the level */
    private FinalStation goal;
    /** Reference to interactive objects as a map*/
    private ObjectMap<String, GameObject> gameObjects;

    public Level() {
        world  = null;
        bounds = new Rectangle(0,0,1,1);
        scale = new Vector2(1,1);
        debug  = false;
        gameObjects = new ObjectMap<>();
    }

    /**
     * Lays out the game geography from the given JSON file
     *
     * @param directory 	the asset manager
     * @param levelFormat	the JSON file defining the level
     */
    public void populate(AssetDirectory directory, JsonValue levelFormat) {
        backgroundTexture = new TextureRegion(directory.getEntry( "background", Texture.class ));

        float gravity = levelFormat.getFloat("gravity");
        float[] pSize = levelFormat.get("physicsSize").asFloatArray();
        int[] gSize = levelFormat.get("graphicSize").asIntArray();

        world = new World(new Vector2(0, gravity), false);
        bounds = new Rectangle(0, 0, pSize[0], pSize[1]);
        scale.x = gSize[0] / pSize[0];
        scale.y = gSize[1] / pSize[1];


        //Add all platforms
        JsonValue floor = levelFormat.get("platforms").child();
        while (floor != null) {
            Platform obj = new Platform();
            obj.initialize(directory, floor);
            obj.setDrawScale(scale);
            activate(obj);
            floor = floor.next();
        }


        //Add all walls
        JsonValue wall = levelFormat.get("walls").child();
        while (wall != null) {
            Wall obj = new Wall();
            obj.initialize(directory, wall);
            obj.setDrawScale(scale);
            activate(obj);
            wall = wall.next();
        }


        //Add all interactive elements
        JsonValue interactable = levelFormat.get("interactive elements").child();
        while (interactable != null) {
            initializeInteractive(directory, interactable);
            interactable = interactable.next();
        }


        //Add all interactors
        JsonValue interactors = levelFormat.get("interactors").child();
        while (interactors != null) {
            initializeInteractors(directory, interactors);
            interactors = interactors.next();
        }


        //Add all moles
        moles = new Array<>();
        JsonValue chefList = levelFormat.get("moles").get("list");
        //JsonValue chef = chefList;
        for (JsonValue chef : chefList) {
            Mole mole = new Mole();
            mole.initialize(directory, chef);
            mole.setDrawScale(scale);
            moles.add(mole);
            activate(mole);
        }
        controlMole = levelFormat.get("moles").getInt("starting");
        moles.get(controlMole).setControlled(true);


        //Add all ingredients
        ingredients = new Array<>();
        JsonValue ings = levelFormat.get("ingredient");

        for (JsonValue i : ings) {
            Ingredient ingredient = new Ingredient();
            ingredient.initialize(directory, i);
            ingredient.setDrawScale(scale);
            ingredients.add(ingredient);
            gameObjects.put(i.getString("type"), ingredient);
            activate(ingredient);
        }


        //Add all cooking stations
        stations = new Array<>();
        JsonValue cooking = levelFormat.get("cooking");
        for (JsonValue c : cooking) {
            CookingStation station = new CookingStation();
            station.initialize(directory, c);
            station.setDrawScale(scale);
            stations.add(station);
            gameObjects.put(c.getString("name"), station);
            activate(station);

        }
    }

    public ObjectMap.Values<GameObject> getGameObjects() {return gameObjects.values();}


    /** Initialize all interactive elements like rotating platforms, dumbwaiters, etc. */
    private void initializeInteractive(AssetDirectory directory, JsonValue json) {
        String type = json.getString("type");
        if (type.equals("dumbwaiter")) {
            Dumbwaiter dumbwaiter = new Dumbwaiter();
            dumbwaiter.initialize(directory, json);
            dumbwaiter.setDrawScale(scale);
            gameObjects.put(json.getString("name"), dumbwaiter);
            activate(dumbwaiter);
        }
        else if (type.equals("rotating_platform")) {
            RotatingPlatform rotatingPlatform = new RotatingPlatform();
            rotatingPlatform.initialize(directory, json);
            rotatingPlatform.setDrawScale(scale);
            gameObjects.put(json.getString("name"), rotatingPlatform);
            activate(rotatingPlatform);
        }
        else {
            System.err.println("Not interactive element of the type provided found: " + type);
        }
    }

    /** Initialize all interactor elements like pressure plates, etc. */
    private void initializeInteractors(AssetDirectory directory, JsonValue json) {
        String type = json.getString("type");
        if (type.equals("pressureplate")) {
            PressurePlate pressureplate = new PressurePlate();
            pressureplate.initialize(directory, json);
            pressureplate.setDrawScale(scale);
            initializeEvents(pressureplate, json);
            gameObjects.put(json.getString("name"), pressureplate);
            activate(pressureplate.getBody());
        }

        else if (type.equals("button")) {
            Button button = new Button();
            button.initialize(directory, json);
            button.setDrawScale(scale);
            initializeEvents(button, json);
            gameObjects.put(json.getString("name"), button);
            activate(button.getBody());
        }

        else {
            System.err.println("No interactor of the type provided found: " + type);
        }
    }

    private void initializeEvents(Interactor messenger, JsonValue json) {
        JsonValue link = json.get("link").child();
        int msg = 1; //Note: the order of adding these events matter. Interactors will dispatch using msgs based on the order added here.
        while (link != null) {
            String listenerName = link.getString("name");
            GameObject listener = gameObjects.get(listenerName);
            Event event = parseEvent(link.getString("event"), link);
            messenger.addEvent(event);
            messenger.addListeners(listener, msg, -msg);
            msg += 1;
            link = link.next();
        }
    }

    private Event parseEvent(String string, JsonValue link) {

        Event event;

        if (string.equals("door")) {
            event = new Door();
        }
        else if (string.equals("dumbwaiter")) {
            event = new EDumbwaiter();
        }
        else {
            System.err.println("no event of that name");
            event = null;
        }
        assert event != null;
        event.initialize(link);
        return event;

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
        canvas.draw(backgroundTexture, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.end();

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

    /** Return cooking stations*/
    public Array<CookingStation> getStations(){
        return stations;
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
