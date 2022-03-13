package edu.cornell.gdiac.molechelindescent.idle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.molechelindescent.GameCanvas;
import edu.cornell.gdiac.molechelindescent.obstacle.BoxObstacle;
import edu.cornell.gdiac.molechelindescent.obstacle.PolygonObstacle;

public class TrapDoor extends PolygonObstacle {

    private boolean isTriggered;

    public TrapDoor (float[] points) {
        super (points, 0, 0);
        setBodyType(BodyDef.BodyType.StaticBody);
        isTriggered = false;
    }

    public void triggerOn () {
        if (isTriggered) {
            return;
        }
        else {
            isTriggered = true;
            setSensor(true);
        }
    }

    public void triggerOff () {
        if (isTriggered) {
            isTriggered = false;
            setSensor(false);
        }
        else {
            return;
        }
    }

    public void draw(GameCanvas canvas) {
        if (isTriggered) {
            return;
        }
        if (region != null) {
            canvas.draw(region, Color.WHITE,0,0,getX()*drawScale.x,getY()*drawScale.y,getAngle(),1,1);
        }
    }

}
