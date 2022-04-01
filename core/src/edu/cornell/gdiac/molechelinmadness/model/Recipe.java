package edu.cornell.gdiac.molechelinmadness.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.molechelinmadness.GameCanvas;
import edu.cornell.gdiac.molechelinmadness.model.obstacle.Obstacle;

public class Recipe{

    /** ingredient Array */
    private Array<Ingredient> ingredients;
    /** The font for giving messages to the player */
    protected BitmapFont displayFont;
    /** Recipe offset*/
    protected int displayLength;
    /** Recipe offset*/
    protected int bottomLeft;
    /** Ingredient offset*/
    protected int ingOffset;
    /** Center X */
    protected int cX;
    /** Center X */
    protected int cY;
    /** text length */
    protected int tL;
    /** knife texture*/
    protected TextureRegion knife;
    /** conversion for physics units to pixels*/
    protected int scale;

    public Recipe(Array<Ingredient> ings, int x, int y) {
        ingredients = ings;
        cX = x;
        cY = y;
        tL = 110;
        scale = 40;
        if (ingredients!=null){
            displayLength = ingredients.size*(ingredients.first().getTexture().getRegionWidth());
            displayLength += (ingredients.size-1)*30;
            displayLength += tL;
            bottomLeft = cX - (displayLength/2);
            ingOffset = ingredients.first().getTexture().getRegionWidth()+30;
            setRecipeIngredients(ingredients);
        }
    }

    public void draw(GameCanvas canvas) {
        //canvas.drawTextCentered("Recipe!", displayFont, 340.0f);
        displayFont.setColor(Color.BLACK);
        canvas.drawText("Recipe:", displayFont, bottomLeft, cY);
        for (Ingredient i: ingredients){
            i.draw(canvas);
            if (i.chopped){
                canvas.draw(knife, i.getX()*scale, (float)(i.getY()-0.75)*scale);
                System.out.print("drawing knife");
            }
        }
    }

    public void setFont(BitmapFont font){
        displayFont = font;
    }
    public void setKnife(TextureRegion k){
        knife = k;
    }

    public void setRecipeIngredients(Array<Ingredient> ings){
        for (int i=0; i < ings.size; i++ ){
            float iX = bottomLeft + tL + i*ingOffset;
            ings.get(i).setPosition( iX/scale, (float) cY/scale);
        }
    }

}
