package com.corosus.desirepaths;

import CoroUtil.forge.CoroUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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


		BlockGrassWorn.listDegradeProgression.clear();
		BlockGrassWorn.listDegradeProgression.add(Blocks.GRASS);
		BlockGrassWorn.listDegradeProgression.add(DesirePaths.dirt_1);
		BlockGrassWorn.listDegradeProgression.add(DesirePaths.dirt_2);
		BlockGrassWorn.listDegradeProgression.add(DesirePaths.dirt_3);
		BlockGrassWorn.listDegradeProgression.add(DesirePaths.dirt_4);
		BlockGrassWorn.listDegradeProgression.add(DesirePaths.dirt_5);
		BlockGrassWorn.listDegradeProgression.add(DesirePaths.dirt_6);

		BlockGrassWorn.lookupBlockToStage.clear();
		addToLookup(Blocks.GRASS, 0);
		addToLookup(DesirePaths.dirt_1, 1);
		addToLookup(DesirePaths.dirt_2, 2);
		addToLookup(DesirePaths.dirt_3, 3);
		addToLookup(DesirePaths.dirt_4, 4);
		addToLookup(DesirePaths.dirt_5, 5);
		addToLookup(DesirePaths.dirt_6, 6);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		CoroUtil.proxy.addItemBlock(event, new ItemBlock(DesirePaths.dirt_1).setRegistryName(DesirePaths.dirt_1.getRegistryName()));
		CoroUtil.proxy.addItemBlock(event, new ItemBlock(DesirePaths.dirt_2).setRegistryName(DesirePaths.dirt_2.getRegistryName()));
		CoroUtil.proxy.addItemBlock(event, new ItemBlock(DesirePaths.dirt_3).setRegistryName(DesirePaths.dirt_3.getRegistryName()));
		CoroUtil.proxy.addItemBlock(event, new ItemBlock(DesirePaths.dirt_4).setRegistryName(DesirePaths.dirt_4.getRegistryName()));
		CoroUtil.proxy.addItemBlock(event, new ItemBlock(DesirePaths.dirt_5).setRegistryName(DesirePaths.dirt_5.getRegistryName()));
		CoroUtil.proxy.addItemBlock(event, new ItemBlock(DesirePaths.dirt_6).setRegistryName(DesirePaths.dirt_6.getRegistryName()));
	}

	public static void addToLookup(Block block, int id) {
		BlockGrassWorn.lookupBlockToStage.put(block, id);
		BlockGrassWorn.lookupStageToBlock.put(id, block);
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

	public void addItemBlock(RegistryEvent.Register<Item> event, Item item) {
		event.getRegistry().register(item);
	}

	public String getNameUnlocalized(String name) {
		return DesirePaths.MODID + "." + name;
	}

	public String getNameDomained(String name) {
		return DesirePaths.MODID + ":" + name;
	}
}
