package main.java.schema.tdobjects.powerups;

import main.java.author.view.tabs.enemy.EnemyViewDefaults;
import main.java.author.view.tabs.item.ItemViewConstants;
import main.java.engine.objects.monster.SimpleMonster;
import main.java.engine.objects.powerup.Annihilator;
import main.java.schema.tdobjects.ItemSchema;
import main.java.schema.tdobjects.MonsterSchema;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * This is a settings object for a specific type of Annihilator.
 */
public class AnnihilatorPowerupSchema extends ItemSchema {
	public static final Class<Annihilator> MY_CONCRETE_TYPE = Annihilator.class;

	public AnnihilatorPowerupSchema() {
		super(MY_CONCRETE_TYPE);
		myAttributeSet.add(AreaBombItemSchema.RANGE);
	}

	/**
	 * @param name name of monster
	 */
	public AnnihilatorPowerupSchema(String name) {
		this();
		populateDefaultAttributes(name);
	}

	public void populateDefaultAttributes(String name) {
		addAttribute(ItemSchema.NAME, name);
		addAttribute(ItemSchema.BUILDUP_TIME, ItemViewConstants.BUILDUP_DEFAULT);
		addAttribute(ItemSchema.COST, ItemViewConstants.COST_DEFAULT);
		addAttribute(ItemSchema.DAMAGE, ItemViewConstants.DAMAGE_DEFAULT);
		addAttribute(ItemSchema.FLASH_INTERVAL, ItemViewConstants.FLASH_INTERVAL_DEFAULT);
		addAttribute(ItemSchema.IMAGE_NAME, ItemViewConstants.IMAGE_DEFAULT);
		addAttribute(AreaBombPowerupSchema.RANGE, ItemViewConstants.RANGE_DEFAULT);
	}

	@Override
	protected Set<String> populateAdditionalAttributes() {
		return new HashSet<String>(); // empty set, no new attributes
	}
}