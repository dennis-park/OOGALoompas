package main.java.engine.objects.detector.monsterdetector;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import main.java.engine.EnvironmentKnowledge;
import main.java.engine.objects.TDObject;
import main.java.engine.objects.detector.TDDetector;
import main.java.engine.objects.detector.TargetDetectorInterface;
import main.java.engine.objects.monster.Monster;

/**
 * A detector that finds the monster nearest to the coordinates
 * 
 * @author Lawrence
 * 
 */
public class NearestMonsterDetector extends TDDetector{

	@Override
	public List<Point2D> findTarget(double x, double y, 
			double range, EnvironmentKnowledge environmentKnowledge) {
		double minDistance = Double.MAX_VALUE;
		List<Point2D> closestMonster = new ArrayList<Point2D>();
		Point2D towerCoordinate = new Point2D.Double(x, y);

		for (Monster m : environmentKnowledge.getAllMonsters()) {
			if (isWithinDistance(m.getCurrentCoor(), towerCoordinate, minDistance) && 
					isWithinDistance(m.getCurrentCoor(), towerCoordinate, range)) {
				minDistance = m.getCurrentCoor().distance(towerCoordinate);
				// a tower should only target one monster at a time
				closestMonster.clear();
				closestMonster.add(centerCoordinate(m));
			}
		}

		return closestMonster;

	}

}
