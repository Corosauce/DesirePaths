package com.corosus.desirepaths;

import CoroUtil.block.BlockBlank;
import CoroUtil.forge.CoroUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	
    public ClientProxy()
    {
    }

    @Override
    public void init()
    {
    	super.init();

        BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
        blockColors.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> BiomeColorHelper.getGrassColorAtPos(worldIn, pos), DesirePaths.dirt_1);
        blockColors.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> BiomeColorHelper.getGrassColorAtPos(worldIn, pos), DesirePaths.dirt_2);
        blockColors.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> BiomeColorHelper.getGrassColorAtPos(worldIn, pos), DesirePaths.dirt_3);
        blockColors.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> BiomeColorHelper.getGrassColorAtPos(worldIn, pos), DesirePaths.dirt_4);
        blockColors.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> BiomeColorHelper.getGrassColorAtPos(worldIn, pos), DesirePaths.dirt_5);
        blockColors.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> BiomeColorHelper.getGrassColorAtPos(worldIn, pos), DesirePaths.dirt_6);
    	
    }

    @Override
    public void addBlock(RegistryEvent.Register<Block> event, Block parBlock, String unlocalizedName) {
        super.addBlock(event, parBlock, unlocalizedName);

        if (!(parBlock instanceof BlockBlank)) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(parBlock), 0, new ModelResourceLocation(DesirePaths.MODID + ":" + unlocalizedName, "inventory"));
        }
    }

    @Override
    public void addItemBlock(RegistryEvent.Register<Item> event, Item item) {
        super.addItemBlock(event, item);

        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
    
    public void registerItem(Item item, int meta, ModelResourceLocation location) {
    	Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, location);
    }
}
