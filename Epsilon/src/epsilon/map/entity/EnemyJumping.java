package epsilon.map.entity;

import epsilon.game.Collision;
import epsilon.game.Physics;
import epsilon.game.Sprite;
import epsilon.map.Map;
import epsilon.map.ShotStore;

/**
 * Artificial intelligence for a simple enemy. The enemy is jumping up and down
 * and only shoots if the player is infront of the enemy.
 *
 * @author vz
 */
public class EnemyJumping extends Enemy {

    private int startXpos;
    private boolean facingRight;
    private HitBox[] hitbox;
    private Sprite spriteFacingLeft;
    private Sprite spriteFacingRight;
    private boolean touchesGround;
    private boolean isDead = false;
    private int health = 4;
    private int speed = 0;
    private ShotStore shots;
    private int shotCooldown = 120;

    /**
     * EnemyPatrol with starting position, enemy walking 100 pixels to the left
     * and then 100 pixels to the right. Then repeat.
     *
     * @param posX enemy starting posX
     * @param posY enemy starting posY
     */
    public EnemyJumping(int posX, int posY, Map m) {
        super(posX, posY, m);
        HitBox[] hitbox = new HitBox[1];

        hitbox[0] = new HitBox(37, 28, 20, 63);



        spriteFacingLeft = new  Sprite(new String[]{"/pics/guy/red/guy01_red.png",
                                                    "/pics/guy/red/guy02_red.png",
                                                    "/pics/guy/red/guy03_red.png",
                                                    "/pics/guy/red/guy04_red.png",
                                                    "/pics/guy/red/guy05_red.png"},
                                                    true, hitbox);

        spriteFacingRight = new Sprite(new String[]{"/pics/guy/red/guy01_red.png",
                                                    "/pics/guy/red/guy02_red.png",
                                                    "/pics/guy/red/guy03_red.png",
                                                    "/pics/guy/red/guy04_red.png",
                                                    "/pics/guy/red/guy05_red.png"},
                                                     false, hitbox);
        currentSprite = spriteFacingRight;
        startXpos = posX;
        facingRight = true;

        shots = new ShotStore(mapReference);

    }

    @Override
    public void collided(Collision c) {
        if (c.collidedWith instanceof World || c.collidedWith instanceof NetworkEntity || c.collidedWith instanceof PlayerEntity) {

            // overlap between the two entities in pixels
            double dlx = c.deltaLeft;
            double drx = c.deltaRight;
            double dty = c.deltaTop;
            double dby = c.deltaBottom;

            // movement if this entity collides on the left side of something
            if (c.crossedLeft && pposX < posX && dty > 20 && dby > 20) {
                facingRight = true;
            }

            // movement if this entity collides on the right side of something
            if (c.crossedRight && pposX > posX && dty > 8 && dby > 6) {
                facingRight = false;
            }

            // movement if it collides on the bottom of this entity
            if (c.crossedTop && posY > pposY && (drx > 8 && dlx > 8)) {
                newPosY -= dty;
                touchesGround = true;
            }

            // movement if it collides on the top of this entity
            if (c.crossedBottom && posY < pposY && (drx > 8 && dlx > 8)) {
                newPosY += dby;
            }
        } else if (c.collidedWith instanceof Shot && ((Shot) c.collidedWith).getShooter() != this) {
            //(Shot)c.collidedWith).remove();
            health -= 1;
            if (health == 0) {
                isDead = true;
                System.out.println("Enemy down!");
            } else {
                speed += 1;
            }
        }

    }

    @Override
    public void calculateMovement() {

        // applying gravity!
        if (!isDead) {

            if (ticker < 5) {
                ticker++;
            } else {
                ticker = 0;
                currentSprite.nextImage();
            }

            if (!touchesGround) {
                double temp = Physics.calculateGravity(posY, pposY, 16);
                newPosY = posY - temp;
            } else if (touchesGround) {
                newPosY -= (6 + speed);
                touchesGround = false;
            }

            if (!facingRight) {
                currentSprite = spriteFacingLeft;
            } else if (facingRight) {
                currentSprite = spriteFacingRight;
            }

            double[] playerPos = mapReference.getPlayerState();
            int playerPosX = (int) (playerPos[0] - posX);

            if (playerPosX < 0) {
                facingRight = false;
            } else {
                facingRight = true;
            }

            if (shotCooldown > 0) {
                shotCooldown--;
            }
            if (shotCooldown == 0) {
                //sound.close();
                // only shoot if player is infront of line of sight!
                if (facingRight && playerPosX > 0 && playerPosX < 300
                        || !facingRight && playerPosX < 0 && playerPosX > -300) {
                    addshot();
                    shotCooldown += 120;
                }
            }
            shots.update();
        }
    }

    @Override
    public boolean facingRight() {
        return true;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    private void addshot() {
        if (facingRight) {
            shots.addShot(posX + 75, posY + 45, facingRight, this, mapReference);
        } else {
            shots.addShot(posX + 15, posY + 45, facingRight, this, mapReference);

        }
    }

    @Override
    public boolean isDead() {
        return isDead;
    }
}
