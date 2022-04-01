package edu.cornell.gdiac.molechelinmadness.model.event;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.GameObject;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

/**
 * An event represents information that a interactee would need to know what it should do to itself.
 * For example, it may contain the degree to which a door should rotate.
 * This event interface is the object that we're using as our extra information payload for
 * the Telegram/Telegraph interface system provided for us by LibGDX AI (it was originally
 * designed for AI but its event-driven system was easy to use in our situation too).
 * Specifically, in the Telegram object, it is the "extraInfo" object attached.
 * For the int "message" attached to the Telegram object, positive number represents the "on"
 * event. As in, swing the door open. A negative number represents the "off" event, as in, swing
 * the door closed. The "handleMessage" method that each GameObject implements will need this
 * information.
 */
public interface Event {

    /**
     * Initialize this event via the subtree in the json.
     *
     * @param json the subtree pertaining to this event
     */
    public void initialize(JsonValue json);

}
