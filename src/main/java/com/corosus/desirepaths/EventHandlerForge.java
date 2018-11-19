package com.corosus.desirepaths;

import com.corosus.desirepaths.block.BlockGrassWorn;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import CoroUtil.util.CoroUtilPlayer;
import CoroUtil.util.Vec3;
import CoroUtil.world.WorldDirectorManager;
import CoroUtil.world.grid.block.BlockDataPoint;

public class EventHandlerForge {

	
	
	@SubscribeEvent
	public void entityTick(LivingUpdateEvent event) {

		//MinecraftServer.tickRate = 5;
		
		EntityLivingBase ent = event.getEntityLiving();
		int walkOnRate = 5;
		
		if (!ent.world.isRemote) {
			if (ent.onGround && ent.world.getTotalWorldTime() % walkOnRate == 0) {
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

					BlockGrassWorn.performWearTick(ent.world, pos, 1F);
				}
			}
		}
	}
}
