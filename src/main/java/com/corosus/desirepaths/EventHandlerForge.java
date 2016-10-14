package com.corosus.desirepaths;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import CoroUtil.world.WorldDirectorManager;
import CoroUtil.world.grid.block.BlockDataPoint;

public class EventHandlerForge {

	
	
	@SubscribeEvent
	public void entityTick(LivingUpdateEvent event) {
		
		EntityLivingBase ent = event.getEntityLiving();
		int walkOnRate = 5;
		
		if (!ent.worldObj.isRemote) {
			if (ent.worldObj.getTotalWorldTime() % walkOnRate == 0) {
				double speed = Math.sqrt(ent.motionX * ent.motionX + ent.motionY * ent.motionY + ent.motionZ * ent.motionZ);
				if (speed > 0.08) {
					//System.out.println(entityId + " - speed: " + speed);
					int newX = MathHelper.floor_double(ent.posX);
					int newY = MathHelper.floor_double(ent.getEntityBoundingBox().minY - 1);
					int newZ = MathHelper.floor_double(ent.posZ);
					IBlockState state = ent.worldObj.getBlockState(new BlockPos(newX, newY, newZ));
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
						BlockDataPoint bdp = WorldDirectorManager.instance().getBlockDataGrid(ent.worldObj).getBlockData(newX, newY, newZ);// ServerTickHandler.wd.getBlockDataGrid(worldObj).getBlockData(newX, newY, newZ);
						
						//add depending on a weight?
						bdp.walkedOnAmount += 0.25F;
						
						//System.out.println("inc walk amount: " + bdp.walkedOnAmount);
						
						if (bdp.walkedOnAmount > 1F) {
							//System.out.println("dirt!!!");
							IBlockState stateUp = ent.worldObj.getBlockState(new BlockPos(newX, newY+1, newZ));
							if (true/*stateUp.getBlock() == Blocks.AIR || stateUp.getMaterial() == Material.PLANTS || stateUp.getMaterial() == Material.VINE*/) {
								ent.worldObj.setBlockState(new BlockPos(newX, newY, newZ), blockNext.getDefaultState());
								//if past first stage of trample, also take out the plant above
								if (index >= 4) {
									if (stateUp.getMaterial() == Material.PLANTS || stateUp.getMaterial() == Material.VINE) {
										ent.worldObj.setBlockState(new BlockPos(newX, newY+1, newZ), Blocks.AIR.getDefaultState());
									}
								}
							}
							
							//BlockRegistry.dirtPath.blockID);
							//cleanup for memory
							WorldDirectorManager.instance().getBlockDataGrid(ent.worldObj).removeBlockData(newX, newY, newZ);
							//ServerTickHandler.wd.getBlockDataGrid(worldObj).removeBlockData(newX, newY, newZ);
						}
					//}
				}
			}
		}
	}
}
