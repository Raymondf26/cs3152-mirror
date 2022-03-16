package edu.cornell.gdiac.molechelinmadness;

import edu.cornell.gdiac.molechelinmadness.model.Mole;
import edu.cornell.gdiac.molechelinmadness.model.interactee.Door;
import edu.cornell.gdiac.molechelinmadness.model.interactee.Interactee;
import edu.cornell.gdiac.molechelinmadness.model.interactor.Button;
import edu.cornell.gdiac.molechelinmadness.model.interactor.Interactor;

public class InteractionController {

    public InteractionController() {

    }

    public void resolveCollision(Interactor interactor, Mole mole, boolean contact) {

        //contact refers to whether we detected "beginContact" or "endContact." true for the former, false for the latter

        switch(interactor.getType()) {
            case BUTTON:
                Button button = (Button) interactor;
                //Check if actually interacting with this object
                //If so, call:
                for (int i = 0; i < button.getLinks().length; i++) {
                    resolveEffect(button.getLinks()[i], mole);
                }

            default:
                break;
        }
    }

    private void resolveEffect(Interactor.Link link,Mole mole) {
        int effect = link.effect;
        Interactee interactee = link.interactee;
        switch(interactee.getType()) {
            case DOOR:
                Door door = (Door) interactee;

            default:
                break;
        }
    }

}
