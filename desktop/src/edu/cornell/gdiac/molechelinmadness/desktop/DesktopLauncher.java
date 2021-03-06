package edu.cornell.gdiac.molechelinmadness.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import edu.cornell.gdiac.molechelinmadness.GDXRoot;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width  = 1280;
		config.height = 720;
		config.resizable = false;
		new LwjglApplication(new GDXRoot(), config);
	}
}
