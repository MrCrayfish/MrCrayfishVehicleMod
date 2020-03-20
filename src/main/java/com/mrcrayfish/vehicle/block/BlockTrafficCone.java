package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

/**
 * Author: MrCrayfish
 */
public class BlockTrafficCone extends BlockObject
{
    private static final VoxelShape COLLISION_SHAPE = Block.makeCuboidShape(2, 0, 2, 14, 18, 14);
    private static final VoxelShape SELECTION_SHAPE = Block.makeCuboidShape(1, 0, 1, 15, 16, 15);

    public BlockTrafficCone()
    {
        super(Block.Properties.create(Material.CLAY, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(0.5F));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SELECTION_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return COLLISION_SHAPE;
    }
}
