/*
 * PlatformController.java
 *
 * You SHOULD NOT need to modify this file.  However, you may learn valuable lessons
 * for the rest of the lab by looking at it.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package edu.cornell.gdiac.molechelindescent.idle;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelindescent.*;
import edu.cornell.gdiac.molechelindescent.obstacle.*;

/**
 * Gameplay specific controller for the platformer game.  
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class PlatformController extends WorldController implements ContactListener {
	/** Texture asset for character avatar */
	private TextureRegion avatarTexture;
	/** Texture asset for the spinning barrier */
	private TextureRegion barrierTexture;
	/** Texture asset for the bullet */
	private TextureRegion bulletTexture;
	/** Texture asset for the bridge plank */
	private TextureRegion bridgeTexture;

	/** The jump sound.  We only want to play once. */
	private Sound jumpSound;
	private long jumpId = -1;
	/** The weapon fire sound.  We only want to play once. */
	private Sound fireSound;
	private long fireId = -1;
	/** The weapon pop sound.  We only want to play once. */
	private Sound plopSound;
	private long plopId = -1;
	/** The default sound volume */
	private float volume;

	// Physics objects for the game
	/** Physics constants for initialization */
	private JsonValue constants;
	/** Reference to the character avatar */
	private DudeModel jumpDude;
	/** Reference to side avatar */
	private DudeModel sideDude;
	/** Reference to button 1 */
	private Button button1;
	/** Reference to button 2*/
	private Button button2;
	/** Reference to trap door */
	private TrapDoor trapDoor;
	/** Reference to second trap door */
	private TrapDoor trapDoor2;
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;

	/** Mark set to handle more sophisticated collision callbacks */
	protected ObjectSet<Fixture> sensorFixtures;
	protected ObjectSet<Fixture> sensorFixtures2;

	/** Controller for side behavior AI*/
	private AISideController sideController;
	private AIJumpController jumpController;

	/** Variable to keep track of which character being controlled */
	private int controlling;

	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public PlatformController() {
		super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		sensorFixtures = new ObjectSet<Fixture>();
		sensorFixtures2 = new ObjectSet<>();

		//My AI stuff
		sideController = new AISideController();
		jumpController = new AIJumpController();
		controlling = 0;
	}

	/**
	 * Gather the assets for this controller.
	 *
	 * This method extracts the asset variables from the given asset directory. It
	 * should only be called after the asset directory is completed.
	 *
	 * @param directory	Reference to global asset manager.
	 */
	public void gatherAssets(AssetDirectory directory) {
		avatarTexture  = new TextureRegion(directory.getEntry("platform:dude",Texture.class));
		barrierTexture = new TextureRegion(directory.getEntry("platform:barrier",Texture.class));
		bulletTexture = new TextureRegion(directory.getEntry("platform:bullet",Texture.class));
		bridgeTexture = new TextureRegion(directory.getEntry("platform:rope",Texture.class));

		jumpSound = directory.getEntry( "platform:jump", Sound.class );
		fireSound = directory.getEntry( "platform:pew", Sound.class );
		plopSound = directory.getEntry( "platform:plop", Sound.class );

		constants = directory.getEntry( "platform:constants", JsonValue.class );
		super.gatherAssets(directory);
	}
	
	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(world.getGravity() );
		
		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();
		
		world = new World(gravity,false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);
		populateLevel();
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		// Add level goal
		float dwidth  = goalTile.getRegionWidth()/scale.x;
		float dheight = goalTile.getRegionHeight()/scale.y;

		JsonValue goal = constants.get("goal");
		JsonValue goalpos = goal.get("pos");
		goalDoor = new BoxObstacle(goalpos.getFloat(0),goalpos.getFloat(1),dwidth,dheight);
		goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
		goalDoor.setDensity(goal.getFloat("density", 0));
		goalDoor.setFriction(goal.getFloat("friction", 0));
		goalDoor.setRestitution(goal.getFloat("restitution", 0));
		goalDoor.setSensor(true);
		goalDoor.setDrawScale(scale);
		goalDoor.setTexture(goalTile);
		goalDoor.setName("goal");
		addObject(goalDoor);

	    String wname = "wall";
	    JsonValue walljv = constants.get("walls");
	    JsonValue defaults = constants.get("defaults");
	    for (int ii = 0; ii < walljv.size; ii++) {
	        PolygonObstacle obj;
	    	obj = new PolygonObstacle(walljv.get(ii).asFloatArray(), 0, 0);
			obj.setBodyType(BodyDef.BodyType.StaticBody);
			obj.setDensity(defaults.getFloat( "density", 0.0f ));
			obj.setFriction(defaults.getFloat( "friction", 0.0f ));
			obj.setRestitution(defaults.getFloat( "restitution", 0.0f ));
			obj.setDrawScale(scale);
			obj.setTexture(earthTile);
			obj.setName(wname+ii);
			addObject(obj);
	    }
	    
	    String pname = "platform";
		JsonValue platjv = constants.get("myplatforms");
	    for (int ii = 0; ii < platjv.size; ii++) {
	        PolygonObstacle obj;
	    	obj = new PolygonObstacle(platjv.get(ii).asFloatArray(), 0, 0);
			obj.setBodyType(BodyDef.BodyType.StaticBody);
			obj.setDensity(defaults.getFloat( "density", 0.0f ));
			obj.setFriction(defaults.getFloat( "friction", 0.0f ));
			obj.setRestitution(defaults.getFloat( "restitution", 0.0f ));
			obj.setDrawScale(scale);
			obj.setTexture(earthTile);
			obj.setName(pname+ii);
			addObject(obj);
	    }

	    // This world is heavier
		world.setGravity( new Vector2(0,defaults.getFloat("gravity",0)) );

		// Create jump dude
		dwidth  = avatarTexture.getRegionWidth()/scale.x;
		dheight = avatarTexture.getRegionHeight()/scale.y;
		jumpDude = new DudeModel(constants.get("dude"), dwidth, dheight);
		jumpDude.setDrawScale(scale);
		jumpDude.setTexture(avatarTexture);
		addObject(jumpDude);

		// Create side-to-side dude
		dwidth  = avatarTexture.getRegionWidth()/scale.x;
		dheight = avatarTexture.getRegionHeight()/scale.y;
		sideDude = new DudeModel(constants.get("dude2"), dwidth, dheight);
		sideDude.setDrawScale(scale);
		sideDude.setTexture(avatarTexture);
		sideDude.setSensorName("SideGroundSensor");
		addObject(sideDude);

		//Create buttons
		dwidth = bulletTexture.getRegionWidth()/scale.x;
		button2 = new Button(7f, 9.5f, dwidth);
		button2.setDrawScale(scale);
		button2.setTexture(bulletTexture);
		button2.setName("button");
		button2.setSensorName("button");
		addObject(button2);

		dwidth = bulletTexture.getRegionWidth()/scale.x;
		button1 = new Button(17.5f, 1f, dwidth);
		button1.setDrawScale(scale);
		button1.setTexture(bulletTexture);
		button1.setName("button1");
		button1.setSensorName("button1");
		addObject(button1);

		//Create trap doors
		float[] points = {12f, 12f, 15f, 12f, 15f, 11.5f, 12f, 11.5f};
		trapDoor = new TrapDoor(points);
		trapDoor.setDrawScale(scale);
		trapDoor.setTexture(earthTile);
		trapDoor.setName("trap door");
		addObject(trapDoor);

		float[] points2 = {13f, 9f, 13.5f, 9f, 13.5f, 6f, 13f, 6f};
		trapDoor2 = new TrapDoor(points2);
		trapDoor2.setDrawScale(scale);
		trapDoor2.setTexture(earthTile);
		trapDoor2.setName("trap door");
		addObject(trapDoor2);

//		// Create rope bridge
//		dwidth  = bridgeTexture.getRegionWidth()/scale.x;
//		dheight = bridgeTexture.getRegionHeight()/scale.y;
//		RopeBridge bridge = new RopeBridge(constants.get("bridge"), dwidth, dheight);
//		bridge.setTexture(bridgeTexture);
//		bridge.setDrawScale(scale);
//		addObject(bridge);
		
//		// Create spinning platform
//		dwidth  = barrierTexture.getRegionWidth()/scale.x;
//		dheight = barrierTexture.getRegionHeight()/scale.y;
//		Spinner spinPlatform = new Spinner(constants.get("spinner"),dwidth,dheight);
//		spinPlatform.setDrawScale(scale);
//		spinPlatform.setTexture(barrierTexture);
//		addObject(spinPlatform);

		volume = constants.getFloat("volume", 1.0f);
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
		if (!super.preUpdate(dt)) {
			return false;
		}
		
		if (!isFailure() && jumpDude.getY() < -1) {
			setFailure(true);
			return false;
		}
		
		return true;
	}

	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 *
	 * @param dt	Number of seconds since last animation frame
	 */
	public void update(float dt) {

		// Process character switching
		if (InputController.getInstance().didSecondary()) {
			controlling = controlling == 0 ? 1 : 0;
		}

		// Process actions in jump model
		if (controlling == 0) {
			jumpDude.setMovement(InputController.getInstance().getHorizontal() * jumpDude.getForce());
			jumpDude.setJumping(InputController.getInstance().didPrimary());
			jumpDude.setPressing(InputController.getInstance().didTertiary());
		}
		else {
			jumpDude.setJumping(jumpController.didPrimary());
			jumpDude.setPressing(false);
			jumpController.update(dt);
		}

		//Process actions for side model
		if (controlling == 1) {
			sideDude.setMovement(InputController.getInstance().getHorizontal() * sideDude.getForce());
			sideDude.setJumping(InputController.getInstance().didPrimary());
			sideDude.setPressing(InputController.getInstance().didTertiary());
		}
		else {
			sideDude.setMovement(sideController.getHorizontal() * sideDude.getForce());
			sideDude.setPressing(false);
			sideController.update(dt);
		}
		
		// Add a bullet if we fire
		if (jumpDude.isShooting()) {
			createBullet();
		}

		// Check for button contact

		if (button2.inContact()) {
			if (jumpDude.isPressing()) {
				button2.toggleOn();
				//System.out.println("toggle on");
			}
			else {
				button2.toggelOff();
			}
		}
		else {
			button2.toggelOff();
			//System.out.println("not in contact");
		}

		if (button1.inContact()) {
			if (sideDude.isPressing()) {
				button1.toggleOn();
			}
			else {
				button1.toggelOff();
			}
		}
		else {
			button1.toggelOff();
		}

		// Check for trap door
		if (button2.isOn()) {
			trapDoor2.triggerOn();
		}
		else {
			trapDoor2.triggerOff();
		}

		if (button1.isOn()) {
			trapDoor.triggerOn();
		}
		else {
			trapDoor.triggerOff();
		}

		//Apply forces
		jumpDude.applyForce();

		sideDude.applyForce();
		if (sideDude.isJumping()) {
			jumpId = playSound( jumpSound, jumpId, volume );
		}
	}

	/**
	 * Add a new bullet to the world and send it in the right direction.
	 */
	private void createBullet() {
		JsonValue bulletjv = constants.get("bullet");
		float offset = bulletjv.getFloat("offset",0);
		offset *= (jumpDude.isFacingRight() ? 1 : -1);
		float radius = bulletTexture.getRegionWidth()/(2.0f*scale.x);
		WheelObstacle bullet = new WheelObstacle(jumpDude.getX()+offset, jumpDude.getY(), radius);
		
	    bullet.setName("bullet");
		bullet.setDensity(bulletjv.getFloat("density", 0));
	    bullet.setDrawScale(scale);
	    bullet.setTexture(bulletTexture);
	    bullet.setBullet(true);
	    bullet.setGravityScale(0);
		
		// Compute position and velocity
		float speed = bulletjv.getFloat( "speed", 0 );
		speed  *= (jumpDude.isFacingRight() ? 1 : -1);
		bullet.setVX(speed);
		addQueuedObject(bullet);

		fireId = playSound( fireSound, fireId );
	}
	
	/**
	 * Remove a new bullet from the world.
	 *
	 * @param  bullet   the bullet to remove
	 */
	public void removeBullet(Obstacle bullet) {
	    bullet.markRemoved(true);
	    plopId = playSound( plopSound, plopId );
	}

	
	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when we first get a collision between two objects.  We use 
	 * this method to test if it is the "right" kind of collision.  In particular, we
	 * use it to test if we made it to the win door.
	 *
	 * @param contact The two bodies that collided
	 */
	public void beginContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();
		
		try {
			Obstacle bd1 = (Obstacle)body1.getUserData();
			Obstacle bd2 = (Obstacle)body2.getUserData();

			// Test bullet collision with world
			if (bd1.getName().equals("bullet") && bd2 != jumpDude) {
		        removeBullet(bd1);
			}

			if (bd2.getName().equals("bullet") && bd1 != jumpDude) {
		        removeBullet(bd2);
			}

			// See if we have landed on the ground.
			if ((jumpDude.getSensorName().equals(fd2) && jumpDude != bd1) ||
				(jumpDude.getSensorName().equals(fd1) && jumpDude != bd2)) {
				jumpDude.setGrounded(true);
				//System.out.println("jump dude hit ground");
				sensorFixtures.add(jumpDude == bd1 ? fix2 : fix1); // Could have more than one ground
			}

			//See if side dude has landed on the ground.
			if ((sideDude.getSensorName().equals(fd2) && sideDude != bd1) ||
					(sideDude.getSensorName().equals(fd1) && sideDude != bd2)) {
				sideDude.setGrounded(true);
				sensorFixtures2.add(sideDude == bd1 ? fix2 : fix1); // Could have more than one ground
			}

			// Check button activation
			if ((bd2 == button2 && bd1 == jumpDude) ||
					(bd1 == button2 && bd2 == jumpDude)) {
				button2.setContact(true);
			}

			if ((bd2 == button1 && bd1 == sideDude) ||
					(bd1 == button1 && bd2 == sideDude)) {
				button1.setContact(true);
			}
			
			// Check for win condition
			if ((bd1 == jumpDude && bd2 == goalDoor) ||
				(bd1 == goalDoor && bd2 == jumpDude)) {
				setComplete(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when two objects cease to touch.  The main use of this method
	 * is to determine when the characer is NOT on the ground.  This is how we prevent
	 * double jumping.
	 */ 
	public void endContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();
		
		Object bd1 = body1.getUserData();
		Object bd2 = body2.getUserData();

		if ((jumpDude.getSensorName().equals(fd2) && jumpDude != bd1) ||
			(jumpDude.getSensorName().equals(fd1) && jumpDude != bd2)) {
			sensorFixtures.remove(jumpDude == bd1 ? fix2 : fix1);
			if (sensorFixtures.size == 0) {
				//System.out.println("jump dude left ground");
				jumpDude.setGrounded(false);
			}
		}

		if ((sideDude.getSensorName().equals(fd2) && sideDude != bd1) ||
				(sideDude.getSensorName().equals(fd1) && sideDude != bd2)) {
			sensorFixtures2.remove(sideDude == bd1 ? fix2 : fix1);
			if (sensorFixtures2.size == 0) {
				sideDude.setGrounded(false);
			}
		}

		// Check button leaving
		if ((bd2 == button2 && bd1 == jumpDude) ||
				(bd1 == button2 && bd2 == jumpDude)) {
			//System.out.println("not touching button");
			button2.setContact(false);
		}

		if ((bd2 == button1 && bd1 == sideDude) ||
				(bd1 == button1 && bd2 == sideDude)) {
			button1.setContact(false);
		}
	}
	
	/** Unused ContactListener method */
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	/** Unused ContactListener method */
	public void preSolve(Contact contact, Manifold oldManifold) {}

	/**
	 * Called when the Screen is paused.
	 *
	 * We need this method to stop all sounds when we pause.
	 * Pausing happens when we switch game modes.
	 */
	public void pause() {
		jumpSound.stop(jumpId);
		plopSound.stop(plopId);
		fireSound.stop(fireId);
	}
}