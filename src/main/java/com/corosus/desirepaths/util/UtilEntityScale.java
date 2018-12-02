package com.corosus.desirepaths.util;

import net.minecraft.entity.Entity;

public class UtilEntityScale {

	public static float getWeight(Entity entity)
	{
		return entity.hasNoGravity() ? 0.0F : entity.height * entity.width;
	}
}
