package edu.cornell.gdiac.molechelinmadness;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.*;
import edu.cornell.gdiac.molechelinmadness.model.event.Event;
import edu.cornell.gdiac.molechelinmadness.model.interactor.Button;
import edu.cornell.gdiac.molechelinmadness.model.interactor.Interactor;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.WheelObstacle;


public class LeftClickController {
    private Object selected;

    private TextureRegion whitebox;
    public LeftClickController(){
        selected = null;
    }

    public Object getSelected(){
        return selected;
    }

    public void setSelected(Obstacle o){
        selected = o;
    }

    public String format(String text){
        StringBuilder sb = new StringBuilder(text);

        int i = 0;
        while (i + 30 < sb.length() && (i = sb.lastIndexOf(" ", i + 30)) != -1) {
            sb.replace(i, i + 1, "\n");
        }
        return sb.toString();
    }

    public String printType(){
        if(selected instanceof CookingStation){
            return "This is a cooking station. Place ingredients in here to make complete the recipe or modify ingredients. Be careful not to get burnt.";
        }
        else if(selected instanceof Ingredient){
            return "This is an ingredient. Bring these to cooking stations in order to cook them. Don't ask why they're on the floor.";
        }
        else if(selected instanceof Dumbwaiter.DumbHead){
            return "This is a dumb waiter. Its smarter than it sounds";
        }
        else if(selected instanceof Mole){
            return "This is a mole chef. Each chef has an idle action they will perform when they are not being micromanaged. Press space to switch moles. Some work harder than others.";
        }
        else if(selected instanceof Platform){
            return "This is a Platform.";
        }
        else if(selected instanceof Button.ButtonObstacle){
            return "Press F to interact.";
        }
        else if(selected instanceof IngredientChute.IngredientChuteObstacle){
            return "This is an ingredient shoot. Use this to transport ingredients to other areas. Likely not magic.";
        }
        else if(selected instanceof WheelObstacle){
            return "This is a rotating platform. Pushing the corresponding button will rotate the platform, allowing moles to pass through.";
        }
        else if(selected instanceof BoxObstacle){
            return "This is a rotating platform. Pushing the corresponding button will rotate the platform, allowing moles to pass through.";
        }

        else{
            return "Not working yet.";
        }
    }

    public void draw(GameCanvas canvas, BitmapFont displayFont, AssetDirectory directory){
        whitebox = new TextureRegion(directory.getEntry("whitebox", Texture .class));
        if(selected != null) {
            canvas.begin();
            canvas.draw(whitebox, 25, 450);
            canvas.drawText(this.format(this.printType()), displayFont, 30, 650);
            canvas.end();
        }
    }


}


