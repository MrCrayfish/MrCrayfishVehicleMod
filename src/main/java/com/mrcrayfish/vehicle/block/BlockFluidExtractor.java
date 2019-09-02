package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidExtractor;
import com.mrcrayfish.vehicle.util.BlockNames;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Author: MrCrayfish
 */
public class BlockFluidExtractor extends BlockRotatedObject
{
    public BlockFluidExtractor()
    {
        super(Material.ANVIL, BlockNames.FLUID_EXTRACTOR);
        this.setHardness(1.0F);
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
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        if(!world.isRemote && !player.capabilities.isCreativeMode)
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof TileEntityFluidExtractor)
            {
                TileEntityFluidExtractor fluidExtractor = (TileEntityFluidExtractor) tileEntity;
                NBTTagCompound tileEntityTag = new NBTTagCompound();
                tileEntity.writeToNBT(tileEntityTag);
                tileEntityTag.removeTag("x");
                tileEntityTag.removeTag("y");
                tileEntityTag.removeTag("z");
                tileEntityTag.removeTag("id");
                tileEntityTag.removeTag("RemainingFuel");
                tileEntityTag.removeTag("FuelMaxProgress");
                tileEntityTag.removeTag("ExtractionProgress");

                NBTTagCompound compound = new NBTTagCompound();
                compound.setTag("BlockEntityTag", tileEntityTag);

                ItemStack drop = new ItemStack(Item.getItemFromBlock(this));
                drop.setTagCompound(compound);
                if(fluidExtractor.hasCustomName())
                {
                    drop.setStackDisplayName(fluidExtractor.getName());
                }
                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
                return world.setBlockToAir(pos);
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
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
