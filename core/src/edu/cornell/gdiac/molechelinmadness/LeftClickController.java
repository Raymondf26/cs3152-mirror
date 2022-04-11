package edu.cornell.gdiac.molechelinmadness;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.molechelinmadness.model.CookingStation;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public class LeftClickController {
    private Obstacle selected;

    private TextureRegion whitebox;
    public LeftClickController(){
        selected = null;
    }

    public Obstacle getSelected(){
        return selected;
    }

    public void setSelected(Obstacle o){
        selected = o;
    }

    public String printType(){
        if(selected instanceof CookingStation){
            return "this is a cooking station";
        }
        else{
            return "this is something we haven't \nwritten bout yet";
        }
    }

    public void draw(GameCanvas canvas, BitmapFont displayFont, AssetDirectory directory){
        whitebox = new TextureRegion(directory.getEntry("whitebox", Texture .class));
        if(selected != null) {
            canvas.begin();
            canvas.draw(whitebox, 25, 450);
            canvas.drawText(this.printType(), displayFont, 30, 650);
            canvas.end();
        }
    }


}


