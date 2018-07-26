package com.corosus.desirepaths;

import CoroUtil.forge.CoroUtil;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.corosus.desirepaths.block.BlockGrassWorn;

@Mod.EventBusSubscriber(modid = DesirePaths.MODID)
public class CommonProxy
{
	
    public CommonProxy()
    {
    	
    }

    public void init()
    {

    }

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		DesirePaths.proxy.addBlock(event, DesirePaths.dirt_6 = new BlockGrassWorn(), "grass_worn_6");
		DesirePaths.proxy.addBlock(event, DesirePaths.dirt_5 = new BlockGrassWorn(), "grass_worn_5");
		DesirePaths.proxy.addBlock(event, DesirePaths.dirt_4 = new BlockGrassWorn(), "grass_worn_4");
		DesirePaths.proxy.addBlock(event, DesirePaths.dirt_3 = new BlockGrassWorn(), "grass_worn_3");
		DesirePaths.proxy.addBlock(event, DesirePaths.dirt_2 = new BlockGrassWorn(), "grass_worn_2");
		DesirePaths.proxy.addBlock(event, DesirePaths.dirt_1 = new BlockGrassWorn(), "grass_worn_1");


		DesirePaths.listDegradeProgression.clear();
		DesirePaths.listDegradeProgression.add(Blocks.GRASS);
		DesirePaths.listDegradeProgression.add(DesirePaths.dirt_1);
		DesirePaths.listDegradeProgression.add(DesirePaths.dirt_2);
		DesirePaths.listDegradeProgression.add(DesirePaths.dirt_3);
		DesirePaths.listDegradeProgression.add(DesirePaths.dirt_4);
		DesirePaths.listDegradeProgression.add(DesirePaths.dirt_5);
		DesirePaths.listDegradeProgression.add(DesirePaths.dirt_6);
	}

	public void addBlock(RegistryEvent.Register<Block> event, Block parBlock, String unlocalizedName) {
		//GameRegistry.registerBlock(parBlock, unlocalizedName);

		parBlock.setUnlocalizedName(getNameUnlocalized(unlocalizedName));
		parBlock.setRegistryName(getNameDomained(unlocalizedName));

		//parBlock.setCreativeTab(CreativeTabs.MISC);

		if (event != null) {
			event.getRegistry().register(parBlock);
		}
	}

	public String getNameUnlocalized(String name) {
		return DesirePaths.MODID + "." + name;
	}

	public String getNameDomained(String name) {
		return DesirePaths.MODID + ":" + name;
	}
}
