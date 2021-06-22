package com.mrcrayfish.vehicle.block;

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
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class FluidPipeBlock extends ObjectBlock
{
    public static final BooleanProperty[] CONNECTED_PIPES = Stream.of(Direction.values()).map(dir -> BooleanProperty.create(dir.getName())).collect(Collectors.toList()).toArray(new BooleanProperty[0]);

    protected static final VoxelShape CENTER = Block.box(5, 5, 5, 11, 11, 11);
    protected static final VoxelShape[] SIDES = {
            Block.box(5, 0, 5, 11, 5, 11), Block.box(5, 11, 5, 11, 16, 11),
            Block.box(5, 5, 0, 11, 11, 5), Block.box(5, 5, 11, 11, 11, 16),
            Block.box(0, 5, 5, 5, 11, 11), Block.box(11, 5, 5, 16, 11, 11),
            CENTER
    };

    public FluidPipeBlock()
    {
        super(AbstractBlock.Properties.of(Material.METAL).strength(0.5F));
        BlockState defaultState = this.getStateDefinition().any();
        for(BooleanProperty property : CONNECTED_PIPES)
        {
            defaultState = defaultState.setValue(property, false);
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

    protected VoxelShape getPipeShape(BlockState state, IBlockReader worldIn, BlockPos pos)
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
            pipe.setConnectionState(direction, !pipe.isConnectionDisabled(direction));
            BlockState newState = state.setValue(CONNECTED_PIPES[direction.get3DDataValue()], !pipe.isConnectionDisabled(direction));
            world.setBlockAndUpdate(pos, newState);
            world.sendBlockUpdated(pos, state, newState, 3 & 8);
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

                    if(this == ModBlocks.FLUID_PUMP.get() && adjacentBlock == ModBlocks.FLUID_PUMP.get())
                    {
                        return null;
                    }

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
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean p_220069_6_)
    {
        if(neighborBlock == ModBlocks.FLUID_PIPE.get())
        {
            this.invalidatePipeNetwork(world, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState replaceState, boolean what)
    {
        this.invalidatePipeNetwork(world, pos);
        super.onRemove(state, world, pos, replaceState, what);
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
        BlockState state = super.getStateForPlacement(context);
        return this.getPipeState(state, context.getLevel(), context.getClickedPos()); //TODO test this
    }

    protected BlockState getPipeState(BlockState state, IWorld world, BlockPos pos)
    {
        boolean[] disabledConnections = this.getDisabledConnections(world, pos);
        for(Direction facing : Direction.values())
        {
            state = state.setValue(CONNECTED_PIPES[facing.get3DDataValue()], false);

            BlockPos adjacentPos = pos.relative(facing);
            BlockState adjacentState = world.getBlockState(adjacentPos);
            boolean enabled = !disabledConnections[facing.get3DDataValue()];
            if(adjacentState.getBlock() == ModBlocks.FLUID_PIPE.get())
            {
                state = state.setValue(CONNECTED_PIPES[facing.get3DDataValue()], enabled);
            }
            else if(adjacentState.getBlock() == ModBlocks.FLUID_PUMP.get())
            {
                state = state.setValue(CONNECTED_PIPES[facing.get3DDataValue()], enabled);
            }
            else
            {
                TileEntity tileEntity = world.getBlockEntity(adjacentPos);
                if(tileEntity != null && tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()).isPresent())
                {
                    state = state.setValue(CONNECTED_PIPES[facing.get3DDataValue()], enabled);
                }
            }
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
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
        return new PipeTileEntity();
    }

    protected boolean[] getDisabledConnections(IBlockReader reader, BlockPos pos)
    {
        PipeTileEntity tileEntity = getPipeTileEntity(reader, pos);
        return tileEntity != null ? tileEntity.getDisabledConnections() : new boolean[Direction.values().length];
    }
}
