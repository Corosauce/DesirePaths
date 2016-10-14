package com.corosus.desirepaths;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.corosus.desirepaths.block.BlockGrassWorn;

public class CommonProxy
{
	
    public CommonProxy()
    {
    	
    }

    public void init()
    {
    	addBlock(DesirePaths.dirt_6 = new BlockGrassWorn(), "grass_worn_6", "Worn Grass");
    	addBlock(DesirePaths.dirt_5 = new BlockGrassWorn(), "grass_worn_5", "Worn Grass");
    	addBlock(DesirePaths.dirt_4 = new BlockGrassWorn(), "grass_worn_4", "Worn Grass");
    	addBlock(DesirePaths.dirt_3 = new BlockGrassWorn(), "grass_worn_3", "Worn Grass");
    	addBlock(DesirePaths.dirt_2 = new BlockGrassWorn(), "grass_worn_2", "Worn Grass");
    	addBlock(DesirePaths.dirt_1 = new BlockGrassWorn(), "grass_worn_1", "Worn Grass");
    	
    	DesirePaths.listDegradeProgression.add(Blocks.GRASS);
    	DesirePaths.listDegradeProgression.add(DesirePaths.dirt_1);
    	DesirePaths.listDegradeProgression.add(DesirePaths.dirt_2);
    	DesirePaths.listDegradeProgression.add(DesirePaths.dirt_3);
    	DesirePaths.listDegradeProgression.add(DesirePaths.dirt_4);
    	DesirePaths.listDegradeProgression.add(DesirePaths.dirt_5);
    	DesirePaths.listDegradeProgression.add(DesirePaths.dirt_6);
    }
    
    public void addBlock(Block parBlock, String unlocalizedName, String blockNameBase) {
		GameRegistry.registerBlock(parBlock, unlocalizedName);
		parBlock.setUnlocalizedName(getNamePrefixed(unlocalizedName));
	}
    
    public static String getNamePrefixed(String name) {
    	return DesirePaths.MODID + "." + name;
    }
}
