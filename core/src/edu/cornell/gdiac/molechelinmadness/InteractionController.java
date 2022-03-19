package edu.cornell.gdiac.molechelinmadness;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.molechelinmadness.model.*;
import edu.cornell.gdiac.molechelinmadness.model.interactor.Interactor;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public class InteractionController{

    /** Reference to the level */
    private Level level;

    /** Mark set to handle more sophisticated collision callbacks */
    protected ObjectSet<Fixture>[] sensorFixturesList; //For now, represented as an array. Can change later.


    public InteractionController() {

    }


}
