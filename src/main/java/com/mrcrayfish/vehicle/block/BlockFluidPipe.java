package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.item.WrenchItem;
import com.mrcrayfish.vehicle.tileentity.FluidPipeTileEntity;
import com.mrcrayfish.vehicle.util.Names;
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
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class BlockFluidPipe extends BlockObject
{
    public static final DirectionProperty DIRECTION = DirectionProperty.create("direction");
    public static final BooleanProperty[] CONNECTED_PIPES = Util.make(() ->
    {
        BooleanProperty[] directions = new BooleanProperty[Direction.values().length];
        for(Direction facing : Direction.values())
        {
            directions[facing.getIndex()] = BooleanProperty.create("pipe_" + facing.getName());
        }
        return directions;
    });

    protected List<AxisAlignedBB> boxes;
    protected AxisAlignedBB boxCenter;
    protected String name;

    public BlockFluidPipe()
    {
        this(Names.Block.FLUID_PIPE);
    }

    public BlockFluidPipe(String name)
    {
        super(name, Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F));
        this.name = name;
        BlockState defaultState = this.getStateContainer().getBaseState().with(DIRECTION, Direction.NORTH);
        for(Direction facing : Direction.values())
        {
            defaultState = defaultState.with(CONNECTED_PIPES[facing.getIndex()], false);
        }
        this.setDefaultState(defaultState);
        this.boxCenter = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);
        this.boxes = Stream.of(new AxisAlignedBB(0.34375, 0, 0.34375, 0.65625, 0.3125, 0.65625), new AxisAlignedBB(0.34375, 0.6875, 0.34375, 0.65625, 1, 0.65625),
                new AxisAlignedBB(0.34375, 0.34375, 0, 0.65625, 0.65625, 0.3125), new AxisAlignedBB(0.34375, 0.34375, 0.6875, 0.65625, 0.65625, 1),
                new AxisAlignedBB(0, 0.34375, 0.34375, 0.3125, 0.65625, 0.65625), new AxisAlignedBB(0.6875, 0.34375, 0.34375, 1, 0.65625, 0.65625),
                this.boxCenter).collect(Collectors.toList());
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
    public static FluidPipeTileEntity getPipeTileEntity(IWorld world, BlockPos pos)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity instanceof FluidPipeTileEntity ? (FluidPipeTileEntity) tileEntity : null;
    }

    //TODO redo collisions
    /*@Override
    public void addCollisionBoxToList(BlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState)
    {
        if (!isActualState)
        {
            state = state.getActualState(world, pos);
        }

        boolean[] disabledConnections = FluidPipeTileEntity.getDisabledConnections(getPipeTileEntity(world, pos));
        for (int i = 0; i < Direction.values().length; i++)
        {
            if (state.getValue(CONNECTED_PIPES[i]) && !disabledConnections[i])
            {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, boxes.get(i));
            }
        }
        for (int i = Direction.values().length; i < boxes.size(); i++)
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, boxes.get(i));
        }
        addCollisionBoxToList(pos, entityBox, collidingBoxes, boxes.get(getCollisionFacing(state).getIndex()));
    }*/

    @Override
    public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        FluidPipeTileEntity pipe = getPipeTileEntity(world, pos);
        Pair<AxisAlignedBB, Direction> hit = this.getBox(world, pos, state, player, hand, result.getFace(), result.getHitVec(), pipe);
        if (pipe != null && hit != null)
        {
            Direction face = hit.getRight();
            pipe.setConnectionDisabled(face, !pipe.isConnectionDisabled(face));
            world.setBlockState(pos, state.with(CONNECTED_PIPES[face.getIndex()], pipe.isConnectionDisabled(face))); //TODO test this
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Nullable
    private Pair<AxisAlignedBB, Direction> getBox(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, Vec3d hitVec, @Nullable FluidPipeTileEntity pipe)
    {
        if(pipe == null || !(player.getHeldItem(hand).getItem() instanceof WrenchItem))
        {
            return null;
        }
        for(int i = 0; i < Direction.values().length + 1; i++)
        {
            boolean isCenter = i == Direction.values().length;
            if((isCenter || state.get(CONNECTED_PIPES[i])) && boxes.get(i).grow(0.001).contains(hitVec))
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
                    return new ImmutablePair<>(boxes.get(i).offset(pos), facing);
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
        BlockState state = super.getStateForPlacement(context);
        return this.getPipeState(state, context.getWorld(), context.getPos(), context.getFace().getOpposite()); //TODO test this
    }

    private BlockState getPipeState(BlockState state, IWorld world, BlockPos pos, Direction originalFacing)
    {
        boolean[] disabledConnections = FluidPipeTileEntity.getDisabledConnections(getPipeTileEntity(world, pos));
        for(Direction facing : Direction.values())
        {
            if(facing == originalFacing)
                continue;

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
