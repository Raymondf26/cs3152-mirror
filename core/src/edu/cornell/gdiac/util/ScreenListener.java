package edu.cornell.gdiac.util;

import com.badlogic.gdx.Screen;

//Ripped from physics lab

/**
 * A listener class for responding to a screen's request to exit.
 *
 * This interface is almost always implemented by the root Game class.
 */
public interface ScreenListener {

    /**
     * The given screen has made a request to exit its player mode.
     *
     * The value exitCode can be used to implement menu options.
     *
     * @param screen   The screen requesting to exit
     * @param exitCode The state of the screen upon exit
     */
    public void exitScreen(Screen screen, int exitCode);
}
