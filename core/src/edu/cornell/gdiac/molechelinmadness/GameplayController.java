package edu.cornell.gdiac.molechelinmadness;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.Level;
import edu.cornell.gdiac.molechelinmadness.model.Mole;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;
import edu.cornell.gdiac.util.ScreenListener;

public class GameplayController implements Screen {


    //START: Constants we can extract into data later

    /** Exit code for quitting the game */
    public static final int EXIT_QUIT = 0;
    /** Exit code for advancing to next level */
    public static final int EXIT_NEXT = 1;
    /** Exit code for jumping back to previous level */
    public static final int EXIT_PREV = 2;
    /** How many frames after winning/losing do we continue? */
    public static final int EXIT_COUNT = 120;

    /** The amount of time for a physics engine step. */
    public static final float WORLD_STEP = 1/60.0f;
    /** Number of velocity iterations for the constrain solvers */
    public static final int WORLD_VELOC = 6;
    /** Number of position iterations for the constrain solvers */
    public static final int WORLD_POSIT = 2;

    //END


    /** Whether or not this is an active controller */
    private boolean active;
    /** Whether we have completed this level */
    private boolean complete;
    /** Whether we have failed at this world (and need a reset) */
    private boolean failed;
    /** Countdown active for winning or losing */
    private int countdown;

    /** Reference to the game canvas */
    protected GameCanvas canvas;
    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;

    /** Reference to the game level */
    protected Level level;

    /** Reference to the Interaction Controller */
    InteractionController interaction;


    //Assets:
    /** Need an ongoing reference to the asset directory */
    protected AssetDirectory directory;
    /** The JSON defining the level model */
    private JsonValue  levelFormat;
    /** The font for giving messages to the player */
    protected BitmapFont displayFont;



    public GameplayController() {

        level = new Level();
        complete = false;
        failed = false;
        active = false;
        countdown = -1;

        setComplete(false);
        setFailure(false);
    }

    public void gatherAssets (AssetDirectory directory) {
        this.directory = directory;
        // Access the assets used directly by this controller
        displayFont = directory.getEntry("display", BitmapFont.class);

        // This represents the level but does not BUILD it
        levelFormat = directory.getEntry( "level", JsonValue.class );
    }

    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
        level.dispose();

        setComplete(false);
        setFailure(false);
        countdown = -1;

        // Reload the json each time
        level.populate(directory, levelFormat);
        level.getWorld().setContactListener(interaction);
    }

    /**
     * Returns whether to process the update loop
     *
     * At the start of the update loop, we check if it is time
     * to switch to a new game mode.  If not, the update proceeds
     * normally.
     *
     * @param dt	Number of seconds since last animation frame
     *
     * @return whether to process the update loop
     */
    public boolean preUpdate(float dt) {


        //BEGIN: General preUpdate Checks

        InputController input = InputController.getInstance();
        input.readInput(level.getBounds(), level.getScale());
        if (listener == null) {
            return true;
        }

        // Toggle debug
        if (input.didDebug()) {
            level.setDebug(!level.getDebug());
        }

        // Handle resets
        if (input.didReset()) {
            reset();
        }

        // Now it is time to maybe switch screens.
        if (input.didExit()) {
            pause();
            listener.exitScreen(this, EXIT_QUIT);
            return false;
        } else if (countdown > 0) {
            countdown--;
        } else if (countdown == 0) {
            if (failed) {
                reset();
            } else if (complete) {
                pause();
                reset();
                return false;
            }
        }
        return true;

        //END


        //BEGIN: Game specific checks for preUpdate

        //END

    }


    /**
     * The core gameplay loop of this world.
     *
     * This method contains the specific update code for this game.
     * This method is called after input is read, but before collisions are resolved.
     * The very last thing that it should do is apply forces to the appropriate objects.
     *
     * @param dt	Number of seconds since last animation frame
     */
    public void update(float dt) {
        //Process actions in models
        Mole moles[] = level.getMoles();
        for (int i = 0; i < moles.length; i++) {
            /*if (moles[i] is being controlled) {
                set mole movement, jumping, and interacting based on Input Controller(similar to lab 4 platformer)
            }
            else {
                set mole movement, jumping, and interacting based on AI Controller
            }
             */

        }

        //Apply forces and sounds
        for (int i = 0; i < moles.length; i++) {
            //apply force for each mole
            //Play sounds relevant sounds
        }
    }

    /**
     * Processes physics
     *
     * Once the update phase is over, but before we draw, we are ready to handle
     * physics.  The primary method is the step() method in world.  This implementation
     * works for all applications and should not need to be overwritten.
     *
     * @param dt	Number of seconds since last animation frame
     */
    public void postUpdate(float dt) {
        // Add any objects created by actions
       /* while (!Level.add.isEmpty()) {
            addObject(addQueue.poll());
        } */

        // Turn the physics engine crank.
        level.getWorld().step(WORLD_STEP,WORLD_VELOC,WORLD_POSIT);

     /*   // Garbage collect the deleted objects.
        // Note how we use the linked list nodes to delete O(1) in place.
        // This is O(n) without copying.
        Iterator<PooledList<Obstacle>.Entry> iterator = objects.entryIterator();
        while (iterator.hasNext()) {
            PooledList<Obstacle>.Entry entry = iterator.next();
            Obstacle obj = entry.getValue();
            if (obj.isRemoved()) {
                obj.deactivatePhysics(world);
                entry.remove();
            } else {
                // Note that update is called last!
                obj.update(dt);
            }
        }*/

        //Grayed out stuff should be reimplemented later. Has to do with adding and removing objects.

    }

    public void draw(float dt) {
        canvas.clear();

        level.draw(canvas);

        // Final message
        if (complete && !failed) {
            displayFont.setColor(Color.YELLOW);
            canvas.begin(); // DO NOT SCALE
            canvas.drawTextCentered("VICTORY!", displayFont, 0.0f);
            canvas.end();
        } else if (failed) {
            displayFont.setColor(Color.RED);
            canvas.begin(); // DO NOT SCALE
            canvas.drawTextCentered("FAILURE!", displayFont, 0.0f);
            canvas.end();
        }
    }


    //START: Mostly boilerplate methods

    /**
     * Immediately adds the object to the physics world
     *
     * param obj The object to add
     */
    protected void addObject(Obstacle obj) {
        /* assert inBounds(obj) : "Object is not in bounds";
        objects.add(obj);
        obj.activatePhysics(world); */

        //Throw this code in level or something. Rn we don't need it but good to have implemented for future.
    }

    /**
     * Called when this screen becomes the current screen for a Game.
     */
    public void show() {
        active = true;
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    public void render(float delta) {
        if (active) {
            if (preUpdate(delta)) {
                update(delta); // This is the one that must be defined.
                postUpdate(delta);
            }
            draw(delta);
        }
    }

    /**
     * Called when the Screen is resized.
     *
     * This can happen at any point during a non-paused state but will never happen
     * before a call to show().
     *
     * @param width  The new width in pixels
     * @param height The new height in pixels
     */
    public void resize(int width, int height) {
        //IGNORE FOR NOW, may go back to implement
    }

    /**
     * Called when the Screen is paused.
     *
     * This is usually when it's not active or visible on screen. An Application is
     * also paused before it is destroyed.
     */
    public void pause() {
        //Auto-generated stub
    }

    /**
     * Called when the Screen is resumed from a paused state.
     *
     * This is usually when it regains focus.
     */
    public void resume() {
        //Auto-generated stub
    }

    /**
     * Called when this screen is no longer the current screen for a Game.
     */
    public void hide() {
        active = false;
    }

    /**
     * Called when this screen should release all resources.
     */
    public void dispose() {
        level.dispose();
        level = null;
        canvas =  null;
    }

    /**
     * Returns true if the level is completed.
     *
     * If true, the level will advance after a countdown
     *
     * @return true if the level is completed.
     */
    public boolean isComplete( ) {
        return complete;
    }

    /**
     * Sets whether the level is completed.
     *
     * If true, the level will advance after a countdown
     *
     * @param value whether the level is completed.
     */
    public void setComplete(boolean value) {
        if (value) {
            countdown = EXIT_COUNT;
        }
        complete = value;
    }

    /**
     * Returns true if the level is failed.
     *
     * If true, the level will reset after a countdown
     *
     * @return true if the level is failed.
     */
    public boolean isFailure( ) {
        return failed;
    }

    /**
     * Sets whether the level is failed.
     *
     * If true, the level will reset after a countdown
     *
     * @param value whether the level is failed.
     */
    public void setFailure(boolean value) {
        if (value) {
            countdown = EXIT_COUNT;
        }
        failed = value;
    }

    /**
     * Returns true if this is the active screen
     *
     * @return true if this is the active screen
     */
    public boolean isActive( ) {
        return active;
    }

    /**
     * Returns the canvas associated with this controller
     *
     * The canvas is shared across all controllers
     *
     * @return the canvas associated with this controller
     */
    public GameCanvas getCanvas() {
        return canvas;
    }

    /**
     * Sets the canvas associated with this controller
     *
     * The canvas is shared across all controllers.  Setting this value will compute
     * the drawing scale from the canvas size.
     *
     * @param canvas the canvas associated with this controller
     */
    public void setCanvas(GameCanvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

}
