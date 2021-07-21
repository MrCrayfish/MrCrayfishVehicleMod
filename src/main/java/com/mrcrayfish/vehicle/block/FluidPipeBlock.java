package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.common.FluidNetworkHandler;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.item.WrenchItem;
import com.mrcrayfish.vehicle.tileentity.PipeTileEntity;
import com.mrcrayfish.vehicle.tileentity.PumpTileEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import com.mrcrayfish.vehicle.util.VoxelShapeHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
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
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class FluidPipeBlock extends ObjectBlock
{
    public static final BooleanProperty[] CONNECTED_PIPES = {BlockStateProperties.DOWN, BlockStateProperties.UP, BlockStateProperties.NORTH, BlockStateProperties.SOUTH, BlockStateProperties.WEST, BlockStateProperties.EAST};
    public static final BooleanProperty DISABLED = BooleanProperty.create("disabled");

    protected static final VoxelShape CENTER = Block.box(5, 5, 5, 11, 11, 11);
    protected static final VoxelShape[] SIDES = {
            Block.box(5, 0, 5, 11, 5, 11), Block.box(5, 11, 5, 11, 16, 11),
            Block.box(5, 5, 0, 11, 11, 5), Block.box(5, 5, 11, 11, 11, 16),
            Block.box(0, 5, 5, 5, 11, 11), Block.box(11, 5, 5, 16, 11, 11),
            CENTER
    };

    public FluidPipeBlock()
    {
        super(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.NETHERITE_BLOCK).strength(0.5F));
        BlockState defaultState = this.getStateDefinition().any().setValue(DISABLED, true);
        for(BooleanProperty property : CONNECTED_PIPES)
        {
            defaultState = defaultState.setValue(property, false);
        }
        this.registerDefaultState(defaultState);
    }

    @Nullable
    public static PipeTileEntity getPipeTileEntity(IBlockReader world, BlockPos pos)
    {
        TileEntity tileEntity = world.getBlockEntity(pos);
        return tileEntity instanceof PipeTileEntity ? (PipeTileEntity) tileEntity : null;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.getPipeShape(state, worldIn, pos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.getPipeShape(state, worldIn, pos);
    }

    public VoxelShape getPipeShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        List<VoxelShape> shapes = new ArrayList<>();
        boolean[] disabledConnections = this.getDisabledConnections(worldIn, pos);
        for(int i = 0; i < Direction.values().length; i++)
        {
            if(state.getValue(CONNECTED_PIPES[i]) && !disabledConnections[i])
            {
                shapes.add(SIDES[i]);
            }
        }
        shapes.addAll(Arrays.asList(SIDES).subList(Direction.values().length, SIDES.length));
        return VoxelShapeHelper.combineAll(shapes);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        PipeTileEntity pipe = getPipeTileEntity(world, pos);
        Pair<AxisAlignedBB, Direction> hit = this.getConnectionBox(world, pos, state, player, hand, result.getDirection(), result.getLocation(), pipe);
        if(pipe != null && hit != null)
        {
            Direction direction = hit.getRight();
            boolean enabled = !pipe.isConnectionDisabled(direction);
            pipe.setConnectionState(direction, enabled);
            BlockState newState = state.setValue(CONNECTED_PIPES[direction.get3DDataValue()], !enabled);
            world.setBlockAndUpdate(pos, newState);
            world.sendBlockUpdated(pos, state, newState, 3 & 8);
            this.invalidatePipeNetwork(world, pos);

            // Also changes the state of the adjacent connection
            BlockPos relativePos = pos.relative(direction);
            PipeTileEntity adjacentPipe = getPipeTileEntity(world, relativePos);
            if(adjacentPipe != null)
            {
                Direction opposite = direction.getOpposite();
                adjacentPipe.setConnectionState(opposite, enabled);
                BlockState relativeState = adjacentPipe.getBlockState();
                BlockState newRelativeState = relativeState.setValue(CONNECTED_PIPES[opposite.get3DDataValue()], !enabled);
                world.setBlockAndUpdate(relativePos, newRelativeState);
                world.sendBlockUpdated(relativePos, relativeState, newRelativeState, 3 & 8);
                FluidPipeBlock relativeBlock = (FluidPipeBlock) relativeState.getBlock();
                relativeBlock.invalidatePipeNetwork(world, relativePos);
            }

            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.IRON_GOLEM_STEP, SoundCategory.BLOCKS, 1.0F, 2.0F);

            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Nullable
    protected Pair<AxisAlignedBB, Direction> getConnectionBox(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, Vector3d hitVec, @Nullable PipeTileEntity pipe)
    {
        Vector3d localHitVec = hitVec.add(-pos.getX(), -pos.getY(), -pos.getZ());
        if(pipe == null || !(player.getItemInHand(hand).getItem() instanceof WrenchItem))
        {
            return null;
        }
        for(int i = 0; i < Direction.values().length + 1; i++)
        {
            boolean isCenter = i == Direction.values().length;
            if((isCenter || state.getValue(CONNECTED_PIPES[i])) && SIDES[i].bounds().inflate(0.001).contains(localHitVec))
            {
                if(!isCenter)
                {
                    facing = Direction.from3DDataValue(i);
                }
                else if(!state.getValue(CONNECTED_PIPES[facing.get3DDataValue()]))
                {
                    BlockPos adjacentPos = pos.relative(facing);
                    BlockState adjacentState = world.getBlockState(adjacentPos);
                    Block adjacentBlock = adjacentState.getBlock();

                    if(adjacentBlock != ModBlocks.FLUID_PIPE.get() && adjacentBlock != ModBlocks.FLUID_PUMP.get())
                    {
                        TileEntity adjacentTileEntity = world.getBlockEntity(adjacentPos);
                        if(adjacentTileEntity == null || !adjacentTileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()).isPresent())
                        {
                            return null;
                        }
                    }
                }

                if(world.getBlockState(pos.relative(facing)).getBlock() != Blocks.LEVER)
                {
                    return new ImmutablePair<>(SIDES[i].bounds().move(pos), facing);
                }
            }
        }
        return null;
    }

    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState newState, boolean what)
    {
        if(state.getBlock() == newState.getBlock())
            return;

        PipeTileEntity tileEntity = this.createTileEntity(state, world);
        if(tileEntity != null)
        {
            for(Direction direction : Direction.values())
            {
                TileEntity relativeTileEntity = world.getBlockEntity(pos.relative(direction));
                if(relativeTileEntity instanceof PipeTileEntity)
                {
                    tileEntity.getDisabledConnections()[direction.get3DDataValue()] = ((PipeTileEntity) relativeTileEntity).isConnectionDisabled(direction.getOpposite());
                }
            }
            world.setBlockEntity(pos, tileEntity);
            FluidNetworkHandler.instance().addPipeForUpdate(tileEntity);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean p_220069_6_)
    {
        boolean disabled = this.getDisabledState(state, world, pos).getValue(DISABLED);
        if(state.getValue(DISABLED) != disabled)
        {
            this.invalidatePipeNetwork(world, pos);
            if(state.getBlock() instanceof FluidPumpBlock)
            {
                world.setBlock(pos, state.setValue(DISABLED, disabled), Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.RERENDER_MAIN_THREAD);
            }
        }

        BlockState newState = this.getPipeState(state, world, pos);
        for(Direction direction : Direction.values())
        {
            int index = direction.get3DDataValue();
            if(newState.getValue(CONNECTED_PIPES[index]) != state.getValue(CONNECTED_PIPES[index]))
            {
                this.invalidatePipeNetwork(world, pos);
                break;
            }
        }
    }

    protected BlockState getDisabledState(BlockState state, World world, BlockPos pos)
    {
        boolean disabled = world.hasNeighborSignal(pos);
        state = state.setValue(DISABLED, disabled);
        return state;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState replaceState, boolean what)
    {
        if(!state.is(replaceState.getBlock()))
        {
            this.invalidatePipeNetwork(world, pos);
            super.onRemove(state, world, pos, replaceState, what);
        }
    }

    protected void invalidatePipeNetwork(World world, BlockPos pos)
    {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof PipeTileEntity)
        {
            Set<BlockPos> pumps = ((PipeTileEntity) tileEntity).getPumps();
            pumps.forEach(pumpPos ->
            {
                TileEntity te = world.getBlockEntity(pumpPos);
                if(te instanceof PumpTileEntity)
                {
                    ((PumpTileEntity) te).invalidatePipeNetwork();
                }
            });
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, IWorld world, BlockPos pos, BlockPos neighbourPos)
    {
        return this.getPipeState(state, world, pos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();
        state = this.getPipeState(state, world, pos);
        state = this.getDisabledState(state, world, pos);
        state = this.getPlacedDisabledState(state, world, pos);
        return state;
    }

    protected BlockState getPlacedDisabledState(BlockState state, World world, BlockPos pos)
    {
        if(!state.getValue(DISABLED))
        {
            state = state.setValue(DISABLED, true);
            for(Direction direction : Direction.values())
            {
                BlockPos relativePos = pos.relative(direction);
                TileEntity relativeTileEntity = world.getBlockEntity(relativePos);
                if(relativeTileEntity instanceof PipeTileEntity)
                {
                    PipeTileEntity pipeTileEntity = (PipeTileEntity) relativeTileEntity;
                    if(!pipeTileEntity.getDisabledConnections()[direction.getOpposite().get3DDataValue()])
                    {
                        BlockState relativeState = pipeTileEntity.getBlockState();
                        if(!relativeState.getValue(DISABLED))
                        {
                            if(relativeState.getBlock() instanceof FluidPumpBlock)
                            {
                                if(relativeState.getValue(FluidPumpBlock.DIRECTION) == direction)
                                {
                                    continue;
                                }
                            }
                            state = state.setValue(DISABLED, false);
                            break;
                        }
                    }
                }
            }
        }
        return state;
    }

    protected BlockState getPipeState(BlockState state, IWorld world, BlockPos pos)
    {
        boolean[] disabledConnections = this.getDisabledConnections(world, pos);
        for(Direction direction : Direction.values())
        {
            state = state.setValue(CONNECTED_PIPES[direction.get3DDataValue()], false);

            if(disabledConnections[direction.get3DDataValue()] && world.getBlockState(pos.relative(direction)).getBlock() != Blocks.LEVER)
                continue;

            state = state.setValue(CONNECTED_PIPES[direction.get3DDataValue()], this.canPipeConnectTo(state, world, pos, direction));
        }
        return state;
    }

    protected boolean canPipeConnectTo(BlockState state, IWorld world, BlockPos pos, Direction direction)
    {
        BlockPos relativePos = pos.relative(direction);
        TileEntity adjacentTileEntity = world.getBlockEntity(relativePos);
        if(adjacentTileEntity instanceof PipeTileEntity)
        {
            BlockState relativeState = world.getBlockState(relativePos);
            if(relativeState.getBlock() instanceof FluidPumpBlock)
            {
                if(relativeState.getValue(FluidPumpBlock.DIRECTION) == direction)
                {
                    return false;
                }
            }
            return !((PipeTileEntity) adjacentTileEntity).isConnectionDisabled(direction.getOpposite());
        }
        else if(adjacentTileEntity != null && adjacentTileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).isPresent())
        {
            return true;
        }
        else
        {
            BlockState adjacentState = world.getBlockState(relativePos);
            if(adjacentState.getBlock() == Blocks.LEVER)
            {
                AttachFace attachFace = adjacentState.getValue(LeverBlock.FACE);
                if(direction.getAxis() != Direction.Axis.Y)
                {
                    return adjacentState.getValue(LeverBlock.FACING) == direction && attachFace == AttachFace.WALL;
                }
                else if(direction == Direction.UP && attachFace == AttachFace.FLOOR)
                {
                    return true;
                }
                return direction == Direction.DOWN && attachFace == AttachFace.CEILING;
            }
            return false;
        }
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return VoxelShapes.block();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED_PIPES);
        builder.add(DISABLED);
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public PipeTileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new PipeTileEntity();
    }

    protected boolean[] getDisabledConnections(IBlockReader reader, BlockPos pos)
    {
        PipeTileEntity tileEntity = getPipeTileEntity(reader, pos);
        return tileEntity != null ? tileEntity.getDisabledConnections() : new boolean[Direction.values().length];
    }
}
