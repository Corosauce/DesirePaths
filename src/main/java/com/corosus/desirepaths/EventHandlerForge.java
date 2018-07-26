package com.corosus.desirepaths;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import CoroUtil.util.CoroUtilPlayer;
import CoroUtil.util.Vec3;
import CoroUtil.world.WorldDirectorManager;
import CoroUtil.world.grid.block.BlockDataPoint;

public class EventHandlerForge {

	
	
	@SubscribeEvent
	public void entityTick(LivingUpdateEvent event) {
		
		EntityLivingBase ent = event.getEntityLiving();
		int walkOnRate = 5;
		
		if (!ent.world.isRemote) {
			if (ent.onGround && ent.world.getTotalWorldTime() % walkOnRate == 0) {
				//TODO: fix for players, probably broken due to player server side not having motion data
				double speed = Math.sqrt(ent.motionX * ent.motionX + ent.motionY * ent.motionY + ent.motionZ * ent.motionZ);
				if (ent instanceof EntityPlayer) {
					Vec3 vec = CoroUtilPlayer.getPlayerSpeedCapped((EntityPlayer) ent, 0.1F);
					speed = Math.sqrt(vec.xCoord * vec.xCoord + vec.yCoord * vec.yCoord + vec.zCoord * vec.zCoord);
					//System.out.println("player speed: " + speed);
				}
				if (speed > 0.08) {

					/*if (ent instanceof EntityPlayer) {
						System.out.println("durr");
					}*/
					//System.out.println(entityId + " - speed: " + speed);
					int newX = MathHelper.floor(ent.posX);
					int newY = MathHelper.floor(ent.getEntityBoundingBox().minY - 1);
					int newZ = MathHelper.floor(ent.posZ);
					IBlockState state = ent.world.getBlockState(new BlockPos(newX, newY, newZ));
					Block id = state.getBlock();
					
					//check for block that can have beaten path data
					int index = -1;
					for (int i = 0; i < DesirePaths.listDegradeProgression.size(); i++) {
						Block block = DesirePaths.listDegradeProgression.get(i);
						if (id == block) {
							index = i;
							break;
						}
					}
					
					//if invalid or last entry in list
					if (index == -1 || index == DesirePaths.listDegradeProgression.size() -1) {
						return;
					}
					
					Block blockNext = DesirePaths.listDegradeProgression.get(index+1);
					
					//if (id == Blocks.GRASS) {
						BlockDataPoint bdp = WorldDirectorManager.instance().getBlockDataGrid(ent.world).getBlockData(newX, newY, newZ);// ServerTickHandler.wd.getBlockDataGrid(world).getBlockData(newX, newY, newZ);
						
						//add depending on a weight?
						bdp.walkedOnAmount += 0.25F;
						
						//System.out.println("inc walk amount: " + bdp.walkedOnAmount);
						
						if (bdp.walkedOnAmount > 1F) {
							//System.out.println("dirt!!!");
							IBlockState stateUp = ent.world.getBlockState(new BlockPos(newX, newY+1, newZ));
							if (true/*stateUp.getBlock() == Blocks.AIR || stateUp.getMaterial() == Material.PLANTS || stateUp.getMaterial() == Material.VINE*/) {
								ent.world.setBlockState(new BlockPos(newX, newY, newZ), blockNext.getDefaultState());
								//if past first stage of trample, also take out the plant above
								if (index >= 4) {
									if (stateUp.getMaterial() == Material.PLANTS || stateUp.getMaterial() == Material.VINE) {
										ent.world.setBlockState(new BlockPos(newX, newY+1, newZ), Blocks.AIR.getDefaultState());
									}
								}
							}
							
							//BlockRegistry.dirtPath.blockID);
							//cleanup for memory
							WorldDirectorManager.instance().getBlockDataGrid(ent.world).removeBlockData(newX, newY, newZ);
							//ServerTickHandler.wd.getBlockDataGrid(world).removeBlockData(newX, newY, newZ);
						}
					//}
				}
			}
		}
	}
}
