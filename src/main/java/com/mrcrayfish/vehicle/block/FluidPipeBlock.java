package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.item.WrenchItem;
import com.mrcrayfish.vehicle.tileentity.FluidPipeTileEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import com.mrcrayfish.vehicle.util.VoxelShapeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.AbstractBlock;

/**
 * Author: MrCrayfish
 */
public class FluidPipeBlock extends ObjectBlock
{
    public static final DirectionProperty DIRECTION = DirectionProperty.create("facing", Direction.values());
    public static final BooleanProperty[] CONNECTED_PIPES = Util.make(() ->
    {
        BooleanProperty[] directions = new BooleanProperty[Direction.values().length];
        for(Direction facing : Direction.values())
        {
            directions[facing.get3DDataValue()] = BooleanProperty.create("pipe_" + facing.getName());
        }
        return directions;
    });

    protected static final VoxelShape CENTER = Block.box(5, 5, 5, 11, 11, 11);
    protected static final VoxelShape PIPES[] = {
            Block.box(5.5, 0, 5.5, 10.5, 5, 10.5), Block.box(5.5, 11, 5.5, 10.5, 16, 10.5),
            Block.box(5.5, 5.5, 0, 10.5, 10.5, 5), Block.box(5.5, 5.5, 11, 10.5, 10.5, 16),
            Block.box(0, 5.5, 5.5, 5, 10.5, 10.5), Block.box(11, 5.5, 5.5, 16, 10.5, 10.5),
            CENTER
    };

    public FluidPipeBlock()
    {
        super(AbstractBlock.Properties.of(Material.METAL).strength(0.5F));
        BlockState defaultState = this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH);
        for(Direction facing : Direction.values())
        {
            defaultState = defaultState.setValue(CONNECTED_PIPES[facing.get3DDataValue()], false);
        }
        this.registerDefaultState(defaultState);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader reader, List<ITextComponent> list, ITooltipFlag advanced)
    {
        if(Screen.hasShiftDown())
        {
            ITextProperties info = new TranslationTextComponent(this.getDescriptionId() + ".info");
            list.addAll(RenderUtil.lines(info, 150));
        }
        else
        {
            list.add(new StringTextComponent(TextFormatting.YELLOW + I18n.get("vehicle.info_help")));
        }
    }

    protected Direction getCollisionFacing(BlockState state)
    {
        return state.getValue(DIRECTION);
    }

    @Nullable
    public static FluidPipeTileEntity getPipeTileEntity(IBlockReader world, BlockPos pos)
    {
        TileEntity tileEntity = world.getBlockEntity(pos);
        return tileEntity instanceof FluidPipeTileEntity ? (FluidPipeTileEntity) tileEntity : null;
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

    protected VoxelShape getPipeShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        List<VoxelShape> shapes = new ArrayList<>();
        boolean[] disabledConnections = FluidPipeTileEntity.getDisabledConnections(getPipeTileEntity(worldIn, pos));
        for(int i = 0; i < Direction.values().length; i++)
        {
            if(state.getValue(CONNECTED_PIPES[i]) && !disabledConnections[i])
            {
                shapes.add(PIPES[i]);
            }
        }
        shapes.addAll(Arrays.asList(PIPES).subList(Direction.values().length, PIPES.length));
        shapes.add(PIPES[this.getCollisionFacing(state).get3DDataValue()]);
        return VoxelShapeHelper.combineAll(shapes);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        FluidPipeTileEntity pipe = getPipeTileEntity(world, pos);
        Pair<AxisAlignedBB, Direction> hit = this.getBox(world, pos, state, player, hand, result.getDirection(), result.getLocation(), pipe);
        if (pipe != null && hit != null)
        {
            Direction face = hit.getRight();
            pipe.setConnectionDisabled(face, !pipe.isConnectionDisabled(face));
            BlockState newState = state.setValue(CONNECTED_PIPES[face.get3DDataValue()], !pipe.isConnectionDisabled(face));
            world.setBlockAndUpdate(pos, newState);
            world.sendBlockUpdated(pos, state, newState, 3 & 8);
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.IRON_GOLEM_STEP, SoundCategory.BLOCKS, 1.0F, 2.0F);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Nullable
    protected Pair<AxisAlignedBB, Direction> getBox(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, Vector3d hitVec, @Nullable FluidPipeTileEntity pipe)
    {
        hitVec = hitVec.add(-pos.getX(), -pos.getY(), -pos.getZ());
        if(pipe == null || !(player.getItemInHand(hand).getItem() instanceof WrenchItem))
        {
            return null;
        }
        for(int i = 0; i < Direction.values().length + 1; i++)
        {
            boolean isCenter = i == Direction.values().length;
            if((isCenter || state.getValue(CONNECTED_PIPES[i])) && PIPES[i].bounds().inflate(0.001).contains(hitVec))
            {
                if(!isCenter)
                {
                    facing = Direction.from3DDataValue(i);
                }
                else if(!state.getValue(CONNECTED_PIPES[facing.get3DDataValue()]))
                {
                    BlockPos adjacentPos = pos.relative(facing);
                    BlockState adjacentState = world.getBlockState(adjacentPos);
                    TileEntity tileEntity = world.getBlockEntity(adjacentPos);
                    Block adjacentBlock = adjacentState.getBlock();
                    if((this == ModBlocks.FLUID_PUMP.get() && adjacentBlock == ModBlocks.FLUID_PUMP.get()) || (this == ModBlocks.FLUID_PIPE.get() && adjacentBlock == ModBlocks.FLUID_PIPE.get() && getCollisionFacing(adjacentState) != facing.getOpposite()) || tileEntity == null || !tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()).isPresent())
                    {
                        return null;
                    }
                }
                if(world.getBlockState(pos.relative(facing)).getBlock() != Blocks.LEVER && this.getCollisionFacing(state) != facing)
                {
                    return new ImmutablePair<>(PIPES[i].bounds().move(pos), facing);
                }
            }
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, IWorld world, BlockPos pos, BlockPos neighbourPos)
    {
        return this.getPipeState(state, world, pos, state.getValue(DIRECTION));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = super.getStateForPlacement(context).setValue(DIRECTION, context.getClickedFace().getOpposite());
        return this.getPipeState(state, context.getLevel(), context.getClickedPos(), context.getClickedFace().getOpposite()); //TODO test this
    }

    protected BlockState getPipeState(BlockState state, IWorld world, BlockPos pos, Direction originalFacing)
    {
        boolean[] disabledConnections = FluidPipeTileEntity.getDisabledConnections(getPipeTileEntity(world, pos));
        for(Direction facing : Direction.values())
        {
            if(facing == originalFacing)
                continue;

            state = state.setValue(CONNECTED_PIPES[facing.get3DDataValue()], false);

            BlockPos adjacentPos = pos.relative(facing);
            BlockState adjacentState = world.getBlockState(adjacentPos);
            boolean enabled = !disabledConnections[facing.get3DDataValue()];
            if(adjacentState.getBlock() == ModBlocks.FLUID_PIPE.get())
            {
                Direction adjacentFacing = adjacentState.getValue(DIRECTION);
                if(adjacentPos.relative(adjacentFacing).equals(pos))
                {
                    state = state.setValue(CONNECTED_PIPES[facing.get3DDataValue()], enabled);
                }
            }
            else if(adjacentState.getBlock() == ModBlocks.FLUID_PUMP.get())
            {
                state = state.setValue(CONNECTED_PIPES[facing.get3DDataValue()], enabled);
            }
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(DIRECTION);
        builder.add(CONNECTED_PIPES);
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
        return new FluidPipeTileEntity();
    }
}
