package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.block.BoostRampBlock;
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
        super(block, new Item.Properties().tab(VehicleMod.CREATIVE_TAB));
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context)
    {
        if(context.getClickedFace() == Direction.UP)
        {
            BlockState state = context.getLevel().getBlockState(context.getClickedPos());
            Block block = state.getBlock();
            if(block instanceof BoostRampBlock)
            {
                if(!state.getValue(BoostRampBlock.STACKED))
                {
                    context.getLevel().setBlockAndUpdate(context.getClickedPos(), block.defaultBlockState().setValue(BoostRampBlock.DIRECTION, state.getValue(BoostRampBlock.DIRECTION)).setValue(BoostRampBlock.STACKED, true));
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.onItemUseFirst(stack, context);
    }
}
