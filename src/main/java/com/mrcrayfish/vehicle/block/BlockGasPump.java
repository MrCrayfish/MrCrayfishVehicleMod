package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.ItemJerryCan;
import com.mrcrayfish.vehicle.tileentity.TileEntityGasPump;
import com.mrcrayfish.vehicle.tileentity.TileEntityGasPumpTank;
import com.mrcrayfish.vehicle.util.BlockNames;
import com.mrcrayfish.vehicle.util.Bounds;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
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
    public static final PropertyBool TOP = PropertyBool.create("top");

    private static final AxisAlignedBB[] COLLISION_BOXES = new Bounds(3, 0, 0, 13, 15, 16).getRotatedBounds();
    private static final AxisAlignedBB[] TOP_SELECTION_BOXES = new Bounds(3, -16, 0, 13, 15, 16).getRotatedBounds();
    private static final AxisAlignedBB[] BOTTOM_SELECTION_BOXES = new Bounds(3, 0, 0, 13, 31, 16).getRotatedBounds();

    public BlockGasPump()
    {
        super(Material.ANVIL, BlockNames.GAS_PUMP);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TOP, false));
        this.setHardness(1.0F);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        EnumFacing facing = state.getValue(FACING);
        return state.getValue(TOP) ? TOP_SELECTION_BOXES[facing.getHorizontalIndex()] : BOTTOM_SELECTION_BOXES[facing.getHorizontalIndex()];
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        EnumFacing facing = state.getValue(FACING);
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BOXES[facing.getHorizontalIndex()]);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote)
        {
            if(state.getValue(TOP))
            {
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if(tileEntity instanceof TileEntityGasPump)
                {
                    TileEntityGasPump gasPump = (TileEntityGasPump) tileEntity;
                    if(gasPump.getFuelingEntity() != null && gasPump.getFuelingEntity().getEntityId() == playerIn.getEntityId())
                    {
                        gasPump.setFuelingEntity(null);
                        worldIn.playSound(null, pos, ModSounds.NOZZLE_PUT_DOWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                    else if(state.getValue(FACING).rotateY().equals(face))
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

                if(stack.getItem() instanceof ItemJerryCan)
                {
                    ItemJerryCan jerryCan = (ItemJerryCan) stack.getItem();

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
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos.up(), state.withProperty(TOP, true));
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if(state.getValue(TOP))
        {
            if(worldIn.getBlockState(pos.down()).getBlock() instanceof BlockGasPump)
            {
                worldIn.setBlockToAir(pos.down());
            }
        }
        else
        {
            if(worldIn.getBlockState(pos.up()).getBlock() instanceof BlockGasPump)
            {
                worldIn.setBlockToAir(pos.up());
            }
        }
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getHorizontalIndex() + (state.getValue(TOP) ? 4 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta % 4)).withProperty(TOP, meta / 4 > 0);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, TOP);
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        if(state.getValue(TOP))
        {
            return new TileEntityGasPump();
        }
        return new TileEntityGasPumpTank();
    }
}