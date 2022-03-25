package edu.cornell.gdiac.molechelinmadness;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.molechelinmadness.model.*;
import edu.cornell.gdiac.molechelinmadness.model.interactor.Interactor;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public class InteractionController implements ContactListener{

    /** */
    private MessageDispatcher dispatcher;


    public InteractionController() {
        dispatcher = new MessageDispatcher();
    }

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


                //Handle interactives (including dumbwaiter, buttons, etc.)
                if (bd2 instanceof Interactive) {
                    Interactive interactive = (Interactive) bd2;
                    if (interactive.getType() == 1) {
                        interactive.resolveBegin(currMole);
                    }
                }

            } else if ("feet".equals(fd2)){
                Mole currMole = (Mole) bd2;

                //Handle jump resets
                currMole.setCanJump(true);
                currMole.addSensorFixtures(fix1);


                //Handle interactives (including dumbwaiter, buttons, etc.)
                if (bd1 instanceof Interactive) {
                    Interactive interactive = (Interactive) bd1;
                    if (interactive.getType() == 1) {
                        interactive.resolveBegin(currMole);
                    }
                }

            }  if ("hands".equals(fd1)){
                Mole currMole = (Mole)(bd1);

                //Handle interactives (including dumbwaiter, buttons, etc.)
                if (bd2 instanceof Interactive) {
                    Interactive interactive = (Interactive) bd2;
                    if (interactive.getType() == 0) {
                        interactive.resolveBegin(currMole);
                    }
                }

            } else if ("hands".equals(fd2)) {
                Mole currMole = (Mole)(bd2);

                //Handle interactives (including dumbwaiter, buttons, etc.)
                if (bd1 instanceof Interactive) {
                    Interactive interactive = (Interactive) bd1;
                    if (interactive.getType() == 0) {
                        interactive.resolveBegin(currMole);
                    }
                }

            }

            //Check for and handle mole-ingredient collision
            if ((bd1 instanceof Mole && bd2 instanceof Ingredient) || (bd1 instanceof Ingredient && bd2 instanceof  Mole)) {
                //logic
                Mole mole = bd1 instanceof Mole ? (Mole) bd1 : (Mole) bd2;
                Ingredient i = bd2 instanceof Ingredient ? (Ingredient) bd2 : (Ingredient) bd1;
                if(mole.getInventory() == null){
                    mole.addToInventory(i);
                    i.holdPos(100, 100);
                }

            }


            //Check for and handle mole-cooking-station collision
           /* if ((bd1 instanceof Mole && bd2 instanceof CookingStation) || (bd1 instanceof CookingStation && bd2 instanceof  Mole)) {
                //logic
                Mole mole = bd1 instanceof Mole ? (Mole) bd1 : (Mole) bd2;
                CookingStation c = bd2 instanceof CookingStation ? (CookingStation) bd2 : (CookingStation) bd1;
                c.Cook(mole);
            }*/

            //Check for and handle mole-final-cooking-station collision
            if ((bd1 instanceof Mole && bd2 instanceof FinalStation) || (bd1 instanceof FinalStation && bd2 instanceof  Mole)) {
                //logic
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

        Object bd1 = body1.getUserData();
        Object bd2 = body2.getUserData();

        //Handle ground collision
        if ("feet".equals(fd1)) {
            Mole currMole = (Mole) bd1;

            //Handle jump resets
            currMole.removeSensorFixtures(fix2);
            if (currMole.countFixtures() == 0) {
                currMole.setCanJump(false);
            }

            //Handle interactives (including dumbwaiter, buttons, etc.)
            if (bd2 instanceof Interactive) {
                Interactive interactive = (Interactive) bd2;
                if (interactive.getType() == 1) {
                    interactive.resolveEnd(currMole);
                }
            }



        } else if ("feet".equals(fd2)) {
            Mole currMole = (Mole) bd2;

            //Handle jump resets
            currMole.removeSensorFixtures(fix1);
            if (currMole.countFixtures() == 0) {
                currMole.setCanJump(false);
            }

            if (bd1 instanceof Interactive) {
                Interactive interactive = (Interactive) bd1;
                if (interactive.getType() == 1) {
                    interactive.resolveEnd(currMole);
                }
            }



        } else if ("hands".equals(fd1)) {
            Mole currMole = (Mole) bd1;

            //Handle interactives (including dumbwaiter, buttons, etc.)
            if (bd2 instanceof Interactive) {
                Interactive interactive = (Interactive) bd2;
                if (interactive.getType() == 0) {
                    interactive.resolveEnd(currMole);
                }
            }

        } else if ("hands".equals(fd2)) {
            Mole currMole = (Mole) bd2;

            if (bd1 instanceof Interactive) {
                Interactive interactive = (Interactive) bd1;
                if (interactive.getType() == 0) {
                    interactive.resolveEnd(currMole);
                }
            }
        }

    }

    /** Lab 4 did not use this. Idk what it does. */
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    /** Lab 4 did not use this. Idk what it does. */
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }


}
