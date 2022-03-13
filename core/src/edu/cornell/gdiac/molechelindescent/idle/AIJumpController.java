package edu.cornell.gdiac.molechelindescent.idle;

public class AIJumpController {

    private boolean jumped;
    private float cooldown;

    public AIJumpController() {
        jumped = false;
        cooldown = 2.5f;
    }

    public boolean didPrimary() {return jumped;}

    public void update(float dt) {
        if (cooldown > 0) {
            jumped = false;
            cooldown -= dt;
            return;
        }
        jumped = true;
        //System.out.println("jumped");
        cooldown = 2.5f;
    }

}
