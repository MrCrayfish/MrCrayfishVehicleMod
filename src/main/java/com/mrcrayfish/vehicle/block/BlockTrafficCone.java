package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.util.BlockNames;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BlockTrafficCone extends BlockObject
{
    private static final AxisAlignedBB COLLISION_BOX = new AxisAlignedBB(0.125, 0, 0.125, 0.875, 18 * 0.0625, 0.875);
    private static final AxisAlignedBB SELECTION_BOX = new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 16 * 0.0625, 0.9375);

    public BlockTrafficCone()
    {
        super(Material.CLAY, MapColor.ORANGE_STAINED_HARDENED_CLAY, BlockNames.TRAFFIC_CONE);
        this.setHardness(0.5F);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SELECTION_BOX;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BOX);
    }
}
