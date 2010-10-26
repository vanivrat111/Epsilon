package epsilon.map.entity;

import epsilon.game.Collision;
import epsilon.game.Sprite;
import java.awt.Graphics;

/**
 *
 * Abstract class that provides a template for all entities in the map
 *
 * @author Marius
 */
public abstract class Entity {

    protected double posX;
    protected double posY;

    // previous position of the entity. Used for movement and smoothing out rendering
    protected double pposX;
    protected double pposY;

    protected Sprite currentSprite;

    /*
     * Constructur for the Entity class
     *
     * @param posX The initial x position of the entity
     * @param posY The initial y position of the entity
     */
    public Entity (double posX,double posY) {
        this.posX = posX;
        this.posY = posY;
        this.pposX = posX;
        this.pposY = posY;
    }

    /*
     * Rendering the object
     *
     * @param g The graphic object the entity is to be drawn on
     * @param delta an int specifying the number of milliseconds since last gameupdate
     */
    public void render(Graphics g, int delta, double x, double y) {
        double coeff = (double)delta/16;

        double posX = this.posX - x;
        double posY = this.posY - y;
        double pposX = this.pposX - x;
        double pposY = this.pposY - y;

        /**
         * Smoothing out frame drawings
         */
        currentSprite.draw(g, (int)Math.ceil(posX+(posX-pposX)*coeff), (int)Math.ceil(posY+(posY-pposY)*coeff));
    }

    /**
     * Renders the hitbox of the entity, for debugging purposes
     *
     * @param g the graphics object to be drawn on
     * @param x x position of the hitbox
     * @param y y position of the hitbox
     */
    public abstract void renderHitBox(Graphics g, double x, double y);

    /**
     * Returns the X position of the entity
     *
     * @return X position of the entity
     */
    public double getXRenderPosition() {
        return getXPosition();
    }

    /**
     * Returns the Y position of the entity
     *
     * @return Y position of the entity
     */
    public double getYRenderPosition() {
        return getYPosition();
    }

    /**
     * Returns the X position of the entity
     *
     * @return X position of the entity
     */
    public double getXPosition() {
        return posX;
    }

    /**
     * Returns the Y position of the entity
     *
     * @return Y position of the entity
     */
    public double getYPosition() {
        return posY;
    }

    /**
     * Checking if an entity is within the hitbox of the entity.
     *
     * @param x
     * @param y
     * @return
     */
    public abstract Collision collision(Entity entity);

    /**
     * Returns the width of the Sprite
     *
     * @return width in pixels
     */
     public int getWidth() {
         return currentSprite.getWidth();
     }

     /**
      * Returns the height of the Sprite
      *
      * @return height in pixels
      */
     public int getHeight() {
         return currentSprite.getHeight();
     }

     public HitBox[] getHitbox() {
         return currentSprite.getHitBox();
     }
}