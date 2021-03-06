package main.java.engine.objects.projectile;

import main.java.engine.objects.TDObject;
import main.java.engine.objects.monster.Monster;
import jgame.JGObject;


/**
 * A projectile that slows down monster upon contact. 
 *
 */
public class FreezeProjectile extends TDObject {

    public static final int TOWER_PROJECTILE_CID = 10;
    public static final double DEFAULT_SPEED = 20;

    /**
     * This is number is the % of which a monster's speed becomes once hit
     */
    private double mySlowdownSpeed;

    /**
     * Creates projectile heading in given angle with default speed.
     * 
     * @param x src x-coor
     * @param y src y-coor
     * @param angle Math.atan2(destX - srcX, destY - srcY)
     */
    public FreezeProjectile (double x, double y, double angle, double mySlowdownSpeed, String img) {
        super("projectile", x, y, TOWER_PROJECTILE_CID, img,
              DEFAULT_SPEED * Math.sin(angle),
              DEFAULT_SPEED * Math.cos(angle),
              JGObject.expire_off_view);
        this.mySlowdownSpeed = mySlowdownSpeed;
    }
    
    
    @Override
    public void hit (JGObject obj) {
        if (and(obj.colid, Monster.MONSTER_CID)) {
            ((Monster) obj).reduceSpeed(mySlowdownSpeed);
            this.remove();
        }
    }

}
