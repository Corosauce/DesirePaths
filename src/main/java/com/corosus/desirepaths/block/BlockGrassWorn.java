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
import com.corosus.desirepaths.config.ConfigDesirePaths;
import com.corosus.desirepaths.config.ConfigDesirePathsDeveloper;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGrassWorn extends Block implements IGrowable
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
        this.setLightOpacity(255);
        this.setHardness(0.5F);
        this.setSoundType(SoundType.GROUND);
    }

    //TODO: what about generic break? dont think its possible still, owell, maybe tick glock grid occasionally to find positions without BlockGrassWorn
    //- or on placement of block, invalidate old data?
    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockDestroyedByPlayer(worldIn, pos, state);

        //remove block data since block is destroyed
        //future proofed for other features by only removing our data and letting internals deside to remove it
        if (!worldIn.isRemote) {
            BlockDataPoint bdp = WorldDirectorManager.instance().getBlockDataGrid(worldIn).getBlockData(pos.getX(), pos.getY(), pos.getZ());

            //in this case it can be null because position is now air and it wasnt ticked or walked on yet
            if (bdp != null) {
                bdp.walkedOnAmount = 0;
                bdp.lastTickTimeGrass = 0;

                WorldDirectorManager.instance().getBlockDataGrid(worldIn).removeBlockDataIfRemovable(pos.getX(), pos.getY(), pos.getZ());
            }
        }
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

    public static void performWearTick(World world, BlockPos pos, float wearAmplifier) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        //check for block that can have beaten path data
        //TODO: switch to a hashmap for faster conversion
        int index = -1;
        if (BlockGrassWorn.lookupBlockToStage.containsKey(block)) {
            index = BlockGrassWorn.lookupBlockToStage.get(block);
        }

        //if invalid or last entry in list
        if (index == -1 || index == BlockGrassWorn.lookupBlockToStage.size() - 1) {
            return;
        }

        BlockDataPoint bdp = WorldDirectorManager.instance().getBlockDataGrid(world).getBlockData(pos.getX(), pos.getY(), pos.getZ());// ServerTickHandler.wd.getBlockDataGrid(world).getBlockData(newX, newY, newZ);

        //add depending on a weight?
        bdp.walkedOnAmount += 0.05F * wearAmplifier;

        //update time since last tick as this would count as a tick
        //moved here so that active walking on it slows down its regrowth, re-enforces path degradation where its often walked on
        bdp.lastTickTimeGrass = world.getTotalWorldTime();

        if (bdp.walkedOnAmount > 1F) {
            BlockGrassWorn.decaySlowly(world, block, pos);

            //reset walked on amount since its a new block state
            //also dont set to full 0 so it doesnt insta revert and block rerender spam due to regrowth code
            bdp.walkedOnAmount = 0.10F;

            //BlockRegistry.dirtPath.blockID);
            //cleanup for memory
            //- maybe pointless here since we are counting lastTickTimeGrass as important data
            WorldDirectorManager.instance().getBlockDataGrid(world).removeBlockDataIfRemovable(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {

        if (!worldIn.isRemote) {
            long curTickTime = worldIn.getTotalWorldTime();
            BlockDataPoint bdp = WorldDirectorManager.instance().getBlockDataGrid(worldIn).getBlockData(pos.getX(), pos.getY(), pos.getZ());
            long lastTickTimeGrass = bdp.lastTickTimeGrass;
            //as soon as any attempts are made we need to mark the time
            //this avoids rapid repair as soon as sun comes up etc
            bdp.lastTickTimeGrass = curTickTime;

            if (ConfigDesirePaths.pathsRepair && worldIn.getLightFromNeighbors(pos.up()) >= 9) {
                for (int i = 0; i < 4; ++i) {
                    BlockPos blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(3) - 1, rand.nextInt(3) - 1);

                    if (blockpos.getY() >= 0 && blockpos.getY() < 256 && !worldIn.isBlockLoaded(blockpos)) {
                        continue;
                    }

                    IBlockState iblockstate1 = worldIn.getBlockState(blockpos);
                    Block block = iblockstate1.getBlock();

                    //if near block is grass or one of our more grass like blocks (but not the more dirt like ones
                    //check for not BlockGrassWorn above grass to work around grass showing up under our blocks
                    if ((block == Blocks.GRASS && !(worldIn.getBlockState(blockpos.up()).getBlock() instanceof BlockGrassWorn))
                            || block == DesirePaths.dirt_1
                            || block == DesirePaths.dirt_2
                            || block == DesirePaths.dirt_3) {

                        //used as a baseline to scale based on time between random ticks
                        long timeSinceLastTick = curTickTime - lastTickTimeGrass;

                        //CULog.dbg("dbg: " + curTickTime + " - " + bdp.lastTickTimeGrass + " = " + timeSinceLastTick);

                        long timeTo1StageOfRegrowth = 20 * 60 * 60 * 3;

                        //make barely worn grass repair faster so lots of seldom walked on areas clear up faster
                        if (state.getBlock() == DesirePaths.dirt_1) {
                            timeTo1StageOfRegrowth = 20 * 60 * 30;
                        } else if (state.getBlock() == DesirePaths.dirt_2) {
                            timeTo1StageOfRegrowth = 20 * 60 * 60;
                        } else if (state.getBlock() == DesirePaths.dirt_3) {
                            timeTo1StageOfRegrowth = 20 * 60 * 60 * 2;
                        }
                        //timeTo1StageOfRegrowth = 1;
                        float oneStageOfRegrowth = 1F;

                        float scale = (float) timeSinceLastTick / (float) timeTo1StageOfRegrowth;

                        float amountToAdjust = oneStageOfRegrowth * scale * (float)Math.max(ConfigDesirePathsDeveloper.pathRepairAmplifier, 0);

                        //code that scales based on ticktime diff goes here
                        bdp.walkedOnAmount -= amountToAdjust;

                        //if (bdp.walkedOnAmount < -1) {
                        //CULog.dbg("decr walk by " + amountToAdjust + " to " + bdp.walkedOnAmount);
                        //}

                        if (bdp.walkedOnAmount <= 0) {

                            //GROW!!!
                            //CULog.dbg("repair grass state - " + amountToAdjust + " to " + bdp.walkedOnAmount);
                            growSlowly(worldIn, state.getBlock(), pos);

                            //reset to nearly full walked on so it has to tick down the value again
                            //but dont se it to 1 or really close so if something walks on it it wont insta wear it down to next state
                            bdp.walkedOnAmount = 0.90F;

                            IBlockState stateNew = worldIn.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));

                            //if it fully regrew
                            if (stateNew.getBlock() == Blocks.GRASS) {

                                //random chance to regrow tallgrass above
                                if (rand.nextInt(5) == 0) {
                                    BlockPos posUp = pos.up();
                                    IBlockState stateUp = worldIn.getBlockState(posUp);
                                    if (stateUp.getMaterial() == Material.AIR) {
                                        if (rand.nextBoolean()) {
                                            worldIn.setBlockState(posUp,
                                                    Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS));
                                        } else {
                                            Blocks.DOUBLE_PLANT.placeAt(worldIn, posUp, BlockDoublePlant.EnumPlantType.GRASS, 3);
                                        }
                                    }
                                }

                                //now that its fully restored to vanilla grass, lets blank out this data too to allow removeBlockDataIfRemovable to do its job
                                bdp.lastTickTimeGrass = 0;

                                //valid only when we are setting it to grass
                                WorldDirectorManager.instance().getBlockDataGrid(worldIn).removeBlockDataIfRemovable(pos.getX(), pos.getY(), pos.getZ());
                            }

                        }

                        break;

                    }
                }
            }


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

                IBlockState stateNext = blockNext.getDefaultState();
                if (blockNext == DesirePaths.dirt_6) {
                    try {
                        String[] str = ConfigDesirePaths.blockFinalWornStage.split(" ");
                        int meta = 0;
                        Block blockNextOverride = Block.getBlockFromName(str[0]);
                        if (blockNextOverride != null) {
                            if (str.length > 1) {
                                meta = Integer.valueOf(str[1]);
                            }
                            stateNext = blockNextOverride.getStateFromMeta(meta);
                        } else {
                            CULog.err("could not parse to block from this string: " + str[0]);
                        }
                    } catch (Exception ex) {
                        CULog.err("incorrect use of blockToSetForFullDirt, " + ex.toString());
                    }

                }

                worldIn.setBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), stateNext);
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

    //wont work for initial grass block but owell
    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (!worldIn.isRemote) {
            performWearTick(worldIn, pos, 10F);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if (playerIn != null && !worldIn.isRemote) {
            ItemStack stack = playerIn.getHeldItem(hand);
            if (playerIn.canPlayerEdit(pos.offset(facing), facing, stack)) {
                if (stack.getItem() instanceof ItemSpade) {
                    worldIn.setBlockState(pos, Blocks.GRASS_PATH.getDefaultState());
                    worldIn.playSound(playerIn, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    stack.damageItem(1, playerIn);
                    return true;
                } else if (stack.getItem() instanceof ItemHoe) {
                    worldIn.setBlockState(pos, Blocks.FARMLAND.getDefaultState());
                    stack.damageItem(1, playerIn);
                    return true;
                }
            }
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        if (super.canSustainPlant(state, world, pos, direction, plantable)) {
            return true;
        }

        if (state.getBlock() == DesirePaths.dirt_1 || state.getBlock() == DesirePaths.dirt_2 || state.getBlock() == DesirePaths.dirt_3)
        {
            //copied from Block.canSustainPlant
            net.minecraftforge.common.EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));
            if (plantType == EnumPlantType.Plains) {
                return true;
            } else if (plantType == EnumPlantType.Beach) {
                return (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
                        world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
                        world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
                        world.getBlockState(pos.south()).getMaterial() == Material.WATER);
            }
        }

        return false;
    }
}