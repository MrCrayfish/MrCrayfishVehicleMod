package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidPipe;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidPump;
import com.mrcrayfish.vehicle.util.BlockNames;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

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
        super(BlockNames.FLUID_PUMP);
        this.setHardness(0.5F);
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

        boolean[] disabledConnections = TileEntityFluidPipe.getDisabledConnections(getTileEntity(source, pos));

        if(state.getValue(CONNECTED_PIPES[EnumFacing.NORTH.getIndex()]) && !disabledConnections[EnumFacing.NORTH.getIndex()])
        {
            minZ = 0.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.EAST.getIndex()]) && !disabledConnections[EnumFacing.EAST.getIndex()])
        {
            maxX = 1.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.SOUTH.getIndex()]) && !disabledConnections[EnumFacing.SOUTH.getIndex()])
        {
            maxZ = 1.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.WEST.getIndex()]) && !disabledConnections[EnumFacing.WEST.getIndex()])
        {
            minX = 0.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.DOWN.getIndex()]) && !disabledConnections[EnumFacing.DOWN.getIndex()])
        {
            minY = 0.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.UP.getIndex()]) && !disabledConnections[EnumFacing.UP.getIndex()])
        {
            maxY = 1.0F;
        }

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ))
        {
            return true;
        }
        TileEntityFluidPipe pipe = getTileEntity(world, pos);
        AxisAlignedBB housingBox = getHousingBox(world, pos, state, player, hand, hitX, hitY, hitZ, pipe);
        if (pipe != null && housingBox != null)
        {
            if (!world.isRemote)
            {
                ((TileEntityFluidPump) pipe).cyclePowerMode(player);
                world.scheduleUpdate(pos, state.getBlock(), 0);
                world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1.0F, 0.5F);
            }
            return true;
        }
        return false;
    }

    @Nullable
    public AxisAlignedBB getHousingBox(World world, BlockPos pos, IBlockState state, EntityPlayer player,
            EnumHand hand, double hitX, double hitY, double hitZ, @Nullable TileEntityFluidPipe pipe)
    {
        if (!(pipe instanceof TileEntityFluidPump) || !(player.getHeldItem(hand).getItem() == ModItems.WRENCH))
        {
            return null;
        }
        state = state.getActualState(world, pos);
        Vec3d hit = new Vec3d(hitX, hitY, hitZ);
        AxisAlignedBB[] boxesHousing = this.boxesHousing[getCollisionFacing(state).getIndex()];
        for (AxisAlignedBB box : boxesHousing)
        {
            if (box.grow(0.001).contains(hit))
            {
                for (AxisAlignedBB box2 : boxesHousing)
                {
                    box = box.union(box2);
                }
                return box.offset(pos);
            }
        }
        return null;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        EnumFacing originalFacing = state.getValue(FACING).getOpposite();
        TileEntityFluidPipe pipe = getTileEntity(world, pos);
        boolean[] disabledConnections = TileEntityFluidPipe.getDisabledConnections(pipe);
        for(EnumFacing facing : EnumFacing.VALUES)
        {
            if(facing == originalFacing)
                continue;

            BlockPos adjacentPos = pos.offset(facing);
            IBlockState adjacentState = world.getBlockState(adjacentPos);
            boolean enabled = !disabledConnections[facing.getIndex()];
            if(adjacentState.getBlock() == ModBlocks.FLUID_PIPE)
            {
                state = state.withProperty(CONNECTED_PIPES[facing.getIndex()], enabled);
            }
            else if(adjacentState.getBlock() == Blocks.LEVER)
            {
                EnumFacing leverFacing = adjacentState.getValue(BlockLever.FACING).getFacing().getOpposite();
                if(adjacentPos.offset(leverFacing).equals(pos))
                {
                    state = state.withProperty(CONNECTED_PIPES[facing.getIndex()], true);
                    if (pipe != null)
                    {
                        pipe.setConnectionDisabled(facing, false);
                    }
                }
            }
            else if(adjacentState.getBlock() != this)
            {
                TileEntity tileEntity = world.getTileEntity(adjacentPos);
                state = state.withProperty(CONNECTED_PIPES[facing.getIndex()], enabled && tileEntity != null
                        && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()));
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
