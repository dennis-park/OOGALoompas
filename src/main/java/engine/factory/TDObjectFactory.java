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
import main.java.engine.PathfinderManager;
import main.java.engine.map.TDMap;
import main.java.engine.objects.Exit;
import main.java.engine.objects.monster.Monster;
import main.java.engine.objects.powerup.TDPowerupPowerup;
import main.java.engine.objects.tower.BombTower;
import main.java.engine.objects.tower.FreezeTower;
import main.java.engine.objects.tower.ITower;
import main.java.engine.objects.tower.MoneyTower;
import main.java.engine.objects.tower.ShootingTower;
import main.java.engine.objects.tower.SimpleTower;
import main.java.engine.objects.tower.TowerBehaviors;
import main.java.engine.util.Reflection;
import main.java.exceptions.engine.ItemCreationFailureException;
import main.java.exceptions.engine.MonsterCreationFailureException;
import main.java.exceptions.engine.TowerCreationFailureException;
import main.java.schema.tdobjects.ItemSchema;
import main.java.schema.tdobjects.MonsterSchema;
import main.java.schema.tdobjects.TDObjectSchema;
import main.java.schema.tdobjects.TowerSchema;


/**
 * A factory in charge of creating objects based on schemas
 * Objects created include towers, monsters, and items
 *
 */
public class TDObjectFactory {
	private static final String ITEM_PATH = "main.java.engine.objects.item.";
	private JGEngineInterface engine;
	private Map<String, TDObjectSchema> tdObjectSchemaMap;
	private List<String> possibleTowersNames;
	private List<String> possibleItemNames;
	private Map<String, String> towerMap;
	private Map<String, String> itemMap;

	public TDObjectFactory (JGEngineInterface engine) {
		this.engine = engine;
		tdObjectSchemaMap = new HashMap<>();
		possibleTowersNames = new ArrayList<String>();
		possibleItemNames = new ArrayList<String>();
		towerMap = new HashMap<String, String>();
		itemMap = new HashMap<String, String>();
	}

	/**
	 * Load tower defense object schemas into schema map
	 * 
	 * @param schemas
	 */
	public void loadTDObjectSchemas (List<TDObjectSchema> schemas) {
		for (TDObjectSchema s : schemas) {
			String objName = (String) s.getAttributesMap().get(TDObjectSchema.NAME);
			String objImagePath =
					Model.RESOURCE_PATH + s.getAttributesMap().get(TDObjectSchema.IMAGE_NAME);
			engine.defineImage(objName, "-", 1, objImagePath, "-");
			tdObjectSchemaMap.put(objName, s);
		}
	}

	/**
	 * Load tower schemas
	 * 
	 * @param schemas
	 */
	@SuppressWarnings("unchecked")
	public void loadTowerSchemas (List<TowerSchema> schemas) {
		for (TowerSchema towerschema: schemas) {
			String towerName = (String) towerschema.getAttributesMap().get(TDObjectSchema.NAME);
			String towerDescription = (String) towerschema.getAttributesMap().get(TowerSchema.DESCRIPTION);
			possibleTowersNames.add(towerName);
			towerMap.put(towerName, towerDescription);
			defineBulletImage(towerschema, TowerSchema.BULLET_IMAGE_NAME);
			defineBulletImage(towerschema, TowerSchema.SHRAPNEL_IMAGE_NAME);
		}
		// Perhaps a better method of casting than using an intermediate wildcard type?
		loadTDObjectSchemas((List<TDObjectSchema>)(List<?>) schemas);
	}

	/**
	 * Define the image of a bullet/shrapnel attribute in jgame engine.
	 * @param towerschema the tower schema
	 * @param imageNameConstant a constant of TowerSchema that is an image name attribute
	 */
	private void defineBulletImage (TowerSchema towerschema, String imageNameConstant) {
		String bulletImageName = (String) towerschema.getAttributesMap().get(imageNameConstant);
		String bulletImagePath = Model.RESOURCE_PATH + towerschema.getAttributesMap().get(imageNameConstant);
		engine.defineImage(bulletImageName, "-", 1, bulletImagePath, "-");
	}

	/**
	 * Load monster schemas
	 * 
	 * @param schemas
	 */
	@SuppressWarnings("unchecked")
	public void loadMonsterSchemas (List<MonsterSchema> schemas) {
		loadTDObjectSchemas((List<TDObjectSchema>)(List<?>) schemas);
	}

	/**
	 * Load item schemas
	 * 
	 * @param schemas
	 */
	@SuppressWarnings("unchecked")
	public void loadItemSchemas(List<ItemSchema> schemas) {
		for (ItemSchema i: schemas) {
			possibleItemNames.add((String) i.getAttributesMap().get(TDObjectSchema.NAME));
		}
		loadTDObjectSchemas((List<TDObjectSchema>)(List<?>) schemas);		
	}

	/**
	 * Places an item at the given location. 
	 * @param location
	 * @param itemName
	 * @return The new TDItem object
	 * @throws ItemCreationFailureException 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public TDPowerupPowerup placeItem (Point2D location, String itemName) throws ItemCreationFailureException {

		Point2D tileOrigin = TDMap.findTileOrigin(location);
		try {
			TDObjectSchema schema = tdObjectSchemaMap.get(itemName);
			schema.addAttribute(ItemSchema.LOCATION, (Serializable) tileOrigin);
			Object[] itemParameters = { schema.getAttributesMap() };
			return (TDPowerupPowerup) placeObject(schema.getMyConcreteType(), itemParameters);
		}
		catch (Exception e) {
			throw new ItemCreationFailureException(e);
		}
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
		Point2D tileOrigin = TDMap.findTileOrigin(location);
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
	public Monster placeMonster (Point2D entrance, Exit exit,
			PathfinderManager pathfinderManager,
			String monsterName)
					throws MonsterCreationFailureException {
		try {
			TDObjectSchema schema = tdObjectSchemaMap.get(monsterName);

			schema.addAttribute(MonsterSchema.ENTRANCE_LOCATION, (Serializable) entrance);
			schema.addAttribute(MonsterSchema.EXIT_LOCATION, exit);
			schema.addAttribute(MonsterSchema.PATHFINDER_MANAGER, pathfinderManager);

			Object[] monsterParameters = { schema.getAttributesMap() };

			return (Monster) placeObject(schema.getMyConcreteType(), monsterParameters);
		}
		catch (Exception e) {
			throw new MonsterCreationFailureException(e);
		}
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
	 * Returns the attributes of a TDobject from its schema, if schema doesn't exist, returns null
	 * @param objName
	 * @return unmodifiable map of attributes
	 */
	public Map<String, Serializable> getTDObjectAttributes(String objName) {
		return tdObjectSchemaMap.containsKey(objName) ? Collections.unmodifiableMap(tdObjectSchemaMap.get(objName).getAttributesMap()) : null;
	}

	/**
	 * Returns names of towers that have loaded schemas, and can possibly be created.
	 * @return an unmodifiable list
	 */
	public List<String> getPossibleTowersNames(){
		return Collections.unmodifiableList(possibleTowersNames);
	}

	/**
	 * Returns the names of items that have loaded schemas, and can possibly be created/
	 * 
	 * @return an unmodifiable list
	 */
	public List<String> getPossibleItemNames() {
		return Collections.unmodifiableList(possibleItemNames);
	}

	/**
	 * Returns the description associated with a tower. 
	 * 
	 * @param towerName
	 * @return
	 */
	public String getTowerDescription(String towerName) {
		return towerMap.get(towerName);
	}
	
	/**
	 * Returns the description associated with an item
	 * 
	 * @param itemName
	 * @return
	 */
	public String getItemDescription(String itemName) {
		return itemMap.get(itemName);
	}


}
