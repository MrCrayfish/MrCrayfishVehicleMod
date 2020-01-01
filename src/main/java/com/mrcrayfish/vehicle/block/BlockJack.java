package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.tileentity.JackTileEntity;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockJack extends BlockObject
{
    private static final VoxelShape SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 9, 16);

    public BlockJack()
    {
        super(Names.Block.JACK, Block.Properties.create(Material.PISTON));
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity instanceof JackTileEntity)
        {
            JackTileEntity jack = (JackTileEntity) tileEntity;
            return VoxelShapes.create(SHAPE.getBoundingBox().expand(0, 0.5 * jack.getProgress(), 0));
        }
        return SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new JackTileEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
