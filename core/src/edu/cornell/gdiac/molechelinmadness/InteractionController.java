package edu.cornell.gdiac.molechelinmadness;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.molechelinmadness.model.*;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

/**
 * Essentially a collision controller.
 */
public class InteractionController implements ContactListener{

    /**
     * Called when two fixtures begin to touch.
     *
     * @param contact
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


            //Check for and handle ground collision to reset mole jumps
            if ("feet".equals(fd1)) {
                Mole currMole = (Mole) bd1;

                //Handle jump resets
                currMole.setCanJump(true);
                currMole.addSensorFixtures(fix2);


                //Handle everything else
                if (bd2.getType() == 1 || bd2.getType() == 2) {
                    bd2.contactBegan(currMole);
                }

            } else if ("feet".equals(fd2)){
                Mole currMole = (Mole) bd2;

                //Handle jump resets
                currMole.setCanJump(true);
                currMole.addSensorFixtures(fix1);


                //Handle everything else
                if (bd1.getType() == 1 || bd1.getType() == 2) {
                    bd1.contactBegan(currMole);
                }

            }  else if ("hands".equals(fd1)) {
                assert(bd1 instanceof Mole);
                Mole currMole = (Mole)(bd1);

                if (bd2.getType() == 0 || bd2.getType() == 2) {
                    bd2.contactBegan(currMole);
                }

            } else if ("hands".equals(fd2)) {
                assert(bd2 instanceof Mole);
                Mole currMole = (Mole)(bd2);

                if (bd1.getType() == 0 || bd1.getType() == 2) {
                    bd1.contactBegan(currMole);
                }

            }

            // Check for and handle mole-ingredient chute collision
            if ((bd1 instanceof Mole && bd2 instanceof IngredientChute) || (bd1 instanceof IngredientChute && bd2 instanceof  Mole)) {
                Mole mole = bd1 instanceof Mole ? (Mole) bd1 : (Mole) bd2;
                IngredientChute ingredientChute = bd1 instanceof IngredientChute ? (IngredientChute) bd1 : (IngredientChute) bd2;
                Ingredient ingredient = mole.drop(); // Drop ingredient in inventory so we can send it up
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Called when two fixtures cease to touch.
     *
     * @param contact
     */
    public void endContact(Contact contact) {

        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        Object fd1 = fix1.getUserData();
        Object fd2 = fix2.getUserData();

        try {
            Obstacle bd1 = (Obstacle)body1.getUserData();
            Obstacle bd2 = (Obstacle)body2.getUserData();

            //Handle ground collision
            if ("feet".equals(fd1)) {
                Mole currMole = (Mole) bd1;

                //Handle jump resets
                currMole.removeSensorFixtures(fix2);
                if (currMole.countFixtures() == 0) {
                    currMole.setCanJump(false);
                }

                //Handle everything else
                if (bd2.getType() == 1 || bd2.getType() == 2) {
                    bd2.contactEnded(currMole);
                }

            } else if ("feet".equals(fd2)) {
                Mole currMole = (Mole) bd2;

                //Handle jump resets
                currMole.removeSensorFixtures(fix1);
                if (currMole.countFixtures() == 0) {
                    currMole.setCanJump(false);
                }

                //Handle everything else
                if (bd1.getType() == 1 || bd1.getType() == 2) {
                    bd1.contactEnded(currMole);
                }

            } else if ("hands".equals(fd1)) {
                Mole currMole = (Mole) bd1;

                //Handle everything
                if (bd2.getType() == 0 || bd2.getType() == 2) {
                    bd2.contactEnded(currMole);
                }

            } else if ("hands".equals(fd2)) {
                Mole currMole = (Mole) bd2;

                //Handle everything
                if (bd1.getType() == 0 || bd1.getType() == 2) {
                    bd1.contactEnded(currMole);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /** Lab 4 did not use this. Idk what it does. */
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    /** Lab 4 did not use this. Idk what it does. */
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }


}
