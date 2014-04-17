package main.java.engine.factory;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jgame.impl.JGEngineInterface;
import main.java.engine.Model;
import main.java.engine.objects.Exit;
import main.java.engine.objects.item.TDItem;
import main.java.engine.objects.monster.Monster;
import main.java.engine.objects.tower.BombTower;
import main.java.engine.objects.tower.FreezeTower;
import main.java.engine.objects.tower.ITower;
import main.java.engine.objects.tower.MoneyTower;
import main.java.engine.objects.tower.ShootingTower;
import main.java.engine.objects.tower.SimpleTower;
import main.java.engine.objects.tower.TowerBehaviors;
import main.java.engine.util.Reflection;
import main.java.exceptions.engine.MonsterCreationFailureException;
import main.java.exceptions.engine.TowerCreationFailureException;
import main.java.schema.tdobjects.MonsterSchema;
import main.java.schema.tdobjects.TDObjectSchema;
import main.java.schema.tdobjects.TowerSchema;


public class TDObjectFactory {
    private static final String ITEM_PATH = "main.java.engine.objects.item.";
	private JGEngineInterface engine;
    private Map<String, TDObjectSchema> tdObjectSchemaMap;
    private List<String> possibleTowersNames;

    public TDObjectFactory (JGEngineInterface engine) {
        this.engine = engine;
        tdObjectSchemaMap = new HashMap<>();
        possibleTowersNames = new ArrayList<String>();
    }

    public void loadTDObjectSchemas (List<TDObjectSchema> schemas) {
        // TODO: Get rid of repetition in loading schemas
        for (TDObjectSchema s : schemas) {
            String objName = (String) s.getAttributesMap().get(TDObjectSchema.NAME);
            String objImagePath =
                    Model.RESOURCE_PATH + s.getAttributesMap().get(TDObjectSchema.IMAGE_NAME);
            engine.defineImage(objName, "-", 1, objImagePath, "-");
            tdObjectSchemaMap.put(objName, s);
        }
    }
    
    @SuppressWarnings("unchecked")
	public void loadTowerSchemas (List<TowerSchema> schemas) {
    	for (TowerSchema s: schemas) {
    		possibleTowersNames.add((String) s.getAttributesMap().get(TDObjectSchema.NAME));
    	}
    	// Perhaps a better method of casting than using an intermediate wildcard type?
    	loadTDObjectSchemas((List<TDObjectSchema>)(List<?>) schemas);
    }
    
    @SuppressWarnings("unchecked")
	public void loadMonsterSchemas (List<MonsterSchema> schemas) {
    	loadTDObjectSchemas((List<TDObjectSchema>)(List<?>) schemas);
    }

    /**
     * Place tower at a given location's tile.
     * 
     * @param location The coordinates to place the tower at
     * @param towerName The name of the tower to place
     * @return The new Tower object
     * @throws TowerCreationFailureException
     */
    public ITower placeTower (Point2D location, String towerName)
                                                                 throws TowerCreationFailureException {
        Point2D tileOrigin = findTileOrigin(location);
        try {
            TDObjectSchema schema = tdObjectSchemaMap.get(towerName);
            schema.addAttribute(TowerSchema.LOCATION, (Serializable) tileOrigin);
            Object[] towerParameters = { schema.getAttributesMap() };

            // return new MoneyTower(new ShootingTower((BaseTower)
            // placeObject(schema.getMyConcreteType(), towerParameters), 10, 3, 200));
            return addTowerBehaviors((SimpleTower) placeObject(schema.getMyConcreteType(),
                                                               towerParameters),
                                     schema);
        }
        catch (Exception e) {
            throw new TowerCreationFailureException(e);
        }
    }

    private ITower addTowerBehaviors (SimpleTower baseTower, TDObjectSchema schema) {
        ITower finalTower = baseTower;
        Map<String, Serializable> attributes = schema.getAttributesMap();
        Collection<TowerBehaviors> towerBehaviors =
                (Collection<TowerBehaviors>) attributes.get(TowerSchema.TOWER_BEHAVIORS);
        for (TowerBehaviors towerBehavior : towerBehaviors) {
            Class<? extends ITower> concreteType = towerBehavior.getConcreteClass();
            Object[] towerParameters = { finalTower, attributes };
            finalTower = (ITower) placeObject(concreteType, towerParameters);
        }
        return finalTower;
    }

    /**
     * Places a monster at set locations. Upon spawning, the monster will traverse from the entrance
     * to the exit with a path finding algorithm.
     * 
     * @param entrance The spawn location of the monster
     * @param exit The exit location of the monster
     * @param monsterName The name of the monster to place
     * @return The new Monster object
     * @throws MonsterCreationFailureException
     */
    public Monster placeMonster (Point2D entrance, Exit exit, String monsterName)
                                                                                 throws MonsterCreationFailureException {
        try {
            TDObjectSchema schema = tdObjectSchemaMap.get(monsterName);

            schema.addAttribute(MonsterSchema.ENTRANCE_LOCATION, (Serializable) entrance);
            schema.addAttribute(MonsterSchema.EXIT_LOCATION, exit);

            List<Integer> blocked = new ArrayList<Integer>();
            blocked.add(2); // TODO, change -- provided by factory
            schema.addAttribute(MonsterSchema.BLOCKED_TILES, (Serializable) blocked);
            Object[] monsterParameters = { schema.getAttributesMap() };

            return (Monster) placeObject(schema.getMyConcreteType(), monsterParameters);
        }
        catch (Exception e) {
            throw new MonsterCreationFailureException(e);
        }
    }
    
    public TDItem placeItem (Point2D location, String itemName) throws 
    	ClassNotFoundException, 
    	InstantiationException, 
    	IllegalAccessException, 
    	IllegalArgumentException, 
    	InvocationTargetException {
    	
    	// TODO: use schema to create item objects!!
    	Point2D tileOrigin = findTileOrigin(location);
    	Class c = Class.forName(ITEM_PATH + itemName);
		Constructor[] con = c.getConstructors();
		TDItem newItem = (TDItem) con[0].newInstance(tileOrigin.getX(), tileOrigin.getY());
    	return newItem;
    }

    /**
     * Uses the Reflection utility class to create the appropriate object with parameters
     * 
     * @param objectType
     * @param parameters
     * @return
     */
    private Object placeObject (Class<?> objectType, Object[] parameters) {
        return Reflection.createInstance(objectType.getName(), parameters);
    }

    /**
     * Find the top-left corner associated with the tile associated with the given location. Used to
     * place
     * new objects and images.
     * 
     * @param location Coordinate of the map used to find the associated file
     * @return The top left corner of the tile at the given coordinate
     */
    private Point2D findTileOrigin (Point2D location) {
        int curXTilePos = (int) location.getX() / engine.tileWidth() * engine.tileWidth();
        int curYTilePos = (int) location.getY() / engine.tileHeight() * engine.tileHeight();
        return new Point2D.Double(curXTilePos, curYTilePos);
    }
    
    /**
     * Returns names of towers that have loaded schemas, and can possibly be created.
     * @return an unmodifiable list
     */
    public List<String> getPossibleTowersNames(){
        return Collections.unmodifiableList(possibleTowersNames);
    }
}
