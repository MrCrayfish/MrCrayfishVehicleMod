package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.item.ItemJerryCan;
import com.mrcrayfish.vehicle.tileentity.TileEntityFuelDrum;
import com.mrcrayfish.vehicle.util.Bounds;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BlockFuelDrum extends BlockRotatedObject
{
    private static final AxisAlignedBB BOUNDING_BOX = new Bounds(1, 0, 1, 15, 16, 15).toAABB();

    private int capacity;

    public BlockFuelDrum(String id, int capacity)
    {
        super(Material.IRON, id);
        this.capacity = capacity;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
        if(GuiScreen.isShiftKeyDown())
        {
            String info = I18n.format("vehicle.tile.fuel_drum.info");
            tooltip.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(info, 150));
        }
        else
        {
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.info_help"));
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return BOUNDING_BOX;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote)
        {
            ItemStack stack = playerIn.getHeldItem(hand);
            if(stack.getItem() instanceof ItemJerryCan)
            {
                ItemJerryCan jerryCan = (ItemJerryCan) stack.getItem();

                if(jerryCan.isFull(stack))
                    return false;

                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if(tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
                {
                    IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                    if(handler != null)
                    {
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
        return false;
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
        return new TileEntityFuelDrum(capacity);
    }
}
