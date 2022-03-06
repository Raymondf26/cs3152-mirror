package edu.cornell.gdiac.molechelindescent.falling;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Queue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.molechelindescent.*;
import edu.cornell.gdiac.molechelindescent.obstacle.*;

public class FallingController extends WorldController implements ContactListener{
        /** Texture assets for the rocket */
        private TextureRegion rocketTexture;
        /** Texture filmstrip for the main afterburner */
        private FilmStrip mainTexture;
        /** Texture filmstrip for the main afterburner */
        private FilmStrip leftTexture;
        /** Texture filmstrip for the main afterburner */
        private FilmStrip rghtTexture;

        /** The sounds for collisions */
        private Sound bumpSound;
        /** The ids of the active collisions sounds */
        private final Queue<Long> bumpIds;
        private int maxBumps;
        /** Volume for bumping */
        private float bumpVol;
        /** Threshold for generating sound on collision */
        private float bumpThresh;
        /** Snake's y level initialized to height above player **/
        float beginningSnakePos = 30f;
        /** Snake's y level initialized to height above player **/
        float snakePos = beginningSnakePos;
        /** Snake should move every certain number of updates, should be adjusted */
        int nextSwitch;
        /** Current amount to decrease snake height by **/
        float decreaseBy = 0.5f;
        /** How long before snake starts to speed up **/
        int levelDifficulty = 10000;
        // could make this change based on level but for now just hard coded to 2000

    /** The sound for the main afterburner */
        private Sound burnSound;
        /** The sound for the left afterburner */
        private Sound leftSound;
        /** The sound for the right afterburner */
        private Sound rghtSound;
        /** Volume for afterburners */
        private float burnVol;

        /** Offset for the inventory message on the screen */
        private static final float DISPLAY_OFFSET   = 25.0f;

        /** Texture assets for the crates */
        private TextureRegion[] crateTextures;

        // Physics objects for the game
        /** Physics constants for initialization */
        private JsonValue constants;
        /** Reference to the goalDoor (for collision detection) */
        private BoxObstacle goalDoor;
        /** Reference to the rocket/player avatar */
        private FallingModel rocket;
        /** Ingredient texture */
        private TextureRegion ingredientTexture;
        /** Obstacle texture */
        private TextureRegion obstacleTexture;

    /**
         * Creates and initialize a new instance of the rocket lander game
         *
         * The game has default gravity and other settings
         */
        public FallingController() {
            bumpIds = new Queue<Long>();
            setDebug(false);
            setComplete(false);
            setFailure(false);
            world.setContactListener(this);
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
            constants  = directory.getEntry( "rocket:constants", JsonValue.class );
            crateTextures = new TextureRegion[constants.get("crates").getInt("textures",0)];
            for (int ii = 0; ii < crateTextures.length; ii++) {
                crateTextures[ii] = new TextureRegion( directory.getEntry( "rocket:crate0" + (ii + 1), Texture.class ) );
            }
            rocketTexture = new TextureRegion( directory.getEntry( "rocket:rocket", Texture.class ) );
            mainTexture  = directory.getEntry( "rocket:main.fire", FilmStrip.class );
            leftTexture  = directory.getEntry( "rocket:left.fire", FilmStrip.class );
            rghtTexture  = directory.getEntry( "rocket:right.fire", FilmStrip.class );

            bumpSound  = directory.getEntry( "rocket:bump", Sound.class );
            burnSound  = directory.getEntry( "rocket:mainburn", Sound.class );
            leftSound  = directory.getEntry( "rocket:leftburn", Sound.class );
            rghtSound = directory.getEntry( "rocket:rightburn", Sound.class );
            ingredientTexture = new TextureRegion (directory.getEntry("ragdoll:head", Texture.class));
            obstacleTexture = new TextureRegion(directory.getEntry("rocket:crate01", Texture.class));
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
            snakePos = beginningSnakePos;
        }

        /**
         * Lays out the game geography.
         */
        private void populateLevel() {
            // Add level goal
            float dwidth  = goalTile.getRegionWidth()/scale.x;
            float dheight = goalTile.getRegionHeight()/scale.y;

           /* JsonValue goal = constants.get("goal");
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
            addObject(goalDoor);*/

            // Create ground pieces
            PolygonObstacle obj;
            JsonValue walljv = constants.get("walls");
            JsonValue defaults = constants.get("defaults");
          /*obj = new PolygonObstacle(walljv.get(0).asFloatArray(), 0, 0);
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(defaults.getFloat( "density", 0 ));
            obj.setFriction(defaults.getFloat( "friction", 0 ));
            obj.setRestitution(defaults.getFloat( "restitution", 0 ));
            obj.setDrawScale(scale);
            obj.setTexture(earthTile);
            obj.setName("wall1");
            addObject(obj);*/

            /*obj = new PolygonObstacle(walljv.get(1).asFloatArray(), 0, 0);
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(defaults.getFloat( "density", 0 ));
            obj.setFriction(defaults.getFloat( "friction", 0 ));
            obj.setRestitution(defaults.getFloat( "restitution", 0 ));
            obj.setDrawScale(scale);
            obj.setTexture(earthTile);
            obj.setName("wall2");
            addObject(obj);*/

            /*obj = new PolygonObstacle(walljv.get(2).asFloatArray(), 0, 0);
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(defaults.getFloat( "density", 0 ));
            obj.setFriction(defaults.getFloat( "friction", 0 ));
            obj.setRestitution(defaults.getFloat( "restitution", 0 ));
            obj.setDrawScale(scale);
            obj.setTexture(earthTile);
            obj.setName("wall3");
            addObject(obj);*/

            // Create the pile of boxes
            /*JsonValue boxjv = constants.get("boxes");
            JsonValue crates = constants.get("crates");
            for (int ii = 0; ii < boxjv.size; ii += 2) {
                int id = RandomController.rollInt(0,crateTextures.length-1);
                TextureRegion texture = crateTextures[id];
                dwidth  = texture.getRegionWidth()/scale.x;
                dheight = texture.getRegionHeight()/scale.y;
                BoxObstacle box = new BoxObstacle(boxjv.getFloat(ii), boxjv.getFloat(ii+1), dwidth, dheight);
                obj.setDensity(crates.getFloat( "density", 0 ));
                obj.setFriction(crates.getFloat( "friction", 0 ));
                obj.setRestitution(defaults.getFloat( "restitution", 0 ));
                box.setName("crate"+id);
                box.setDrawScale(scale);
                box.setTexture(texture);
                addObject(box);
            }*/

            //Add ingredients
            JsonValue ingredientJV = constants.get("ingredients");
            MapIngredient ingredient;
            for (int i = 0; i < ingredientJV.size; i++) {
                String textureName = ingredientJV.get(i).getString("texture");
                float x = ingredientJV.get(i).get("pos").getFloat(0);
                float y = ingredientJV.get(i).get("pos").getFloat(1);
                Array<String> drops = new Array<>();
                for (int j = 0; j < ingredientJV.get(i).get("drops").size; j++) {
                    drops.add(ingredientJV.get(i).get("drops").getString(j));
                }
                dwidth  = ingredientTexture.getRegionWidth()/scale.x;
                dheight = ingredientTexture.getRegionHeight()/scale.y;
                ingredient = new MapIngredient(x, y, dwidth, ingredientTexture, scale, "testIngredient"+i);
                ingredient.setDrops(drops);
                addObject(ingredient);
            }

            // Add obstacles
            JsonValue obstacleJV = constants.get("obstacles");
            MapObstacle obstacle;
            for (int i = 0; i < obstacleJV.size; i++) {
                float x = obstacleJV.get(i).get("pos").getFloat(0);
                float y = obstacleJV.get(i).get("pos").getFloat(1);
                float[] points = new float[obstacleJV.get(i).get("points").size];
                for (int j = 0; j < obstacleJV.get(i).get("points").size; j++) {
                    points[j] = Integer.parseInt(obstacleJV.get(i).get("points").getString(j));
                }
                obstacle = new MapObstacle(points, x, y, earthTile, scale);
                addObject(obstacle);
            }

            // Create the rocket avatar
            dwidth  = rocketTexture.getRegionWidth()/scale.x;
            dheight = rocketTexture.getRegionHeight()/scale.y;
            JsonValue rockjv = constants.get("rocket");
            rocket = new FallingModel(rockjv, dwidth, dheight);
            rocket.setDrawScale(scale);
            rocket.setTexture(rocketTexture);
            rocket.setBurnerStrip(FallingModel.Burner.MAIN,  mainTexture);
            rocket.setBurnerStrip(FallingModel.Burner.LEFT,  leftTexture);
            rocket.setBurnerStrip(FallingModel.Burner.RIGHT, rghtTexture);

            // Add the sound names
            rocket.setBurnerSound(FallingModel.Burner.MAIN,  burnSound);
            rocket.setBurnerSound(FallingModel.Burner.LEFT,  leftSound);
            rocket.setBurnerSound(FallingModel.Burner.RIGHT, rghtSound);
            addObject(rocket);

            burnVol = defaults.getFloat("volume", 1);
            bumpVol = constants.get("collisions").getFloat( "volume", 1 );;
            maxBumps = constants.get("collisions").getInt( "maximum", 1 );;
            bumpThresh = constants.get("collisions").getFloat( "threshold", 0 );
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

            if (snakePos < rocket.getY()) {
                canvas.end();
            }
            //#region INSERT CODE HERE
            // Read from the input and add the force to the rocket model
            // Then apply the force using the method you modified in RocketObject

            InputController input = InputController.getInstance();
            float xforce = rocket.getThrust() * input.getHorizontal();
            float yforce = rocket.getThrust() * input.getVertical() * 2.5f;
            rocket.setFX(xforce);
            rocket.setFY(yforce);
            rocket.applyForce();

            //#endregion
            // snake should be decreasing its height by some amount and
            // then decreasing by more as the level keeps going
            nextSwitch += 1;
            if (nextSwitch % 10 == 0) {
                snakePos -= decreaseBy;
                nextSwitch = 0;
            }
            if (snakePos % levelDifficulty == 0) {
                decreaseBy = decreaseBy * 1.1f;
            }

            // Animate the three burners
            updateBurner(FallingModel.Burner.MAIN, rocket.getFY() > 1);
            updateBurner(FallingModel.Burner.LEFT, rocket.getFX() > 1);
            updateBurner(FallingModel.Burner.RIGHT, rocket.getFX() < -1);
        }

        /**
         * Updates that animation for a single burner
         *
         * This method is here instead of the the rocket model because of our philosophy
         * that models should always be lightweight.  Animation includes sounds and other
         * assets that we do not want to process in the model
         *
         * @param  burner   The rocket burner to animate
         * @param  on       Whether to turn the animation on or off
         */
        private void updateBurner(FallingModel.Burner burner, boolean on) {
          /*  Sound sound = rocket.getBurnerSound(burner);
            long soundId = rocket.getBurnerId(burner);
            if (on) {
                rocket.animateBurner(burner, true);
                if (rocket.getBurnerId( burner ) == -1) {
                    soundId = sound.loop(burnVol);
                    rocket.setBurnerId( burner, soundId );
                }
            } else {
                rocket.animateBurner(burner, false);
                rocket.setBurnerId( burner, -1 );
                sound.stop(soundId);
            } */
        }

        /// CONTACT LISTENER METHODS
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
            Body body1 = contact.getFixtureA().getBody();
            Body body2 = contact.getFixtureB().getBody();

            if( (body1.getUserData() == rocket   && body2.getUserData() == goalDoor) ||
                    (body1.getUserData() == goalDoor && body2.getUserData() == rocket)) {
                setComplete(true);
            }

            // Detect and resolve player-ingredient collision
            if( (body1.getUserData() == rocket   && body2.getUserData() instanceof MapIngredient) ||
                    (body1.getUserData() instanceof MapIngredient && body2.getUserData() == rocket)) {
                Array<String> inventory = rocket.getInventory();
                MapIngredient ingredient = body1.getUserData() instanceof MapIngredient ?
                        (MapIngredient)(body1.getUserData()) : (MapIngredient)(body2.getUserData());
                Array<String> drops = ingredient.getDrops();
                inventory.addAll(drops);
                rocket.setInventory(inventory);
                ingredient.markRemoved(true);
//                System.out.println(rocket.getInventory());

            }

            if( (body1.getUserData() == rocket && body2.getUserData() instanceof MapObstacle) ||
                    (body1.getUserData() instanceof MapObstacle && body2.getUserData() == rocket)) {
                rocket.updateMultiplier(0.25f);
            }

        }

        /**
         * Callback method for the start of a collision
         *
         * This method is called when two objects cease to touch.  We do not use it.
         */
        public void endContact(Contact contact) {
            rocket.updateMultiplier(1f);
        }

        private final Vector2 cache = new Vector2();

        /** Unused ContactListener method */
        public void postSolve(Contact contact, ContactImpulse impulse) {}

        /**
         * Handles any modifications necessary before collision resolution
         *
         * This method is called just before Box2D resolves a collision.  We use this method
         * to implement sound on contact, using the algorithms outlined similar to those in
         * Ian Parberry's "Introduction to Game Physics with Box2D".
         *
         * However, we cannot use the proper algorithms, because LibGDX does not implement
         * b2GetPointStates from Box2D.  The danger with our approximation is that we may
         * get a collision over multiple frames (instead of detecting the first frame), and
         * so play a sound repeatedly.  Fortunately, the cooldown hack in SoundController
         * prevents this from happening.
         *
         * @param  contact  	The two bodies that collided
         * @param  oldManifold  The collision manifold before contact
         */
        public void preSolve(Contact contact, Manifold oldManifold) {
            float speed = 0;

            // Use Ian Parberry's method to compute a speed threshold
            Body body1 = contact.getFixtureA().getBody();
            Body body2 = contact.getFixtureB().getBody();
            WorldManifold worldManifold = contact.getWorldManifold();
            Vector2 wp = worldManifold.getPoints()[0];
            cache.set(body1.getLinearVelocityFromWorldPoint(wp));
            cache.sub(body2.getLinearVelocityFromWorldPoint(wp));
            speed = cache.dot(worldManifold.getNormal());

            // Play a sound if above threshold (otherwise too many sounds)
            if (speed > bumpThresh) {
                String s1 = ((Obstacle)body1.getUserData()).getName();
                String s2 = ((Obstacle)body2.getUserData()).getName();
                if (s1.equals("rocket") || s1.startsWith("crate")) {
                    playBump();
                }
                if (s2.equals("rocket") || s2.startsWith("crate")) {
                    playBump();
                }
            }
        }

        /**
         * Plays a bump sound ensuring we never exceed MAX_BUMPS.
         *
         * The longest playing sound is evicted if necessary. To see why this
         * is needed, make MAX_BUMPS large and watch it blow your speakers.
         */
        public void playBump() {
            if (bumpIds.size >= maxBumps) {
                long oldest = bumpIds.removeFirst();
                bumpSound.stop(oldest);
            }
            long newest = bumpSound.play(bumpVol);
            bumpIds.addLast( newest );
        }

        /**
         * Called when the Screen is paused.
         *
         * We need this method to stop all sounds when we pause.
         * Pausing happens when we switch game modes.
         */
        public void pause() {
            for (long id : bumpIds) {
                bumpSound.stop(id);
            }
            // THIS IS CRITICAL. THE AFTERBURNER IS ON A LOOP.
            // IF YOU DO NOT STOP IT THE AFTERBURNER WILL PLAY FOREVER!
            Sound sound;
            long sid;
            sound = rocket.getBurnerSound(FallingModel.Burner.MAIN);
            sid = rocket.getBurnerId(FallingModel.Burner.MAIN);
            if (sid != -1) {
                sound.stop(sid);
            }
            sound = rocket.getBurnerSound(FallingModel.Burner.LEFT);
            sid = rocket.getBurnerId(FallingModel.Burner.LEFT);
            if (sid != -1) {
                sound.stop(sid);
            }
            sound = rocket.getBurnerSound(FallingModel.Burner.RIGHT);
            sid = rocket.getBurnerId(FallingModel.Burner.RIGHT);
            if (sid != -1) {
                sound.stop(sid);
            }
        }

        @Override
    public void draw(float dt) {
        canvas.clear();
        // placeholder so the magic number doesn't need to be replaced
        float factor = 32;

        canvas.begin();
        // Output the player inventory to the screen
        String message = "Current inventory: " + rocket.getInventory().toString();
        canvas.drawText(message, displayFont, DISPLAY_OFFSET, rocket.getY() * factor + 150.0f);

        int rocketYLocation = (int) rocket.getY();
        int snakeDist = Math.abs((int) snakePos - rocketYLocation); // this is complicated because rocket starts in positive and goes to negative

        canvas.drawText("Snake is " +  snakeDist + " units behind you!", displayFont, DISPLAY_OFFSET, rocket.getY()*factor + 250);
        canvas.drawText("SNAKE", displayFont, canvas.getWidth()/2, snakePos*factor);

        canvas.setCameraPosY(rocket.getY() * factor); //magic number 32 rn. Should change to soft-code.
        //System.out.println(rocket.getY());
        for(Obstacle obj : objects) {
            obj.draw(canvas);
        }
        canvas.end();
    }
}
