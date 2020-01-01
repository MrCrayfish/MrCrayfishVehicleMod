package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.util.Bounds;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BlockGasPump extends BlockRotatedObject
{
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    private static final AxisAlignedBB[] COLLISION_BOXES = new Bounds(3, 0, 0, 13, 15, 16).getRotatedBounds();
    private static final AxisAlignedBB[] TOP_SELECTION_BOXES = new Bounds(3, -16, 0, 13, 15, 16).getRotatedBounds();
    private static final AxisAlignedBB[] BOTTOM_SELECTION_BOXES = new Bounds(3, 0, 0, 13, 31, 16).getRotatedBounds();

    public BlockGasPump()
    {
        super(Names.Block.GAS_PUMP, Block.Properties.create(Material.ANVIL).hardnessAndResistance(1.0F));
        this.setDefaultState(this.getStateContainer().getBaseState().with(DIRECTION, Direction.NORTH).with(TOP, false));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, Direction face, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote)
        {
            if(state.getValue(TOP))
            {
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if(tileEntity instanceof GasPumpTileEntity)
                {
                    GasPumpTileEntity gasPump = (GasPumpTileEntity) tileEntity;
                    if(gasPump.getFuelingEntity() != null && gasPump.getFuelingEntity().getEntityId() == playerIn.getEntityId())
                    {
                        gasPump.setFuelingEntity(null);
                        worldIn.playSound(null, pos, ModSounds.NOZZLE_PUT_DOWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                    else if(state.getValue(DIRECTION).rotateY().equals(face))
                    {
                        gasPump.setFuelingEntity(playerIn);
                        worldIn.playSound(null, pos, ModSounds.NOZZLE_PICK_UP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                }
            }
            else
            {
                ItemStack stack = playerIn.getHeldItem(hand);

                if(FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, face))
                {
                    return true;
                }

                if(stack.getItem() instanceof JerryCanItem)
                {
                    JerryCanItem jerryCan = (JerryCanItem) stack.getItem();

                    if(jerryCan.isFull(stack))
                    {
                        return false;
                    }

                    TileEntity tileEntity = worldIn.getTileEntity(pos);
                    if(tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
                    {
                        IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                        if(handler != null)
                        {
                            if(handler instanceof FluidTank)
                            {
                                FluidTank tank = (FluidTank) handler;
                                if(tank.getFluid() != null && tank.getFluid().getFluid() != ModFluids.FUELIUM)
                                {
                                    return false;
                                }

                                FluidStack fluidStack = handler.drain(50, true);
                                if(fluidStack != null)
                                {
                                    int remaining = jerryCan.fill(stack, fluidStack.amount);
                                    if(remaining > 0)
                                    {
                                        fluidStack.amount = remaining;
                                        handler.fill(fluidStack, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.isAirBlock(pos) && worldIn.isAirBlock(pos.up());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        worldIn.setBlockState(pos.up(), state.with(TOP, true));
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if(state.get(TOP))
        {
            if(worldIn.getBlockState(pos.down()).getBlock() instanceof BlockGasPump)
            {
                worldIn.setBlockState(pos.down(), Blocks.AIR.getDefaultState());
            }
        }
        else
        {
            if(worldIn.getBlockState(pos.up()).getBlock() instanceof BlockGasPump)
            {
                worldIn.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
            }
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(DIRECTION);
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
        if(state.get(TOP))
        {
            return new GasPumpTileEntity();
        }
        return new GasPumpTankTileEntity();
    }
}