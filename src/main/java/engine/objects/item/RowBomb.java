package main.java.engine.objects.item;

import main.java.engine.EnvironmentKnowledge;
import main.java.engine.objects.monster.Monster;

/**
 * 
 * A special bomb that destroys a row of monsters
 * 
 * @author Lawrence
 *
 */
public class RowBomb extends TDItem{
	
	private static final int FLASH_INTERVAL = 5;
	private static final String IMAGE = "row_bomb";
	private static final double COST = 100;
	private static final double BUILDUP_TIME = 100;
	private static final double DAMAGE = Double.MAX_VALUE;
	
	private String image;

	public RowBomb(double x, double y) {
		super("row_bomb", x, y, IMAGE, COST, BUILDUP_TIME, DAMAGE);
	}

	@Override
	public void doAction(EnvironmentKnowledge environmentKnowledge) {
		timeCounter++;
		if (timeCounter >= buildupTime) {
			for (Monster m : environmentKnowledge.getAllMonsters()) {
				if (isInRange(m, x, y)) {
					m.takeDamage(DAMAGE);
				}
			}
			lightFire();
			terminateItem();
		} else flash(timeCounter, FLASH_INTERVAL, IMAGE);
	}

	private void lightFire() {
		
		
	}

	private boolean isInRange(Monster m, double x, double y) {
		double lower = y - m.getImageBBoxConst().height/2;
		double upper = y + m.getImageBBoxConst().height/2;
		return ((m.y > lower) && (m.y < upper));
	}

}
