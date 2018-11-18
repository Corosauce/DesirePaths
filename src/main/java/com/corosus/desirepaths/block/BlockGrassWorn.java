package com.corosus.desirepaths.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import CoroUtil.forge.CULog;
import CoroUtil.world.WorldDirectorManager;
import CoroUtil.world.grid.block.BlockDataPoint;
import com.corosus.desirepaths.DesirePaths;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGrassWorn extends Block implements IGrowable, IBlockColor
{
    public static List<Block> listDegradeProgression = new ArrayList<>();

    //stage 0 = full grass, last stage = no grass
    public static HashMap<Integer, Block> lookupStageToBlock = new HashMap<>();
    public static HashMap<Block, Integer> lookupBlockToStage = new HashMap<>();

    public BlockGrassWorn()
    {
        super(Material.GRASS);
        this.setDefaultState(this.blockState.getBaseState());
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    /*public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        Block block = worldIn.getBlockState(pos.up()).getBlock();
        return state.withProperty(SNOWY, Boolean.valueOf(block == Blocks.SNOW || block == Blocks.SNOW_LAYER));
    }*/

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {

        //CULog.dbg("rand tick");

        BlockDataPoint bdp = WorldDirectorManager.instance().getBlockDataGrid(worldIn).getBlockData(pos.getX(), pos.getY(), pos.getZ());
        //bdp.tickUpdate();
        long curTickTime = worldIn.getTotalWorldTime();

        //used as a baseline to scale based on time between random ticks
        long timeSinceLastTick = curTickTime - bdp.lastTickTimeGrass;

        //CULog.dbg("dbg: " + curTickTime + " - " + bdp.lastTickTimeGrass + " = " + timeSinceLastTick);

        long timeTo1StageOfRegrowth = 20*60*30;
        float oneStageOfRegrowth = 1F;

        float scale = (float)timeSinceLastTick / (float)timeTo1StageOfRegrowth;

        float amountToAdjust = oneStageOfRegrowth * scale;

        //code that scales based on ticktime diff goes here
        bdp.walkedOnAmount -= amountToAdjust;


        //if (bdp.walkedOnAmount < -1) {
            CULog.dbg("decr walk by " + amountToAdjust + " to " + bdp.walkedOnAmount);
        //}

        if (bdp.walkedOnAmount <= 0) {

            //GROW!!!
            growSlowly(worldIn, state.getBlock(), pos);

            bdp.walkedOnAmount = 0;

            IBlockState stateNew = worldIn.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));

            //if it fully regrew
            if (stateNew.getBlock() == Blocks.GRASS) {

                //random chance to regrow tallgrass above
                if (rand.nextInt(5) == 0) {
                    IBlockState stateUp = worldIn.getBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()));
                    if (stateUp.getMaterial() == Material.AIR) {
                        worldIn.setBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()), Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS));
                    }
                }

                //now that its fully restored to vanilla grass, lets blank out this data too to allow removeBlockDataIfRemovable to do its job
                bdp.lastTickTimeGrass = 0;

                //valid only when we are setting it to grass
                WorldDirectorManager.instance().getBlockDataGrid(worldIn).removeBlockDataIfRemovable(pos.getX(), pos.getY(), pos.getZ());
            } else {
                bdp.lastTickTimeGrass = curTickTime;
            }

        } else {
            bdp.lastTickTimeGrass = curTickTime;
        }

    }

    /**
     * Progress to next stage of growth to show more grass in spot ending in vanilla grass
     *
     * named growSlowly to differenciate from vanilla grow which bonemeal uses to insta restore
     *
     * @param worldIn
     * @param pos
     */
    public static void growSlowly(World worldIn, Block blockFrom, BlockPos pos) {
        //maybe random chance to grow tallgrass when it restores to full grass?
        //- compensates for us removing tallgrass and plants etc on trample

        int stage = -1;
        if (lookupBlockToStage.containsKey(blockFrom)) {
            stage = lookupBlockToStage.get(blockFrom);
        }
        if (stage != -1) {
            Block blockNext = lookupStageToBlock.get(stage - 1);
            if (blockNext != null) {
                //CULog.dbg("growed by a stage");
                worldIn.setBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), blockNext.getDefaultState());
            }

        }
    }

    /**
     * Decay 1 stage to show less grass, ending in our custom dirt
     *
     * @param worldIn
     * @param pos
     */
    public static void decaySlowly(World worldIn, Block blockFrom, BlockPos pos) {

        int stage = -1;
        if (lookupBlockToStage.containsKey(blockFrom)) {
            stage = lookupBlockToStage.get(blockFrom);
        }
        if (stage != -1) {
            Block blockNext = lookupStageToBlock.get(stage + 1);
            if (blockNext != null) {
                worldIn.setBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), blockNext.getDefaultState());
                //if mostly trampled, remove plant above
                if (stage >= 4) {
                    IBlockState stateUp = worldIn.getBlockState(new BlockPos(pos.getX(), pos.getY()+1, pos.getZ()));
                    if (stateUp.getMaterial() == Material.PLANTS || stateUp.getMaterial() == Material.VINE) {
                        //maybe let it "pop off"?
                        worldIn.setBlockState(new BlockPos(pos.getX(), pos.getY()+1, pos.getZ()), Blocks.AIR.getDefaultState());
                    }
                }
            }

        }

    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Nullable
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Blocks.DIRT.getItemDropped(Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), rand, fortune);
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return true;
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        worldIn.setBlockState(pos, Blocks.GRASS.getDefaultState());
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

	@Override
	public int colorMultiplier(IBlockState state, IBlockAccess worldIn,
			BlockPos pos, int tintIndex) {
		try {
			return BiomeColorHelper.getGrassColorAtPos(worldIn, pos);
		} catch (Exception e) {
			// TODO: fix exception for NPE at "at net.minecraft.world.biome.BiomeColorHelper.getColorAtPos(BiomeColorHelper.java:39)"
			//reproduce by jump landing on block
			e.printStackTrace();
			return 0;
		}
		
	}

    /*protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {SNOWY});
    }*/


}