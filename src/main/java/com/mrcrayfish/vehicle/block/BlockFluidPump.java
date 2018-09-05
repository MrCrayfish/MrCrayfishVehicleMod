package com.mrcrayfish.vehicle.block;

import java.util.List;

import javax.annotation.Nullable;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidPump;

import net.minecraft.block.BlockLever;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

/**
 * Author: MrCrayfish
 */
public class BlockFluidPump extends BlockFluidPipe
{
    private final AxisAlignedBB[][] boxesHousing = new AxisAlignedBB[][]
            {{new AxisAlignedBB(0.1875, 0, 0.1875, 0.8125, 0.1875, 0.8125),  new AxisAlignedBB(0.28125, 0.1875, 0.28125, 0.71875, 0.25, 0.71875)},
            {new AxisAlignedBB(0.1875, 1, 0.1875, 0.8125, 0.8125, 0.8125),  new AxisAlignedBB(0.28125, 0.8125, 0.28125, 0.71875, 0.75, 0.71875)},
            {new AxisAlignedBB(0.1875, 0.1875, 0, 0.8125, 0.8125, 0.1875),  new AxisAlignedBB(0.28125, 0.28125, 0.1875, 0.71875, 0.71875, 0.25)},
            {new AxisAlignedBB(0.1875, 0.1875, 1, 0.8125, 0.8125, 0.8125),  new AxisAlignedBB(0.28125, 0.28125, 0.8125, 0.71875, 0.71875, 0.75)},
            {new AxisAlignedBB(0, 0.1875, 0.1875, 0.1875, 0.8125, 0.8125),  new AxisAlignedBB(0.1875, 0.28125, 0.28125, 0.25, 0.71875, 0.71875)},
            {new AxisAlignedBB(1, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125),  new AxisAlignedBB(0.8125, 0.28125, 0.28125, 0.75, 0.71875, 0.71875)}};

    public BlockFluidPump()
    {
        super("fluid_pump");
    }

    @Override
    protected EnumFacing getCollisionFacing(IBlockState state)
    {
        return state.getValue(FACING).getOpposite();
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState)
    {
        super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, isActualState);
        for (AxisAlignedBB box : boxesHousing[getCollisionFacing(isActualState ? state : state.getActualState(world, pos)).getIndex()])
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        double minX = 5 * 0.0625;
        double minY = 3 * 0.0625;
        double minZ = 5 * 0.0625;
        double maxX = 11 * 0.0625;
        double maxY = 13 * 0.0625;
        double maxZ = 11 * 0.0625;

        state = this.getActualState(state, source, pos);
        EnumFacing originalFacing = state.getValue(FACING);
        switch(originalFacing)
        {
            case DOWN:
                minY = 5 * 0.0625;
                maxY = 1;
                break;
            case UP:
                minY = 0;
                maxY = 11 * 0.0625;
                break;
            case NORTH:
                maxZ = 1.0;
                break;
            case SOUTH:
                minZ = 0;
                break;
            case WEST:
                maxX = 1.0;
                break;
            case EAST:
                minX = 0;
                break;
        }
        switch(originalFacing.getAxis())
        {
            case Y:
                maxX = maxZ = 13 * 0.0625;
                minX = minZ = 3 * 0.0625;
                break;
            case Z:
                minX = 3 * 0.0625;
                maxX = 13 * 0.0625;
                break;
            case X:
                minZ = 3 * 0.0625;
                maxZ = 13 * 0.0625;
                break;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.NORTH.getIndex()]))
        {
            minZ = 0.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.EAST.getIndex()]))
        {
            maxX = 1.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.SOUTH.getIndex()]))
        {
            maxZ = 1.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.WEST.getIndex()]))
        {
            minX = 0.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.DOWN.getIndex()]))
        {
            minY = 0.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.UP.getIndex()]))
        {
            maxY = 1.0F;
        }

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        EnumFacing originalFacing = state.getValue(FACING).getOpposite();
        for(EnumFacing facing : EnumFacing.VALUES)
        {
            if(facing == originalFacing)
                continue;

            BlockPos adjacentPos = pos.offset(facing);
            IBlockState adjacentState = worldIn.getBlockState(adjacentPos);
            if(adjacentState.getBlock() == ModBlocks.FLUID_PIPE)
            {
                state = state.withProperty(CONNECTED_PIPES[facing.getIndex()], true);
            }
            else if(adjacentState.getBlock() == Blocks.LEVER)
            {
                EnumFacing leverFacing = adjacentState.getValue(BlockLever.FACING).getFacing().getOpposite();
                if(adjacentPos.offset(leverFacing).equals(pos))
                {
                    state = state.withProperty(CONNECTED_PIPES[facing.getIndex()], true);
                }
            }
            else if(adjacentState.getBlock() != this)
            {
                TileEntity tileEntity = worldIn.getTileEntity(adjacentPos);
                if(tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()))
                {
                    state = state.withProperty(CONNECTED_PIPES[facing.getIndex()], true);
                }
            }
        }
        return state;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
        return state.withProperty(FACING, facing);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityFluidPump();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.SOLID;
    }
}
