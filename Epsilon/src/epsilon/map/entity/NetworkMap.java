package epsilon.map.entity;

import epsilon.game.Collision;
import epsilon.game.Input;
import epsilon.game.SoundPlayer;
import epsilon.map.Background;
import epsilon.map.Map;
import epsilon.map.WorldStore;
import epsilon.menu.DeathPage;
import epsilon.menu.Menu;
import epsilon.net.NetworkHandler;
import java.util.ArrayList;

/**
 *
 *
 * @author Marius
 */
public class NetworkMap extends Map {

    private int shotCooldown = 0;

    /**
     * Initialises all entities on the map, and all fields in the object
     */
    public NetworkMap (String name) {
        super(name);
    }

    @Override
    public void update() {

        if (playerEntity.isDead()) {
            Menu.get().setMenu(new DeathPage());
        }

        while(NetworkHandler.getInstance().hasNewPlayers()) {
            String s = NetworkHandler.getInstance().getNewPlayer();
            double[] d = NetworkHandler.getInstance().getPlayerStateByName(s);

            if (d != null) {
                TestNetworkEntity n = new TestNetworkEntity(d[0], d[1], s);
                renderableEntities.add(n);
                moveableEntities.add(n);
                entities.add(n);
            }
        }

        // shots
        if (shotCooldown > 0) {
            shotCooldown--;
        }
        if (Input.get().attack() && shotCooldown == 0) {
            //sound.close();
            shots.addShot(playerEntity.getXPosition(), playerEntity.getYPosition(), playerEntity.facingRight());
            shotCooldown += 30;
        }

        MoveableEntity[] temp = new MoveableEntity[moveableEntities.size()];
        moveableEntities.toArray(temp);

        Collision c;

        for (int i = 0; i < temp.length; i++) {
            temp[i].calculateMovement();

            worldstore.checkCollision(temp[i]);

            if (temp[i] instanceof TestNetworkEntity && !((TestNetworkEntity)temp[i]).exists()) {
                moveableEntities.remove(temp[i]);
                renderableEntities.remove(temp[i]);
                entities.remove(temp[i]);
            }

            c = temp[i].collision(playerEntity);
            if (c.collided) {
                playerEntity.collided(c);
            }
        }

        for (int i=0;i<temp.length;i++) {
            temp[i].move();
        }

        NetworkHandler.getInstance().sendPlayerAction();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initialiseNonStatic(String s) {
        
        super.initialiseNonStatic(s);
        renderableEntities = new ArrayList<Entity>();
        moveableEntities = new ArrayList<MoveableEntity>();
        entities = new ArrayList<Entity>();

        bg = new Background("/pics/bg3.png", 1.25);

        playerEntity = new TestPlayerEntity(-70, 400, s);

        renderableEntities.add(playerEntity);
        moveableEntities.add(playerEntity);
        entities.add(playerEntity);

        String filename = "/sound/zabutom.lets.shooting.mp3";
        soundtrack = new SoundPlayer(filename);
        //soundtrack.play();
    }

    @Override
    protected void initialiseStatic() {

        super.initialiseStatic();

        worldstore.add(new Floor_1(-500, 525));
        worldstore.add(new Floor_1(-500, 565));
        worldstore.add(new Floor_1(-450, 565));
        worldstore.add(new Floor_1(-400, 565));
        worldstore.add(new Floor_1(-350, 565));
        worldstore.add(new Floor_1(-300, 565));
        worldstore.add(new Floor_1(-250, 565));
        worldstore.add(new Floor_1(-200, 565));
        worldstore.add(new Floor_1(-150, 565));
        worldstore.add(new Floor_1(-100, 565));
        worldstore.add(new Floor_1(-50, 565));
        worldstore.add(new Floor_1(-50, 525));

        worldstore.add(new Floor_1(1000, 565));
        worldstore.add(new Floor_1(1050, 565));
        worldstore.add(new Floor_1(1100, 565));
        worldstore.add(new Floor_1(1150, 565));
        worldstore.add(new Floor_1(1200, 565));
        worldstore.add(new Floor_1(1250, 565));
        worldstore.add(new Floor_1(1300, 565));
        worldstore.add(new Floor_1(1350, 565));
        worldstore.add(new Floor_1(1400, 565));
        worldstore.add(new Floor_1(1450, 565));

        worldstore.add(new Floor_1(80, 505));

        worldstore.add(new Floor_1(250, 415));
        worldstore.add(new Floor_1(300, 415));

        worldstore.add(new Floor_1(500, 385));
        worldstore.add(new Floor_1(550, 385));


        worldstore.add(new Floor_1(350, 455));
        worldstore.add(new Floor_1(500, 495));

    }

}
 