package edu.cornell.gdiac.molechelinmadness;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.molechelinmadness.model.*;
import edu.cornell.gdiac.molechelinmadness.model.interactor.Interactor;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public class InteractionController implements ContactListener {

    /** Reference to the level */
    private Level level;

    /** Mark set to handle more sophisticated collision callbacks */
    protected ObjectSet<Fixture>[] sensorFixturesList; //For now, represented as an array. Can change later.


    public InteractionController() {

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
            Mole[] moles = level.getMoles();
            for (int i = 0; i < moles.length; i++) {
                Mole mole = moles[i];
                //Do the same check as in lab 4 for ground collision to reset jump
            }

            //Check for and handle mole-ingredient collision
            if ((bd1 instanceof Mole && bd2 instanceof Ingredient) || (bd1 instanceof Ingredient && bd2 instanceof  Mole)) {
                //logic
            }


            //Check for and handle mole-cooking-station collision
            if ((bd1 instanceof Mole && bd2 instanceof CookingStation) || (bd1 instanceof CookingStation && bd2 instanceof  Mole)) {
                //logic
            }

            //Check for and handle mole-final-cooking-station collision
            if ((bd1 instanceof Mole && bd2 instanceof FinalStation) || (bd1 instanceof FinalStation && bd2 instanceof  Mole)) {
                //logic
            }

            //Check for and handle mole-interactor collision
            if ((bd1 instanceof Mole && bd2 instanceof Interactor) || (bd1 instanceof Interactor && bd2 instanceof  Mole)) {
                //logic
            }

            //Check for and handle mole-dumbwaiter collision
            if ((bd1 instanceof Mole && bd2 instanceof Dumbwaiter) || (bd1 instanceof Dumbwaiter && bd2 instanceof  Mole)) {
                Mole mole = bd1 instanceof Mole ? (Mole) bd1 : (Mole) bd2;
                Dumbwaiter dumbwaiter = bd1 instanceof Dumbwaiter ? (Dumbwaiter) bd1 : (Dumbwaiter) bd2;

                //logic w/ mole
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
        Mole[] moles = level.getMoles();
        for (int i = 0; i < moles.length; i++) {
            Mole mole = moles[i];
            //Do the same check as in lab 4 for ground collision to reset jump
        }


        //Handle cooking station collision
        if ((bd1 instanceof Mole && bd2 instanceof CookingStation) || (bd1 instanceof CookingStation && bd2 instanceof  Mole)) {
            //logic
        }


        //Handle interactor collision
        if ((bd1 instanceof Mole && bd2 instanceof Interactor) || (bd1 instanceof Interactor && bd2 instanceof  Mole)) {
            //logic
        }


    }

    /** Lab 4 did not use this. Idk what it does. */
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    /** Lab 4 did not use this. Idk what it does. */
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
