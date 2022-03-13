package edu.cornell.gdiac.molechelindescent.idle;

import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.molechelindescent.obstacle.WheelObstacle;

public class Button extends WheelObstacle {

    private String sensorName;
    private boolean state;
    private boolean contact;

    public Button(float x, float y, float radius) {
        super(x, y, radius);
        setBodyType(BodyDef.BodyType.StaticBody);
        setSensor(true);
    }

    public void setSensorName(String name) {sensorName = name;}

    public String getSensorName() {return sensorName;}

    public void toggleOn () {
        if (state) {
            return;
        }
        else {
            state = true;
        }
    }

    public void toggelOff () {
        if (state) {
            state = false;
        }
        else {
            return;
        }
    }

    public boolean isOn() {return state;}

    public void setContact (boolean contact) {this.contact = contact;}

    public boolean inContact () {return contact;}

}
