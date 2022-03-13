package edu.cornell.gdiac.molechelindescent.idle;

public class AISideController {
    private float horizontal;
    private float timePassed;
    private boolean goingRight;

    public AISideController () {
        horizontal = 1;
        timePassed = 0;
        goingRight = true;
    }

    public float getHorizontal () {return horizontal;}

    public void update(float dt) {
        if (timePassed < 0.6) {
            timePassed += dt;
            if (horizontal == 0) {
                if (goingRight) {
                    horizontal = 1;
                }
                else {
                    horizontal = -1;
                }
            }
        }
        else {
            horizontal = 0;
            goingRight = !goingRight;
            timePassed = 0;
        }
    }

}
