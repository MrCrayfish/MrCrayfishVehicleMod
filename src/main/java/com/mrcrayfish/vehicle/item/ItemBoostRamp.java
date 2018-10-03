package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.block.BlockBoostRamp;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class ItemBoostRamp extends ItemBlock
{
    public ItemBoostRamp(Block block)
    {
        super(block);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        if(side == EnumFacing.UP)
        {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if(block instanceof BlockBoostRamp)
            {
                if(!state.getValue(BlockBoostRamp.STACKED))
                {
                    world.setBlockState(pos, block.getDefaultState().withProperty(BlockBoostRamp.FACING, state.getValue(BlockBoostRamp.FACING)).withProperty(BlockBoostRamp.STACKED, true));
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
}
