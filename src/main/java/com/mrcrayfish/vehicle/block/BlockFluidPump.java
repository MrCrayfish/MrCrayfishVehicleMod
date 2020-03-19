package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.tileentity.FluidPipeTileEntity;
import com.mrcrayfish.vehicle.tileentity.FluidPumpTileEntity;
import com.mrcrayfish.vehicle.util.Names;
import com.mrcrayfish.vehicle.util.VoxelShapeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BlockFluidPump extends BlockFluidPipe
{
    //TODO add collisions
    private final VoxelShape[][] PUMP_BOX = new VoxelShape[][]{
            {Block.makeCuboidShape(3, 0, 3, 13, 3, 13), Block.makeCuboidShape(4.5, 3, 4.5, 11.5, 4, 11.5)},
            {Block.makeCuboidShape(3, 16, 3, 13, 13, 13), Block.makeCuboidShape(4.5, 13, 4.5, 11.5, 12, 11.5)},
            {Block.makeCuboidShape(3, 3, 0, 13, 13, 3), Block.makeCuboidShape(4.5, 4.5, 3, 11.5, 11.5, 4)},
            {Block.makeCuboidShape(3, 3, 16, 13, 13, 13), Block.makeCuboidShape(4.5, 4.5, 13, 11.5, 11.5, 12)},
            {Block.makeCuboidShape(0, 3, 3, 3, 13, 13), Block.makeCuboidShape(3, 4.5, 4.5, 4, 11.5, 11.5)},
            {Block.makeCuboidShape(16, 3, 3, 13, 13, 13), Block.makeCuboidShape(13, 4.5, 4.5, 12, 11.5, 11.5)}
    };

    @Override
    protected VoxelShape getPipeShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        List<VoxelShape> shapes = new ArrayList<>();
        shapes.add(super.getPipeShape(state, worldIn, pos));
        Collections.addAll(shapes, PUMP_BOX[this.getCollisionFacing(state).getIndex()]);
        return VoxelShapeHelper.combineAll(shapes);
    }

    @Override
    protected Direction getCollisionFacing(BlockState state)
    {
        return state.get(DIRECTION).getOpposite();
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        if(super.onBlockActivated(state, world, pos, player, hand, result))
        {
            return true;
        }
        FluidPipeTileEntity pipe = getPipeTileEntity(world, pos);
        AxisAlignedBB housingBox = this.getHousingBox(pos, state, player, hand, result.getHitVec().add(-pos.getX(), -pos.getY(), -pos.getZ()), pipe);
        if(pipe != null && housingBox != null)
        {
            if(!world.isRemote)
            {
                ((FluidPumpTileEntity) pipe).cyclePowerMode(player);
                world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1.0F, 0.5F);
            }
            return true;
        }
        return true;
    }

    @Nullable
    public AxisAlignedBB getHousingBox(BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Vec3d hitVec, @Nullable FluidPipeTileEntity pipe)
    {
        if(!(pipe instanceof FluidPumpTileEntity) || player.getHeldItem(hand).getItem() != ModItems.WRENCH.get())
        {
            return null;
        }

        VoxelShape[] boxesHousing = this.PUMP_BOX[getCollisionFacing(state).getIndex()];
        for(VoxelShape box : boxesHousing)
        {
            AxisAlignedBB boundingBox = box.getBoundingBox();
            if(boundingBox.grow(0.001).contains(hitVec))
            {
                for(VoxelShape box2 : boxesHousing)
                {
                    boundingBox = boundingBox.union(box2.getBoundingBox());
                }
                return boundingBox.offset(pos);
            }
        }
        return null;
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState neighbourState, IWorld world, BlockPos pos, BlockPos neighbourPos)
    {
        return this.getPumpState(world, pos, state, state.get(DIRECTION));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = super.getStateForPlacement(context).with(DIRECTION, context.getFace());
        return this.getPumpState(context.getWorld(), context.getPos(), state, context.getFace());
    }

    private BlockState getPumpState(IWorld world, BlockPos pos, BlockState state, Direction originalFacing)
    {
        FluidPipeTileEntity pipe = getPipeTileEntity(world, pos);
        boolean[] disabledConnections = FluidPipeTileEntity.getDisabledConnections(pipe);
        for(Direction facing : Direction.values())
        {
            if(facing == originalFacing.getOpposite()) continue;

            state = state.with(CONNECTED_PIPES[facing.getIndex()], false);

            BlockPos adjacentPos = pos.offset(facing);
            BlockState adjacentState = world.getBlockState(adjacentPos);
            boolean enabled = !disabledConnections[facing.getIndex()];
            if(adjacentState.getBlock() == ModBlocks.FLUID_PIPE.get())
            {
                state = state.with(CONNECTED_PIPES[facing.getIndex()], enabled);
            }
            else if(adjacentState.getBlock() == Blocks.LEVER)
            {
                Direction leverFacing = adjacentState.get(LeverBlock.HORIZONTAL_FACING).getOpposite();
                if(adjacentPos.offset(leverFacing).equals(pos))
                {
                    state = state.with(CONNECTED_PIPES[facing.getIndex()], true);
                    if(pipe != null)
                    {
                        pipe.setConnectionDisabled(facing, false);
                    }
                }
            }
            else if(adjacentState.getBlock() != this)
            {
                TileEntity tileEntity = world.getTileEntity(adjacentPos);
                state = state.with(CONNECTED_PIPES[facing.getIndex()], enabled && tileEntity != null && tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()).isPresent());
            }
        }
        return state;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new FluidPumpTileEntity();
    }
}
