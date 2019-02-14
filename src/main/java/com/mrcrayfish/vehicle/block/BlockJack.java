package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.tileentity.TileEntityJack;
import javafx.scene.chart.Axis;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockJack extends BlockObject
{
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 1, 0.5625, 1);

    public BlockJack()
    {
        super(Material.PISTON, "jack");
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TileEntity tileEntity = source.getTileEntity(pos);
        if(tileEntity instanceof TileEntityJack)
        {
            TileEntityJack tileEntityJack = (TileEntityJack) tileEntity;
            float progress = tileEntityJack.liftProgress / (float) TileEntityJack.MAX_LIFT_PROGRESS;
            return BOUNDING_BOX.expand(0, 0.5 * progress, 0);
        }
        return BOUNDING_BOX;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityJack();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
