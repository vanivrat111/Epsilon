package epsilon.map.entity;

import epsilon.game.Collision;
import epsilon.game.Input;
import epsilon.game.SoundPlayer;
import epsilon.map.Background;
import epsilon.map.Map;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * Map made for testing purposes.
 *
 * @author Marius
 */
public class TestMap implements Map {

    ArrayList<Entity> renderableEntities;
    ArrayList<MoveableEntity> moveableEntities;
    ArrayList<Entity> entities;
    ArrayList<Shot> shots;
    SoundPlayer soundtrack;

    MoveableEntity playerEntity;

    Background bg;

    private int shotCooldown = 0;

    public TestMap() {

        renderableEntities = new ArrayList<Entity>();
        moveableEntities = new ArrayList<MoveableEntity>();
        entities = new ArrayList<Entity>();
        shots = new ArrayList<Shot>();

        bg = new Background("/pics/bg3.png", 1.25);

        TestEntity test = new TestEntity(-70, 400);
        playerEntity = test;

        // TODO: Discuss; should floors have x,y parameter = x*50, y*40 as default?
        // => new Floor(3,4), instead of new Floor(150,160)?


        renderableEntities.add(new Floor_1(-500, 525));
        renderableEntities.add(new Floor_1(-500, 565));
        renderableEntities.add(new Floor_1(-450, 565));
        renderableEntities.add(new Floor_1(-400, 565));
        renderableEntities.add(new Floor_1(-350, 565));
        renderableEntities.add(new Floor_1(-300, 565));
        renderableEntities.add(new Floor_1(-250, 565));
        renderableEntities.add(new Floor_1(-200, 565));
        renderableEntities.add(new Floor_1(-150, 565));
        renderableEntities.add(new Floor_1(-100, 565));
        renderableEntities.add(new Floor_1(-50, 565));
        renderableEntities.add(new Floor_1(-50, 525));


        renderableEntities.add(new Floor_1(1000, 565));
        renderableEntities.add(new Floor_1(1050, 565));
        renderableEntities.add(new Floor_1(1100, 565));
        renderableEntities.add(new Floor_1(1150, 565));
        renderableEntities.add(new Floor_1(1200, 565));
        renderableEntities.add(new Floor_1(1250, 565));
        renderableEntities.add(new Floor_1(1300, 565));
        renderableEntities.add(new Floor_1(1350, 565));
        renderableEntities.add(new Floor_1(1400, 565));
        renderableEntities.add(new Floor_1(1450, 565));

        

        renderableEntities.add(new Floor_1(80, 505));

        renderableEntities.add(new Floor_1(250, 415));
        renderableEntities.add(new Floor_1(300, 415));

        renderableEntities.add(new Floor_1(500, 385));
        renderableEntities.add(new Floor_1(550, 385));


        renderableEntities.add(new Floor_1(350, 455));
        renderableEntities.add(new Floor_1(500, 495));

        Enemy enemy = new EnemyPatrol(-300,100);
        renderableEntities.add(enemy);
        moveableEntities.add(enemy);
        entities.add(enemy);


        renderableEntities.add(test);
        moveableEntities.add(test);
        entities.add(test);

        // Test MP3 playing
        String filename = "/sound/zabutom.lets.shooting.mp3";
        soundtrack = new SoundPlayer(filename);
        soundtrack.play();



    }

    public void render(Graphics g, int delta) {

        bg.render(g, playerEntity.getXPosition(), playerEntity.getYPosition());
        Entity[] temp = new Entity[renderableEntities.size()];
        renderableEntities.toArray(temp);

        for (int i = 0; i < temp.length; i++) {
            temp[i].render(g, delta, playerEntity.getXRenderPosition(), playerEntity.getYRenderPosition());
            temp[i].renderHitBox(g, playerEntity.getXRenderPosition(), playerEntity.getYRenderPosition());
        }

    }

    public void update() {

        // temp implemention of shot cooldown
        if (shotCooldown>0) {
            shotCooldown -= 1;
        }
        if (Input.get().attack() && shotCooldown == 0) {
            //sound.close();
            Shot shot = new Shot(playerEntity.getXPosition(),playerEntity.getYPosition(),playerEntity.facingRight());
            moveableEntities.add(shot);
            renderableEntities.add(shot);
            shots.add(shot);
            shotCooldown += 30;
        }

        // creating temp array to avoid unexpected behaviour when removing elements
        Shot[] tempshot = new Shot[shots.size()];
        shots.toArray(tempshot);

        // checking each shot if it has traveled its distance
        for (int i = 0; i < tempshot.length; i++) {
            Shot shot = tempshot[i];
            if (shot.distanceDone()) {
                // removing the shot from the lists
                shots.remove(shot);
                renderableEntities.remove(shot);
                moveableEntities.remove(shot);
            }
        }

        MoveableEntity[] temp = new MoveableEntity[moveableEntities.size()];
        moveableEntities.toArray(temp);

        for (int i = 0; i < temp.length; i++) {
            temp[i].calculateMovement();
        }

        // temp collision, simple test
        for (Entity ent : renderableEntities) {
            Collision c = ent.collision(playerEntity);
            if(c.collided) {
                playerEntity.collided(c);
            }
        }

        for (int i=0;i<temp.length;i++) {
            temp[i].move();
        }
    }
}