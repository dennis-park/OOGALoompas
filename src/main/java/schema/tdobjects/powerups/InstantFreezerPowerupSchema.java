package main.java.schema.tdobjects.powerups;

import main.java.author.view.tabs.item.ItemViewConstants;
import main.java.engine.objects.powerup.Annihilator;
import main.java.engine.objects.powerup.AreaBomb;
import main.java.engine.objects.powerup.InstantFreeze;
import main.java.schema.tdobjects.ItemSchema;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * This is a settings object for a specific type of InstantFreeze.
 */
public class InstantFreezerPowerupSchema extends ItemSchema {
	public static final Class<InstantFreeze> MY_CONCRETE_TYPE = InstantFreeze.class;
	public static final String FREEZE_DURATION = "Range";

	public InstantFreezerPowerupSchema() {
		super(MY_CONCRETE_TYPE);
	}

	/**
	 * @param name name of monster
	 */
	public InstantFreezerPowerupSchema(String name) {
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
		addAttribute(InstantFreezerPowerupSchema.FREEZE_DURATION, ItemViewConstants.FREEZE_DURATION_DEFAULT);

	}

	@Override
	protected Set<String> populateAdditionalAttributes() {
		myAttributeSet.add(InstantFreezerPowerupSchema.FREEZE_DURATION);
		return myAttributeSet;
	}
}