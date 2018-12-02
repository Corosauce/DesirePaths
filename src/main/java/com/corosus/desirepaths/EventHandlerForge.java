package com.corosus.desirepaths;

import CoroUtil.ai.BehaviorModifier;
import CoroUtil.forge.CULog;

import com.animania.common.entities.sheep.EntityAnimaniaSheep;
import com.corosus.desirepaths.ai.EntityAIEatGrassExtended;
import com.corosus.desirepaths.block.BlockGrassWorn;
import com.corosus.desirepaths.config.ConfigDesirePaths;
import com.corosus.desirepaths.util.UtilEntityBuffsInstances;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import CoroUtil.util.CoroUtilPlayer;
import CoroUtil.util.Vec3;

public class EventHandlerForge {

	@SubscribeEvent
	public void onEntityCreatedOrLoaded(EntityJoinWorldEvent event) {
		if (event.getEntity().world.isRemote) return;

		if (event.getEntity() instanceof EntitySheep && !(Loader.isModLoaded("desirepaths") && event.getEntity() instanceof EntityAnimaniaSheep)) {
			EntitySheep ent = (EntitySheep) event.getEntity();

			
			CULog.dbg("replacing EntityAIEatGrass with our extended version");
			EntityAIEatGrassExtended task = new EntityAIEatGrassExtended(ent);
			UtilEntityBuffsInstances.replaceTaskIfMissing(ent, EntityAIEatGrass.class, task, 5);

			ent.entityAIEatGrass = task;
		}
	}
	
	@SubscribeEvent
	public void entityTick(LivingUpdateEvent event) {

		//dev env debug
		//MinecraftServer.tickRate = 50;
		
		EntityLivingBase ent = event.getEntityLiving();
		int walkOnRate = 5;
		
		if (!ent.world.isRemote) {
			if (ConfigDesirePaths.pathsWearDown && ent.onGround && ent.world.getTotalWorldTime() % walkOnRate == 0) {

				//TODO: motionY always has gravity in it i think, should we not factor it in here? might be mudding up speed calc
				double speed = Math.sqrt(ent.motionX * ent.motionX + ent.motionY * ent.motionY + ent.motionZ * ent.motionZ);

				if (ent instanceof EntityPlayer) {
					Vec3 vec = CoroUtilPlayer.getPlayerSpeedCapped((EntityPlayer) ent, 0.1F);
					speed = Math.sqrt(vec.xCoord * vec.xCoord + vec.yCoord * vec.yCoord + vec.zCoord * vec.zCoord);
					//System.out.println("player speed: " + speed);
				}

				if (speed > 0.08) {
					int newX = MathHelper.floor(ent.posX);
					int newY = MathHelper.floor(ent.getEntityBoundingBox().minY - 1);
					int newZ = MathHelper.floor(ent.posZ);
					BlockPos pos = new BlockPos(newX, newY, newZ);

					float amp = 1F;
					if (ent instanceof EntitySheep) {
						amp = (float) Math.max(ConfigDesirePaths.sheepPathWearAmplifier, 0);
					}

					if (amp != 0) {
						BlockGrassWorn.performWearTick(ent.world, pos, amp);
					}
				}
			}
		}
	}
}
