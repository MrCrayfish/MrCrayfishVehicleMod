package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.tileentity.TileEntityVehicleCrate;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

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

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
        String vehicle = "vehicle";
        NBTTagCompound tagCompound = stack.getTagCompound();
        if(tagCompound != null)
        {
            if(tagCompound.hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
            {
                NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
                vehicle = blockEntityTag.getString("vehicle");
                vehicle = I18n.format("entity.vehicle." + vehicle.split(":")[1] + ".name");
                tooltip.add(TextFormatting.BLUE + vehicle);
            }
        }

        if(GuiScreen.isShiftKeyDown())
        {
            String info = I18n.format("vehicle.tile.vehicle_crate.info", vehicle);
            tooltip.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(info, 150));
        }
        else
        {
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.info_help"));
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
        ForgeRegistries.ENTITIES.getValuesCollection().forEach(entityEntry ->
        {
            ResourceLocation name = entityEntry.getRegistryName();
            if(name != null)
            {
                if(name.getResourceDomain().equals(Reference.MOD_ID))
                {
                    NBTTagCompound blockEntityTag = new NBTTagCompound();
                    blockEntityTag.setString("vehicle", name.toString());

                    NBTTagCompound itemTag = new NBTTagCompound();
                    itemTag.setTag("BlockEntityTag", blockEntityTag);

                    ItemStack stack = new ItemStack(this);
                    stack.setTagCompound(itemTag);
                    items.add(stack);
                }
            }
        });
    }
}
