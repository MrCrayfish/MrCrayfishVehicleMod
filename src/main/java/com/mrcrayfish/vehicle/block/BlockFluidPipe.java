package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.item.WrenchItem;
import com.mrcrayfish.vehicle.tileentity.FluidPipeTileEntity;
import com.mrcrayfish.vehicle.util.Names;
import com.mrcrayfish.vehicle.util.VoxelShapeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class BlockFluidPipe extends BlockObject
{
    public static final DirectionProperty DIRECTION = DirectionProperty.create("facing", Direction.values());
    public static final BooleanProperty[] CONNECTED_PIPES = Util.make(() ->
    {
        BooleanProperty[] directions = new BooleanProperty[Direction.values().length];
        for(Direction facing : Direction.values())
        {
            directions[facing.getIndex()] = BooleanProperty.create("pipe_" + facing.getName());
        }
        return directions;
    });

    protected static final VoxelShape CENTER = Block.makeCuboidShape(5, 5, 5, 11, 11, 11);
    protected static final VoxelShape PIPES[] = {
            Block.makeCuboidShape(5.5, 0, 5.5, 10.5, 5, 10.5), Block.makeCuboidShape(5.5, 11, 5.5, 10.5, 16, 10.5),
            Block.makeCuboidShape(5.5, 5.5, 0, 10.5, 10.5, 5), Block.makeCuboidShape(5.5, 5.5, 11, 10.5, 10.5, 16),
            Block.makeCuboidShape(0, 5.5, 5.5, 5, 10.5, 10.5), Block.makeCuboidShape(11, 5.5, 5.5, 16, 10.5, 10.5),
            CENTER
    };

    public BlockFluidPipe()
    {
        this(Names.Block.FLUID_PIPE);
    }

    public BlockFluidPipe(String name)
    {
        super(name, Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F));
        BlockState defaultState = this.getStateContainer().getBaseState().with(DIRECTION, Direction.NORTH);
        for(Direction facing : Direction.values())
        {
            defaultState = defaultState.with(CONNECTED_PIPES[facing.getIndex()], false);
        }
        this.setDefaultState(defaultState);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable IBlockReader reader, List<ITextComponent> list, ITooltipFlag advanced)
    {
        if(Screen.hasShiftDown())
        {
            String info = I18n.format(this.getTranslationKey() + ".info");
            list.addAll(Minecraft.getInstance().fontRenderer.listFormattedStringToWidth(info, 150).stream().map((Function<String, ITextComponent>) StringTextComponent::new).collect(Collectors.toList()));
        }
        else
        {
            list.add(new StringTextComponent(TextFormatting.YELLOW + I18n.format("vehicle.info_help")));
        }
    }

    protected Direction getCollisionFacing(BlockState state)
    {
        return state.get(DIRECTION);
    }

    @Nullable
    public static FluidPipeTileEntity getPipeTileEntity(IBlockReader world, BlockPos pos)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
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
            if(state.get(CONNECTED_PIPES[i]) && !disabledConnections[i])
            {
                shapes.add(PIPES[i]);
            }
        }
        shapes.addAll(Arrays.asList(PIPES).subList(Direction.values().length, PIPES.length));
        shapes.add(PIPES[this.getCollisionFacing(state).getIndex()]);
        return VoxelShapeHelper.combineAll(shapes);
    }

    @Override
    public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        FluidPipeTileEntity pipe = getPipeTileEntity(world, pos);
        Pair<AxisAlignedBB, Direction> hit = this.getBox(world, pos, state, player, hand, result.getFace(), result.getHitVec(), pipe);
        if (pipe != null && hit != null)
        {
            Direction face = hit.getRight();
            pipe.setConnectionDisabled(face, !pipe.isConnectionDisabled(face));
            BlockState newState = state.with(CONNECTED_PIPES[face.getIndex()], !pipe.isConnectionDisabled(face));
            world.setBlockState(pos, newState);
            world.notifyBlockUpdate(pos, state, newState, 3 & 8);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Nullable
    protected Pair<AxisAlignedBB, Direction> getBox(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, Vec3d hitVec, @Nullable FluidPipeTileEntity pipe)
    {
        hitVec = hitVec.add(-pos.getX(), -pos.getY(), -pos.getZ());
        if(pipe == null || !(player.getHeldItem(hand).getItem() instanceof WrenchItem))
        {
            return null;
        }
        for(int i = 0; i < Direction.values().length + 1; i++)
        {
            boolean isCenter = i == Direction.values().length;
            if((isCenter || state.get(CONNECTED_PIPES[i])) && PIPES[i].getBoundingBox().grow(0.001).contains(hitVec))
            {
                if(!isCenter)
                {
                    facing = Direction.byIndex(i);
                }
                else if(!state.get(CONNECTED_PIPES[facing.getIndex()]))
                {
                    BlockPos adjacentPos = pos.offset(facing);
                    BlockState adjacentState = world.getBlockState(adjacentPos);
                    TileEntity tileEntity = world.getTileEntity(adjacentPos);
                    Block adjacentBlock = adjacentState.getBlock();
                    if((this == ModBlocks.FLUID_PUMP && adjacentBlock == ModBlocks.FLUID_PUMP) || (this == ModBlocks.FLUID_PIPE && adjacentBlock == ModBlocks.FLUID_PIPE && getCollisionFacing(adjacentState) != facing.getOpposite()) || tileEntity == null || !tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()).isPresent())
                    {
                        return null;
                    }
                }
                if(world.getBlockState(pos.offset(facing)).getBlock() != Blocks.LEVER && this.getCollisionFacing(state) != facing)
                {
                    return new ImmutablePair<>(PIPES[i].getBoundingBox().offset(pos), facing);
                }
            }
        }
        return null;
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState neighbourState, IWorld world, BlockPos pos, BlockPos neighbourPos)
    {
        return this.getPipeState(state, world, pos, state.get(DIRECTION));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = super.getStateForPlacement(context).with(DIRECTION, context.getFace().getOpposite());
        return this.getPipeState(state, context.getWorld(), context.getPos(), context.getFace().getOpposite()); //TODO test this
    }

    protected BlockState getPipeState(BlockState state, IWorld world, BlockPos pos, Direction originalFacing)
    {
        boolean[] disabledConnections = FluidPipeTileEntity.getDisabledConnections(getPipeTileEntity(world, pos));
        for(Direction facing : Direction.values())
        {
            if(facing == originalFacing)
                continue;

            state = state.with(CONNECTED_PIPES[facing.getIndex()], false);

            BlockPos adjacentPos = pos.offset(facing);
            BlockState adjacentState = world.getBlockState(adjacentPos);
            boolean enabled = !disabledConnections[facing.getIndex()];
            if(adjacentState.getBlock() == ModBlocks.FLUID_PIPE)
            {
                Direction adjacentFacing = adjacentState.get(DIRECTION);
                if(adjacentPos.offset(adjacentFacing).equals(pos))
                {
                    state = state.with(CONNECTED_PIPES[facing.getIndex()], enabled);
                }
            }
            else if(adjacentState.getBlock() == ModBlocks.FLUID_PUMP)
            {
                state = state.with(CONNECTED_PIPES[facing.getIndex()], enabled);
            }
        }
        return state;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
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
