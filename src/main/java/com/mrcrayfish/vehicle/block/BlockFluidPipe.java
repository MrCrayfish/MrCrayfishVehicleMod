package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.tileentity.TileEntityFluidPipe;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockFluidPipe extends BlockObject
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool[] CONNECTED_PIPES;

    static
    {
        CONNECTED_PIPES = new PropertyBool[EnumFacing.values().length];
        for(EnumFacing facing : EnumFacing.values())
        {
            CONNECTED_PIPES[facing.getIndex()] = PropertyBool.create("pipe_" + facing.getName());
        }
    }

    public BlockFluidPipe()
    {
        super(Material.IRON, "fluid_pipe");
        IBlockState defaultState = this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH);
        for(EnumFacing facing : EnumFacing.values())
        {
            defaultState = defaultState.withProperty(CONNECTED_PIPES[facing.getIndex()], false);
        }
        this.setDefaultState(defaultState);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        EnumFacing originalFacing = state.getValue(FACING);
        for(EnumFacing facing : EnumFacing.VALUES)
        {
            if(facing == originalFacing)
                continue;

            BlockPos adjacentPos = pos.offset(facing);
            IBlockState adjacentState = worldIn.getBlockState(adjacentPos);
            if(adjacentState.getBlock() == this)
            {
                EnumFacing adjacentFacing = adjacentState.getValue(FACING);
                if(adjacentPos.offset(adjacentFacing).equals(pos))
                {
                    state = state.withProperty(CONNECTED_PIPES[facing.getIndex()], true);
                }
            }
        }
        /*EnumFacing facing = originalFacing;
        for(int i = 0; i < 3; i++)
        {
            facing = facing.rotateY();
            for(int j = 0; j < originalFacing.getHorizontalIndex(); j++)
            {
                facing = facing.rotateYCCW();
            }
            BlockPos adjacentPos = pos.offset(facing);
            IBlockState adjacentState = worldIn.getBlockState(adjacentPos);
            if(adjacentState.getBlock() == this)
            {
                EnumFacing adjacentFacing = adjacentState.getValue(FACING);
                if(adjacentPos.offset(adjacentFacing).equals(pos))
                {
                    state = state.withProperty(CONNECTED_PIPES[facing.getIndex()], true);
                }
            }
        }*/
        return state;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
        return state.withProperty(FACING, facing.getOpposite());
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
        builder.add(FACING);
        builder.add(CONNECTED_PIPES);
        return builder.build();
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
        return new TileEntityFluidPipe();
    }
}
