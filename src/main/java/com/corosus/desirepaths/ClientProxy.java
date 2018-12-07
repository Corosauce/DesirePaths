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
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
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
    }

    public void initPost()
    {
        super.initPost();

        BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();

        //old quark non compat way:
        //IBlockColor blockColorizer = (state, worldIn, pos, tintIndex) -> worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D);

        //since Quark mod hacks the biome colorization, i need to hack his hack and use whatever modification he set for the grass block, and also run it in post init, and have it run after quark for good measure:
        //see: https://github.com/Vazkii/Quark/blob/c491ee6fbae2e320325702689cbe67f47e693ba5/src/main/java/vazkii/quark/client/feature/GreenerGrass.java#L69-L81
        java.util.Map<net.minecraftforge.registries.IRegistryDelegate<Block>, IBlockColor> map = ObfuscationReflectionHelper.getPrivateValue(BlockColors.class, blockColors, "blockColorMap");
        IBlockColor blockColorizer = map.get(Blocks.GRASS.delegate);

        //fallback on null just in case
        if (blockColorizer == null) {
            blockColorizer = blockColorizer = (state, worldIn, pos, tintIndex) -> worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D);
        }

        blockColors.registerBlockColorHandler(blockColorizer, DesirePaths.dirt_1, DesirePaths.dirt_2, DesirePaths.dirt_3,
                DesirePaths.dirt_4, DesirePaths.dirt_5, DesirePaths.dirt_6);

        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
        IItemColor itemColorizer = (stack, tintIndex) -> ColorizerGrass.getGrassColor(0.5D, 1.0D);

        itemColors.registerItemColorHandler(itemColorizer, Item.getItemFromBlock(DesirePaths.dirt_1), Item.getItemFromBlock(DesirePaths.dirt_2),
                Item.getItemFromBlock(DesirePaths.dirt_3), Item.getItemFromBlock(DesirePaths.dirt_4),
                Item.getItemFromBlock(DesirePaths.dirt_5), Item.getItemFromBlock(DesirePaths.dirt_6));
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
