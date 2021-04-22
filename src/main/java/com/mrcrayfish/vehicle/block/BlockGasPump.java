package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.util.VoxelShapeHelper;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class BlockGasPump extends BlockRotatedObject
{
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    private static final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public BlockGasPump()
    {
        super(Block.Properties.create(Material.ANVIL).hardnessAndResistance(1.0F));
        this.setDefaultState(this.getStateContainer().getBaseState().with(DIRECTION, Direction.NORTH).with(TOP, false));
    }

    private VoxelShape getShape(BlockState state)
    {
        if (SHAPES.containsKey(state))
        {
            return SHAPES.get(state);
        }
        Direction direction = state.get(DIRECTION);
        boolean top = state.get(TOP);
        List<VoxelShape> shapes = new ArrayList<>();
        if (top)
        {
            shapes.add(VoxelShapeHelper.getRotatedShapes(VoxelShapeHelper.rotate(Block.makeCuboidShape(3, -16, 0, 13, 15, 16), Direction.EAST))[direction.getHorizontalIndex()]);
        }
        else
        {
            shapes.add(VoxelShapeHelper.getRotatedShapes(VoxelShapeHelper.rotate(Block.makeCuboidShape(3, 0, 0, 13, 31, 16), Direction.EAST))[direction.getHorizontalIndex()]);
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
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult result)
    {
        if (world.isRemote())
            return ActionResultType.SUCCESS;
        if (state.get(TOP))
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof GasPumpTileEntity)
            {
                GasPumpTileEntity gasPump = (GasPumpTileEntity) tileEntity;
                if (gasPump.getFuelingEntity() != null && gasPump.getFuelingEntity().getEntityId() == playerEntity.getEntityId())
                {
                    gasPump.setFuelingEntity(null);
                    world.playSound(null, pos, ModSounds.NOZZLE_PUT_DOWN.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                else if (state.get(DIRECTION).rotateY().equals(result.getFace()))
                {
                    gasPump.setFuelingEntity(playerEntity);
                    world.playSound(null, pos, ModSounds.NOZZLE_PICK_UP.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
        else
        {
            ItemStack stack = playerEntity.getHeldItem(hand);

            if (FluidUtil.interactWithFluidHandler(playerEntity, hand, world, pos, result.getFace()))
            {
                return ActionResultType.CONSUME;
            }

            if (stack.getItem() instanceof JerryCanItem)
            {
                JerryCanItem jerryCan = (JerryCanItem) stack.getItem();
                if (jerryCan.isFull(stack))
                {
                    return ActionResultType.CONSUME;
                }

                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity != null)
                {
                    IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElse(null);
                    if (handler instanceof FluidTank)
                    {
                        FluidTank tank = (FluidTank) handler;
                        if (tank.getFluid() != null && tank.getFluid().getFluid() != ModFluids.FUELIUM.get())
                        {
                            return ActionResultType.CONSUME;
                        }

                        FluidStack fluidStack = handler.drain(50, IFluidHandler.FluidAction.EXECUTE);
                        if (fluidStack != null)
                        {
                            int remaining = jerryCan.fill(stack, fluidStack.getAmount());
                            if (remaining > 0)
                            {
                                fluidStack.setAmount(remaining);
                                handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                            }
                        }
                        return ActionResultType.CONSUME;
                    }
                }
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader reader, BlockPos pos)
    {
        return reader.isAirBlock(pos) && reader.isAirBlock(pos.up());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        worldIn.setBlockState(pos.up(), state.with(TOP, true));
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (!world.isRemote())
        {
            boolean top = state.get(TOP);
            BlockPos blockpos = pos.offset(top ? Direction.DOWN : Direction.UP);
            BlockState blockstate = world.getBlockState(blockpos);
            if (blockstate.getBlock() == state.getBlock() && blockstate.get(TOP) != top)
            {
                world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
                world.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
            }
        }

        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        if (state.get(TOP))
        {
            Vector3d origin = builder.get(LootParameters.field_237457_g_);
            if (origin != null)
            {
                BlockPos pos = new BlockPos(origin);
                TileEntity tileEntity = builder.getWorld().getTileEntity(pos.down());
                if (tileEntity != null)
                {
                    builder = builder.withParameter(LootParameters.BLOCK_ENTITY, tileEntity);
                }
            }
        }
        return super.getDrops(state, builder);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
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
        if (state.get(TOP))
        {
            return new GasPumpTileEntity();
        }
        return new GasPumpTankTileEntity();
    }
}