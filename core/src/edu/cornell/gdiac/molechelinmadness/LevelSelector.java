package edu.cornell.gdiac.molechelinmadness;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.Level;
import edu.cornell.gdiac.util.ScreenListener;

import java.util.Map;

public class LevelSelector implements Screen, InputProcessor, ControllerListener {

    /** Array of level names to be extracted */
    public static String[] levels;

    /** Play button to display */
    private Texture playButton;

    private ArrayMap<Integer, Vector2> levelButtonPositions;

    /** Current index in level array */
    private int levelIndex;

    /** Whether or not this is an active controller */
    private boolean active;

    /** The current state of the play button */
    private int   pressState;

    /** Standard window size (for scaling) */
    private static int STANDARD_WIDTH  = 800;
    /** Standard window height (for scaling) */
    private static int STANDARD_HEIGHT = 700;
    /** Scale of the button */
    private static float BUTTON_SCALE  = 0.75f;

    // Assets:
    /** Need an ongoing reference to the asset directory */
    protected AssetDirectory directory;
    /** The JSON defining the level model */
    private JsonValue  levelFormat;
    /** The font for giving messages to the player */
    protected BitmapFont displayFont;
    /** Background texture for start-up */
    private Texture background;

    /** The width of the progress bar */
    private int width;
    /** The y-coordinate of the center of the progress bar */
    private int centerY;
    /** The x-coordinate of the center of the progress bar */
    private int centerX;
    /** The height of the canvas window (necessary since sprite origin != screen origin) */
    private int heightY;
    /** Scaling factor for when the student changes the resolution. */
    private float scale;

    // There are TWO asset managers.  One to load the menu screen.  The other to load the assets
    /** Internal assets for this loading screen */
    private AssetDirectory internal;
    /** The actual assets to be loaded */
    private AssetDirectory assets;

    /** Reference to GameCanvas created by the root */
    private GameCanvas canvas;
    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;


    public LevelSelector(GameCanvas canvas) {
        levelIndex = 0;
        active = false;
        this.canvas = canvas;

        // Compute the dimensions from the canvas
        resize(canvas.getWidth(),canvas.getHeight());

        // Deal with assets later, menu.json to be created
//         We need these files loaded immediately
        internal = new AssetDirectory( "menu.json" );
        internal.loadAssets();
        internal.finishLoading();

        levelButtonPositions = new ArrayMap<Integer, Vector2>();
        loadLevelNames();

        heightY = canvas.height;

        playButton = internal.getEntry("play",Texture.class);

        background = internal.getEntry( "background", Texture.class );
        background.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        displayFont = internal.getEntry("shared:retro", BitmapFont.class);
        pressState = 0; // no button pressed

        Gdx.input.setInputProcessor( this );

//        assets = new AssetDirectory( file );
//        assets.loadAssets();
//        active = true;
    }

    /**
     * Called when this screen should release all resources.
     */
    public void dispose() {
        internal.unloadAssets();
        internal.dispose();
    }

    public boolean isReady() {
        return pressState == 2;
    }

    /**
     * Update the status of this player mode.
     *
     * We prefer to separate update and draw from one another as separate methods, instead
     * of using the single render() method that LibGDX does.  We will talk about why we
     * prefer this in lecture.
     *
     * @param delta Number of seconds since last animation frame
     */
    private void update(float delta) {
//        if (playButton == null) {
//            assets.update(budget);
//            this.progress = assets.getProgress();
//            if (progress >= 1.0f) {
//                this.progress = 1.0f;
//                playButton = internal.getEntry("play", Texture.class);
//            }
//        }
    }

    /**
     * Draw a "level box" - each level has a name and click to play button for now
     *
     * @param level the level to draw the box for
     */
    private void drawLevel(int level) {
        String levelName = levels[level];

        float y = canvas.getHeight() / 2 + playButton.getHeight() /2;
        float x = canvas.getWidth() / 4 * (level + 1);
        canvas.drawText(levelName, displayFont, x, y);
        Color tint = ((pressState == 1 && levelIndex == level)? Color.GRAY: Color.WHITE);
        canvas.draw(playButton, tint, playButton.getWidth()/2, playButton.getHeight()/2,
                x, y - playButton.getHeight() / 2, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
        if (!levelButtonPositions.containsKey(level)) levelButtonPositions.insert(level, level, new Vector2(x, y - playButton.getHeight()/2));
    }

    /**
     * Draw the status of this player mode.
     *
     * We prefer to separate update and draw from one another as separate methods, instead
     * of using the single render() method that LibGDX does.  We will talk about why we
     * prefer this in lecture.
     */
    private void draw() {
        canvas.begin();
        canvas.draw(background, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());
        for (int i = 0; i < levels.length; i ++) {
            drawLevel(i);
        }

        canvas.end();
    }


    // ADDITIONAL SCREEN METHODS
    /**
     * Called when the Screen should render itself.
     *
     * We defer to the other methods update() and draw().  However, it is VERY important
     * that we only quit AFTER a draw.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void render(float delta) {
      if (active) {
//          //  update(delta);
           draw();

            // We are are ready, notify our listener
            if (isReady() && listener != null) {
                listener.exitScreen(this, 0, levelIndex);
            }
        }
    }

    public AssetDirectory getAssets() {
        return assets;
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
// Compute the drawing scale
        float sx = ((float)width)/STANDARD_WIDTH;
        float sy = ((float)height)/STANDARD_HEIGHT;
        scale = (sx < sy ? sx : sy);

        this.width = width;
        centerY = height;
        centerX = width/2;
        heightY = height;
    }

    /**
     * Called when the Screen is paused.
     *
     * This is usually when it's not active or visible on screen. An Application is
     * also paused before it is destroyed.
     */
    public void pause() {
        // TODO Auto-generated method stub

    }

    /**
     * Called when the Screen is resumed from a paused state.
     *
     * This is usually when it regains focus.
     */
    public void resume() {
        // TODO Auto-generated method stub

    }

    /**
     * Called when this screen becomes the current screen for a Game.
     */
    public void show() {
        // Useless if called in outside animation loop
        active = true;
    }

    /**
     * Called when this screen is no longer the current screen for a Game.
     */
    public void hide() {
        // Useless if called in outside animation loop
        active = false;
    }

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }





    public void gatherAssets (AssetDirectory directory) {
        this.directory = directory;
        // Access the assets used directly by this controller
        displayFont = directory.getEntry("shared:retro", BitmapFont.class);

        // This represents the level but does not BUILD it
        levelFormat = directory.getEntry( levels[levelIndex], JsonValue.class );
    }


    public boolean keyDown(int keycode) {
        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if ( pressState == 2) {
            return true;
        }

        // Flip to match graphics coordinates
        screenY = heightY-screenY;

        // TODO: Fix scaling
        // Play button is a circle.
        float radius = BUTTON_SCALE*scale*playButton.getWidth()/2.0f;
        // very inefficient fix this
        for (int i = 0; i < levelButtonPositions.size; i ++) {
            float buttonX = levelButtonPositions.get(i).x;
            float buttonY = levelButtonPositions.get(i).y;
            System.out.println("button center " + buttonX + " " + buttonY);
            System.out.println("screen pos " + screenX + " " + screenY);
            float dist = (screenX-buttonX)*(screenX-buttonX)+(screenY-buttonY)*(screenY-buttonY);
            if (dist < radius*radius) {
                pressState = 1;
                levelIndex = i;
            }
        }

        return false;
    }

    /**
     * The corresponding index into the levels array based on where the click took place.
     * @param screenX
     * @param screenY
     * @return -1 if not valid playbutton click, otherwise the index of the level
     */
    private int buttonIndex(int screenX, int screenY) {
    return 0;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pressState == 1) {
            pressState = 2;
            return false;
        }
        return true;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void connected(Controller controller) {

    }

    public void disconnected(Controller controller) {

    }

    public boolean buttonDown(Controller controller, int buttonCode) {
        if (pressState == 0) {
            ControllerMapping mapping = controller.getMapping();
            if (mapping != null && buttonCode == mapping.buttonStart ) {
                pressState = 1;
                return false;
            }
        }
        return true;
    }

    public boolean buttonUp(Controller controller, int buttonCode) {
        if (pressState == 1) {
            ControllerMapping mapping = controller.getMapping();
            if (mapping != null && buttonCode == mapping.buttonStart ) {
                pressState = 2;
                return false;
            }
        }
        return true;
    }

    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    /**
     * Helper method to load the names of the levels from the asset directory for the level selector to the
     * class variable of level names
     */
    private void loadLevelNames() {
        Array<String> allKeys = internal.getEntryKeys();
        Array<String> levels = new Array<String>();

        for (int i = 0; i < allKeys.size; i++) {
            String[] split = allKeys.get(i).split("level:");
            if (split.length > 1) levels.add(split[1]);
        }
        this.levels = new String[levels.size];
        for (int i = 0; i < levels.size; i++) {
            this.levels[i] = levels.get(i);
        }
    }
}
