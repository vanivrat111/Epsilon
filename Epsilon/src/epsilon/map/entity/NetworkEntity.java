package epsilon.map.entity;

import epsilon.map.Map;
import epsilon.net.NetworkHandler;

/**
 * 
 *
 * @author Marius
 */
public class NetworkEntity extends PlayerEntity {

    /**
     * Sets the name and position variables of the entity
     *
     * @param posX the X-axis position of the entity
     * @param posY the Y-axis position of the entity
     * @param playerName the name of the entity
     */
    public NetworkEntity(double posX, double posY, String playerName, Map m) {

        super(posX, posY, playerName, new String[]{"/pics/sheep_enemy.png"}, m);

    }

    @Override
    public void calculateMovement() {

        newPosX = posX;
        newPosY = posY;

        double[] d = NetworkHandler.getInstance().getPlayerStateByName(super.getName());
        newPosX = d[0];
        newPosY = d[1];

        if (newPosX > posX) {
            if (currentSprite != rightSprite) {
                currentSprite.resetImage();
                currentSprite = rightSprite;
                rightSprite.resetImage();
                ticker = 0;
                facingRight = true;
            }
        } else if (newPosX < posX) {
            if (currentSprite != leftSprite) {
                currentSprite.resetImage();
                currentSprite = leftSprite;
                leftSprite.resetImage();
                ticker = 0;
                facingRight = false;
            }
        } else {
            if (currentSprite != standSpriteRight && currentSprite != standSpriteLeft) {
                currentSprite.resetImage();
                // if the guy was moving right, he should be face right when stopped
                if (currentSprite == rightSprite) {
                    currentSprite = standSpriteRight;
                    standSpriteRight.resetImage();
                    facingRight = true;
                } else { // last moved left, animation should be inverted
                    currentSprite = standSpriteLeft;
                    standSpriteLeft.resetImage();
                    facingRight = false;
                }
                ticker = 0;
            }
        }

        // go to the next picture in the sprite if it is time
        if (ticker < 5) {
            ticker++;
        } else {
            ticker = 0;
            currentSprite.nextImage();
        }
    }
}
