package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.tileentity.PipeTileEntity;
import com.mrcrayfish.vehicle.tileentity.PumpTileEntity;
import com.mrcrayfish.vehicle.util.VoxelShapeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class FluidPumpBlock extends FluidPipeBlock
{
    public static final DirectionProperty DIRECTION = BlockStateProperties.FACING;

    public static final VoxelShape[] PUMP_BOX = new VoxelShape[]{
            Block.box(3, 0, 3, 13, 4, 13),
            Block.box(3, 12, 3, 13, 16, 13),
            Block.box(3, 3, 0, 13, 13, 4),
            Block.box(3, 3, 12, 13, 13, 16),
            Block.box(0, 3, 3, 4, 13, 13),
            Block.box(12, 3, 3, 16, 13, 13)
    };

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.getPumpShape(state, worldIn, pos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.getPumpShape(state, worldIn, pos);
    }

    protected VoxelShape getPumpShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        List<VoxelShape> shapes = new ArrayList<>();
        shapes.add(super.getPipeShape(state, worldIn, pos));
        shapes.add(PUMP_BOX[this.getCollisionFacing(state).get3DDataValue()]);
        return VoxelShapeHelper.combineAll(shapes);
    }

    protected Direction getCollisionFacing(BlockState state)
    {
        return state.getValue(DIRECTION).getOpposite();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        if(super.use(state, world, pos, player, hand, result) == ActionResultType.SUCCESS)
        {
            return ActionResultType.SUCCESS;
        }

        if(!world.isClientSide())
        {
            PipeTileEntity tileEntity = getPipeTileEntity(world, pos);
            if(tileEntity instanceof PumpTileEntity)
            {
                PumpTileEntity pumpTileEntity = (PumpTileEntity) tileEntity;

                /*if(!FMLLoader.isProduction())
                {
                    pumpTileEntity.invalidatePipeNetwork();
                }*/

                Vector3d localHitVec = result.getLocation().add(-pos.getX(), -pos.getY(), -pos.getZ());
                if(player.getItemInHand(hand).getItem() == ModItems.WRENCH.get() && this.isLookingAtHousing(state, localHitVec))
                {
                    pumpTileEntity.cyclePowerMode();
                    this.invalidatePipeNetwork(world, pos);
                    Vector3d vec = result.getLocation();
                    world.playSound(null, vec.x(), vec.y(), vec.z(), SoundEvents.NETHERITE_BLOCK_HIT, SoundCategory.BLOCKS, 1.0F, 0.5F + 0.1F * world.random.nextFloat());
                    return ActionResultType.SUCCESS;
                }
            }
        }

        return ActionResultType.PASS;
    }

    public boolean isLookingAtHousing(BlockState state, Vector3d hitVec)
    {
        VoxelShape shape = PUMP_BOX[this.getCollisionFacing(state).get3DDataValue()];
        AxisAlignedBB boundingBox = shape.bounds();
        return boundingBox.inflate(0.001).contains(hitVec);
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState replaceState, boolean what)
    {
        if(!state.is(replaceState.getBlock()))
        {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if(tileEntity instanceof PumpTileEntity)
            {
                ((PumpTileEntity) tileEntity).removePumpFromPipes();
            }
        }
        super.onRemove(state, world, pos, replaceState, what);
    }

    @Override
    protected void invalidatePipeNetwork(World world, BlockPos pos)
    {
        super.invalidatePipeNetwork(world, pos);

        TileEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof PumpTileEntity)
        {
            ((PumpTileEntity) tileEntity).invalidatePipeNetwork();
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockState state = this.defaultBlockState().setValue(DIRECTION, face);
        state = this.getPipeState(state, world, pos);
        state = this.getDisabledState(state, world, pos);
        return state;
    }

    @Override
    public BlockState getDisabledState(BlockState state, World world, BlockPos pos)
    {
        boolean disabled = false;
        TileEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof PumpTileEntity)
        {
            PumpTileEntity pump = (PumpTileEntity) tileEntity;
            disabled = !pump.getPowerMode().test(pump);
        }
        state = state.setValue(DISABLED, disabled);
        return state;
    }

    @Override
    protected boolean canPipeConnectTo(BlockState state, IWorld world, BlockPos pos, Direction direction)
    {
        if(direction == state.getValue(DIRECTION).getOpposite())
            return false;
        return super.canPipeConnectTo(state, world, pos, direction);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(DIRECTION);
    }

    @Nullable
    @Override
    public PumpTileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new PumpTileEntity();
    }
}
