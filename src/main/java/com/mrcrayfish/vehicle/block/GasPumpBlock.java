package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.util.VoxelShapeHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class GasPumpBlock extends RotatedObjectBlock
{
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    private static final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public GasPumpBlock()
    {
        super(AbstractBlock.Properties.of(Material.HEAVY_METAL).strength(1.0F));
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(TOP, false));
    }

    private VoxelShape getShape(BlockState state)
    {
        if (SHAPES.containsKey(state))
        {
            return SHAPES.get(state);
        }
        Direction direction = state.getValue(DIRECTION);
        boolean top = state.getValue(TOP);
        List<VoxelShape> shapes = new ArrayList<>();
        if (top)
        {
            shapes.add(VoxelShapeHelper.getRotatedShapes(VoxelShapeHelper.rotate(Block.box(3, -16, 0, 13, 15, 16), Direction.EAST))[direction.get2DDataValue()]);
        }
        else
        {
            shapes.add(VoxelShapeHelper.getRotatedShapes(VoxelShapeHelper.rotate(Block.box(3, 0, 0, 13, 31, 16), Direction.EAST))[direction.get2DDataValue()]);
        }
        VoxelShape shape = VoxelShapeHelper.combineAll(shapes);
        SHAPES.put(state, shape);
        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context)
    {
        return this.getShape(state);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult result)
    {
        if(world.isClientSide())
        {
            return ActionResultType.SUCCESS;
        }

        if(state.getValue(TOP))
        {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if(tileEntity instanceof GasPumpTileEntity)
            {
                GasPumpTileEntity gasPump = (GasPumpTileEntity) tileEntity;
                if(gasPump.getFuelingEntity() != null && gasPump.getFuelingEntity().getId() == playerEntity.getId())
                {
                    gasPump.setFuelingEntity(null);
                    world.playSound(null, pos, ModSounds.BLOCK_GAS_PUMP_NOZZLE_PUT_DOWN.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                else if(state.getValue(DIRECTION).getClockWise().equals(result.getDirection()))
                {
                    gasPump.setFuelingEntity(playerEntity);
                    world.playSound(null, pos, ModSounds.BLOCK_GAS_PUMP_NOZZLE_PICK_UP.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }
            return ActionResultType.SUCCESS;
        }
        else if(FluidUtil.interactWithFluidHandler(playerEntity, hand, world, pos, result.getDirection()))
        {
            return ActionResultType.CONSUME;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader reader, BlockPos pos)
    {
        return reader.isEmptyBlock(pos) && reader.isEmptyBlock(pos.above());
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        worldIn.setBlockAndUpdate(pos.above(), state.setValue(TOP, true));
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (!world.isClientSide())
        {
            boolean top = state.getValue(TOP);
            BlockPos blockpos = pos.relative(top ? Direction.DOWN : Direction.UP);
            BlockState blockstate = world.getBlockState(blockpos);
            if (blockstate.getBlock() == state.getBlock() && blockstate.getValue(TOP) != top)
            {
                world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                world.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        if (state.getValue(TOP))
        {
            Vector3d origin = builder.getOptionalParameter(LootParameters.ORIGIN);
            if (origin != null)
            {
                BlockPos pos = new BlockPos(origin);
                TileEntity tileEntity = builder.getLevel().getBlockEntity(pos.below());
                if (tileEntity != null)
                {
                    builder = builder.withParameter(LootParameters.BLOCK_ENTITY, tileEntity);
                }
            }
        }
        return super.getDrops(state, builder);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(TOP);
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
        if (state.getValue(TOP))
        {
            return new GasPumpTileEntity();
        }
        return new GasPumpTankTileEntity();
    }
}