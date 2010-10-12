package epsilon.map.entity;

import epsilon.game.Physics;
import epsilon.game.Sprite;
import epsilon.game.Input;
import java.awt.Graphics;

/**
 * Test class that extends entity
 *
 * @author Marius
 */
public class TestEntity extends MoveableEntity {

    // keeps track of when to change pictures in the sprite
    private int ticker;

    // used for checking if the entity can jump
    private boolean touchesGround;

    // the different sprites this entity uses
    private Sprite rightSprite;
    private Sprite standSpriteRight;
    private Sprite leftSprite;
    private Sprite standSpriteLeft;

    /**
     * Constructor for the entity that initialises sprites
     *
     * @param posX The starting X position of the entity
     * @param posY The starting Y position of the entity
     */
    public TestEntity(int posX,int posY) {
        super(posX, posY);
        ticker = 0;
        touchesGround = false;

        rightSprite = new Sprite(new String[]{"/pics/guy01.png","/pics/guy02.png","/pics/guy03.png","/pics/guy04.png","/pics/guy05.png"});
        leftSprite = new Sprite(new String[]{"/pics/guy01.png","/pics/guy02.png","/pics/guy03.png","/pics/guy04.png","/pics/guy05.png"},true);
        standSpriteRight = new Sprite(new String[]{"/pics/guy01.png"});
        standSpriteLeft = new Sprite(new String[]{"/pics/guy01.png"},true);

        currentSprite = standSpriteRight;
    }

    @Override
    public void calculateMovement() {

        // handle input, and chose the right sprite for the job
        newPosX = posX;
        newPosY = posY;

        if(Input.get().right() && !Input.get().left()) {
            if (currentSprite != rightSprite) {
                currentSprite.resetImage();
                currentSprite = rightSprite;
                rightSprite.resetImage();
                ticker = 0;
            }
            newPosX = posX+4;
        } else if (Input.get().left() && !Input.get().right()) {
            if (currentSprite != leftSprite) {
                currentSprite.resetImage();
                currentSprite = leftSprite;
                leftSprite.resetImage();
                ticker = 0;
            }
            newPosX = posX-4;
        } else {
            if (currentSprite != standSpriteRight && currentSprite != standSpriteLeft) {
                currentSprite.resetImage();
                // if the guy was moving right, he should be face right when stopped
                if (pposX < posX) {
                    currentSprite = standSpriteRight;
                    standSpriteRight.resetImage();
                } else { // last moved left, animation should be inverted
                    currentSprite = standSpriteLeft;
                    standSpriteLeft.resetImage();
                }
                ticker = 0;
            }
        }

        // Handle falling
        if (posY<500 && !touchesGround) {
            double temp = Physics.calculateGravity(posY, pposY, 16);
            newPosY = posY-temp;
        } else if (Input.get().jump()) {
            // if it touches the ground, jump!
            newPosY -= 6;
        }

        // go to the next picture in the sprite if it is time
        if (ticker < 5) {
            ticker++;
        } else {
            ticker = 0;
            currentSprite.nextImage();
        }

        touchesGround = false;
    }

    @Override
    public double getXRenderPosition () {
        return posX - 400 + currentSprite.getWidth()/2;
    }

    @Override
    public double getYRenderPosition () {
        //return posY - 300 + currentSprite.getHeight()/2;
        return 0;
    }

    @Override
    public boolean[] collision(Entity entity) {
        return new boolean[]{false,false,false,false,false}; // yet to be implemented
    }

    @Override
    public void renderHitBox(Graphics g, double x, double y) {

        double posX = this.posX - x;
        double posY = this.posY - y;

        g.drawRect((int)posX, (int)posY, this.getWidth(), this.getHeight());
    }

    @Override
    public void collided(boolean[] hitbox, Entity collidedWith) {

        if (collidedWith instanceof World) {

            // overlap between the two entities in pixels
            double dlx = collidedWith.getXPosition() + collidedWith.getWidth() - newPosX;
            double drx = newPosX + getWidth() - collidedWith.getXPosition();
            double dty = newPosY + getHeight() - collidedWith.getYPosition();
            double dby = (collidedWith.getYPosition() + collidedWith.getHeight()) - newPosY ;

            // movement if this entity collides on the left side of something
            if (hitbox[1] && pposX < posX && dty > 2 && dby > 6) {
                newPosX = collidedWith.getXPosition() - getWidth();
                pposX = newPosX;
            }

            // movement if this entity collides on the right side of something
            if (hitbox[2] && pposX > posX && dty> 2 && dby > 6) {
                newPosX = collidedWith.getXPosition() + collidedWith.getWidth();
                pposX = newPosX;
            }

            // movement if it collides on the bottom of this entity
            if (hitbox[3] && posY > pposY && (drx > 8 && dlx > 8) ) {
                posY = collidedWith.getYPosition() - getHeight() + 1;
                newPosY = posY;
                pposY = posY;
                touchesGround = true;
            }

            // movement if it collides on the top of this entity
            if (hitbox[4] && posY < pposY && (drx > 8 && dlx > 8)) {
                pposY = collidedWith.posY + collidedWith.getHeight() - 1;
                newPosY = pposY;
                pposY = posY;
            }

        }
    }
}
