package main.java.schema.tdobjects;

import java.awt.geom.Point2D;

import main.java.engine.objects.powerup.TDPowerupPowerup;

public abstract class ItemSchema extends TDObjectSchema{
	public static final String COST = "Cost";
	public static final String BUILDUP_TIME = "Build Up Time (sec)";
	public static final String DAMAGE = "Damage";
	public static final String FLASH_INTERVAL = "Flash interval";
	public static final String LOCATION = "Location";
	public static final String DESCRIPTION = "Description Displayed";

	public ItemSchema(Class<? extends TDPowerupPowerup> myConcreteType) {
		super(myConcreteType);
		myAttributeSet.add(ItemSchema.BUILDUP_TIME);
		myAttributeSet.add(ItemSchema.COST);
		myAttributeSet.add(ItemSchema.DAMAGE);
		myAttributeSet.add(ItemSchema.FLASH_INTERVAL);
		myAttributeSet.add(ItemSchema.IMAGE_NAME);
		myAttributeSet.add(ItemSchema.NAME);
		myAttributeSet.add(ItemSchema.LOCATION);
		myAttributeSet.add(ItemSchema.DESCRIPTION);
	}

}
