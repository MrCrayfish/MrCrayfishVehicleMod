package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.block.BlockBoostRamp;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;

/**
 * Author: MrCrayfish
 */
public class ItemBoostRamp extends BlockItem
{
    public ItemBoostRamp(Block block)
    {
        super(block, new Item.Properties().group(VehicleMod.CREATIVE_TAB));
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context)
    {
        if(context.getFace() == Direction.UP)
        {
            BlockState state = context.getWorld().getBlockState(context.getPos());
            Block block = state.getBlock();
            if(block instanceof BlockBoostRamp)
            {
                if(!state.get(BlockBoostRamp.STACKED))
                {
                    context.getWorld().setBlockState(context.getPos(), block.getDefaultState().with(BlockBoostRamp.DIRECTION, state.get(BlockBoostRamp.DIRECTION)).with(BlockBoostRamp.STACKED, true));
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.onItemUseFirst(stack, context);
    }
}
