package com.corosus.desirepaths.ai;

import com.corosus.desirepaths.DesirePaths;
import com.corosus.desirepaths.block.BlockGrassWorn;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class EntityAIEatGrassExtended extends EntityAIEatGrass
{

    public static final Predicate<IBlockState> IS_TALL_GRASS = BlockStateMatcher.forBlock(Blocks.TALLGRASS).where(BlockTallGrass.TYPE, Predicates.equalTo(BlockTallGrass.EnumType.GRASS));

    public EntityAIEatGrassExtended(EntityLiving grassEaterEntityIn)
    {
        super(grassEaterEntityIn);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.grassEaterEntity.getRNG().nextInt(this.grassEaterEntity.isChild() ? 50 : 1000) != 0) {
            return false;
        }
        else {
            BlockPos blockpos = new BlockPos(this.grassEaterEntity.posX, this.grassEaterEntity.posY, this.grassEaterEntity.posZ);

            if (IS_TALL_GRASS.apply(this.entityWorld.getBlockState(blockpos)))
            {
                return true;
            }
            else
            {
                BlockPos blockpos1 = blockpos.down();
                if (this.entityWorld.getBlockState(blockpos1).getBlock() == Blocks.GRASS) {
                    return true;
                } else {
                    Block block = this.entityWorld.getBlockState(blockpos1).getBlock();
                    if (block instanceof BlockGrassWorn && block != DesirePaths.dirt_6) {
                        return true;
                    }
                }
            }


        }

        return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        super.startExecuting();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        super.resetTask();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return super.shouldContinueExecuting();
    }

    /**
     * Number of ticks since the entity started to eat grass
     */
    public int getEatingGrassTimer()
    {
        return super.getEatingGrassTimer();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        this.eatingGrassTimer = Math.max(0, this.eatingGrassTimer - 1);

        if (this.eatingGrassTimer == 4)
        {
            BlockPos blockpos = new BlockPos(this.grassEaterEntity.posX, this.grassEaterEntity.posY, this.grassEaterEntity.posZ);

            if (IS_TALL_GRASS.apply(this.entityWorld.getBlockState(blockpos)))
            {
                if (this.entityWorld.getGameRules().getBoolean("mobGriefing"))
                {
                    this.entityWorld.destroyBlock(blockpos, false);
                }

                this.grassEaterEntity.eatGrassBonus();
            }
            else
            {
                BlockPos blockpos1 = blockpos.down();

                if (this.entityWorld.getBlockState(blockpos1).getBlock() == Blocks.GRASS)
                {
                    if (this.entityWorld.getGameRules().getBoolean("mobGriefing"))
                    {
                        this.entityWorld.playEvent(2001, blockpos1, Block.getIdFromBlock(Blocks.GRASS));
                        //this.entityWorld.setBlockState(blockpos1, Blocks.DIRT.getDefaultState(), 2);
                        BlockGrassWorn.performWearTick(entityWorld, blockpos1, 20F);
                    }

                    this.grassEaterEntity.eatGrassBonus();
                } else {
                    Block block = this.entityWorld.getBlockState(blockpos1).getBlock();

                    //if our block but not the full dirt stage
                    if (block instanceof BlockGrassWorn && block != DesirePaths.dirt_6)
                    {
                        if (this.entityWorld.getGameRules().getBoolean("mobGriefing"))
                        {
                            this.entityWorld.playEvent(2001, blockpos1, Block.getIdFromBlock(Blocks.GRASS));
                            //this.entityWorld.setBlockState(blockpos1, Blocks.DIRT.getDefaultState(), 2);
                            //20 = 1 full state wear
                            BlockGrassWorn.performWearTick(entityWorld, blockpos1, 20F);
                        }

                        this.grassEaterEntity.eatGrassBonus();
                    }
                }
            }
        }
    }
}