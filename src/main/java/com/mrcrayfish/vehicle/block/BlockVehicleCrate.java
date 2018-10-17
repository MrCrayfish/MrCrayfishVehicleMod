package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.tileentity.TileEntityVehicleCrate;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockVehicleCrate extends Block
{
    public BlockVehicleCrate()
    {
        super(Material.IRON, MapColor.SILVER);
        this.setUnlocalizedName("vehicle_crate");
        this.setRegistryName("vehicle_crate");
        this.setHardness(1.5F);
        this.setResistance(5.0F);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote && facing == EnumFacing.UP && playerIn.getHeldItem(hand).getItem() == ModItems.WRENCH)
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof TileEntityVehicleCrate)
            {
                ((TileEntityVehicleCrate) tileEntity).open();
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
        return new TileEntityVehicleCrate();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}
