package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.tileentity.FluidPipeTileEntity;
import com.mrcrayfish.vehicle.tileentity.FluidPumpTileEntity;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockFluidPump extends BlockFluidPipe
{
    //TODO add collisions
    private final AxisAlignedBB[][] boxesHousing = new AxisAlignedBB[][]{{new AxisAlignedBB(0.1875, 0, 0.1875, 0.8125, 0.1875, 0.8125), new AxisAlignedBB(0.28125, 0.1875, 0.28125, 0.71875, 0.25, 0.71875)}, {new AxisAlignedBB(0.1875, 1, 0.1875, 0.8125, 0.8125, 0.8125), new AxisAlignedBB(0.28125, 0.8125, 0.28125, 0.71875, 0.75, 0.71875)}, {new AxisAlignedBB(0.1875, 0.1875, 0, 0.8125, 0.8125, 0.1875), new AxisAlignedBB(0.28125, 0.28125, 0.1875, 0.71875, 0.71875, 0.25)}, {new AxisAlignedBB(0.1875, 0.1875, 1, 0.8125, 0.8125, 0.8125), new AxisAlignedBB(0.28125, 0.28125, 0.8125, 0.71875, 0.71875, 0.75)}, {new AxisAlignedBB(0, 0.1875, 0.1875, 0.1875, 0.8125, 0.8125), new AxisAlignedBB(0.1875, 0.28125, 0.28125, 0.25, 0.71875, 0.71875)}, {new AxisAlignedBB(1, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125), new AxisAlignedBB(0.8125, 0.28125, 0.28125, 0.75, 0.71875, 0.71875)}};

    public BlockFluidPump()
    {
        super(Names.Block.FLUID_PUMP);
    }

    @Override
    protected Direction getCollisionFacing(BlockState state)
    {
        return state.get(DIRECTION).getOpposite();
    }

    @Override
    public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        if(super.func_225533_a_(state, world, pos, player, hand, result) == ActionResultType.SUCCESS)
        {
            return ActionResultType.SUCCESS;
        }
        FluidPipeTileEntity pipe = getPipeTileEntity(world, pos);
        AxisAlignedBB housingBox = this.getHousingBox(pos, state, player, hand, result.getHitVec(), pipe);
        if(pipe != null && housingBox != null)
        {
            if(!world.isRemote)
            {
                ((FluidPumpTileEntity) pipe).cyclePowerMode(player);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Nullable
    public AxisAlignedBB getHousingBox(BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Vec3d hitVec, @Nullable FluidPipeTileEntity pipe)
    {
        if(!(pipe instanceof FluidPumpTileEntity) || player.getHeldItem(hand).getItem() != ModItems.WRENCH)
        {
            return null;
        }

        AxisAlignedBB[] boxesHousing = this.boxesHousing[getCollisionFacing(state).getIndex()];
        for(AxisAlignedBB box : boxesHousing)
        {
            if(box.grow(0.001).contains(hitVec))
            {
                for(AxisAlignedBB box2 : boxesHousing)
                {
                    box = box.union(box2);
                }
                return box.offset(pos);
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
        BlockState state = super.getStateForPlacement(context);
        return this.getPumpState(context.getWorld(), context.getPos(), state, context.getFace());
    }

    private BlockState getPumpState(IWorld world, BlockPos pos, BlockState state, Direction originalFacing)
    {
        FluidPipeTileEntity pipe = getPipeTileEntity(world, pos);
        boolean[] disabledConnections = FluidPipeTileEntity.getDisabledConnections(pipe);
        for(Direction facing : Direction.values())
        {
            if(facing == originalFacing) continue;

            BlockPos adjacentPos = pos.offset(facing);
            BlockState adjacentState = world.getBlockState(adjacentPos);
            boolean enabled = !disabledConnections[facing.getIndex()];
            if(adjacentState.getBlock() == ModBlocks.FLUID_PIPE)
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
