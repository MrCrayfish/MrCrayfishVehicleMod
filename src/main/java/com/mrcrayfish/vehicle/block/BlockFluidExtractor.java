package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.item.ItemJerryCan;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidExtractor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockFluidExtractor extends BlockRotatedObject
{
    public BlockFluidExtractor()
    {
        super(Material.ANVIL, "fluid_extractor");
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote)
        {
            ItemStack stack = playerIn.getHeldItem(hand);
            if(stack.getItem() == Items.BUCKET)
            {
                if(FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing))
                {
                    TileEntity tileEntity = worldIn.getTileEntity(pos);
                    if(tileEntity instanceof TileEntityFluidExtractor)
                    {
                        ((TileEntityFluidExtractor) tileEntity).syncFluidLevelToClients();
                    }
                }
                return true;
            }

            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof TileEntityFluidExtractor)
            {
                playerIn.openGui(VehicleMod.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
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
        return new TileEntityFluidExtractor();
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
}
