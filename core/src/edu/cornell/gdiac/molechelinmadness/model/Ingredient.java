package edu.cornell.gdiac.molechelinmadness.model;

import edu.cornell.gdiac.molechelinmadness.model.obstacle.BoxObstacle;

public class Ingredient extends BoxObstacle {

    public enum IngType {
        CARROT,
        TURNIP,
        RADISH
    }
    //Type of ingredient
    public IngType type;


    //Gets the ingredients type
    public IngType getIngredientType(){
        return this.type;
    }


    //Sets the ingredients type
    public void setIngType(IngType ing){
        this.type = ing;
    }

    /**
     * Creates a new box at the origin.
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public Ingredient(float x, float y, float width, float height, IngType type) {
        super(x, y, width, height);
        this.type = type;

    }
}
