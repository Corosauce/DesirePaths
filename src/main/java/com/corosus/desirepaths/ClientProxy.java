package com.corosus.desirepaths;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

    	//dont break dediserver
    	Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor)DesirePaths.dirt_1, DesirePaths.dirt_1);
    	Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor)DesirePaths.dirt_2, DesirePaths.dirt_2);
    	Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor)DesirePaths.dirt_3, DesirePaths.dirt_3);
    	Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor)DesirePaths.dirt_4, DesirePaths.dirt_4);
    	Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor)DesirePaths.dirt_5, DesirePaths.dirt_5);
    	Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor)DesirePaths.dirt_6, DesirePaths.dirt_6);
    	
    }
    
    @Override
    public void addBlock(Block parBlock, String unlocalizedName, String blockNameBase) {
    	super.addBlock(parBlock, unlocalizedName, blockNameBase);
    	
    	registerItem(Item.getItemFromBlock(parBlock), 0, new ModelResourceLocation(DesirePaths.MODID + ":" + unlocalizedName, "inventory"));
    }
    
    public void registerItem(Item item, int meta, ModelResourceLocation location) {
    	Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, location);
    }
}
