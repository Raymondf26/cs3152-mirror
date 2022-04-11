package edu.cornell.gdiac.molechelinmadness;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.util.ScreenListener;

/**
 * The root controller. Instantiates GameplayController, LevelSelector, and LoadingMode.
 */
public class GDXRoot extends Game implements ScreenListener {
	/** AssetManager to load game assets (textures, sounds, etc.) */
	AssetDirectory directory;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas;
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;
	/** Player mode for the main menu and level-select screen (CONTROLLER CLASS) */
	private LevelSelector menu;
	/** Player mode for the game proper (CONTROLLER CLASS) */
	private GameplayController controller;

	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 */
	public GDXRoot() { }

	/**
	 * Called when the Application is first created.
	 *
	 * This is method immediately loads assets for the loading screen, and prepares
	 * the asynchronous loader for all other assets.
	 */
	public void create() {
		canvas  = new GameCanvas();
		loading = new LoadingMode("assets.json",canvas,1);

		// Initialize the game world
		controller = new GameplayController();
		loading.setScreenListener(this);
		setScreen(loading);
	}

	/**
	 * Called when the Application is destroyed.
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		setScreen(null);
		controller.dispose();

		canvas.dispose();
		canvas = null;

		// Unload all of the resources
		if (directory != null) {
			directory.unloadAssets();
			directory.dispose();
			directory = null;
		}
		super.dispose();
	}

	/**
	 * Called when the Application is resized.
	 *
	 * This can happen at any point during a non-paused state but will never happen
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}

	public void exitScreen(Screen screen, int exitCode, int levelIndex) {
		if(screen == menu) {
			controller.setLevel(levelIndex);
			this.exitScreen(screen, exitCode);
		}
	}

	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		if (screen == loading) {
			directory = loading.getAssets();
			menu = new LevelSelector(canvas);

			menu.gatherAssets(directory);
			menu.setScreenListener(this);
			System.out.println("here");

			setScreen(menu);

			loading.dispose();
			loading = null;
		} else if (screen == menu) {
			controller.gatherAssets(directory);
			controller.setScreenListener(this);
			controller.setCanvas(canvas);
			controller.reset();
			setScreen(controller);
			menu.dispose();
			menu = null;

		} else if (exitCode == GameplayController.EXIT_QUIT) {
			// We quit the main application
			Gdx.app.exit();
		} else if (exitCode == GameplayController.EXIT_NEXT) {
			
		} else if (exitCode == GameplayController.EXIT_PREV) {

		}
	}

}
