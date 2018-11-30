package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.tileentity.TileEntityVehicleCrate;
import com.mrcrayfish.vehicle.util.Bounds;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BlockVehicleCrate extends BlockRotatedObject
{
    private static final List<ResourceLocation> REGISTERED_CRATES = new ArrayList<>();

    private static final AxisAlignedBB PANEL = new Bounds(0, 0, 0, 16, 2, 16).toAABB();

    public BlockVehicleCrate()
    {
        super(Material.IRON, MapColor.SILVER, "vehicle_crate");
        this.setHardness(1.5F);
        this.setResistance(5.0F);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TileEntity tileEntity = source.getTileEntity(pos);
        if(tileEntity instanceof TileEntityVehicleCrate)
        {
            if(((TileEntityVehicleCrate) tileEntity).isOpened())
            {
                return PANEL;
            }
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity instanceof TileEntityVehicleCrate)
        {
            if(((TileEntityVehicleCrate) tileEntity).isOpened())
            {
                Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, PANEL);
                return;
            }
        }
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote && facing == EnumFacing.UP && playerIn.getHeldItem(hand).getItem() == ModItems.WRENCH)
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof TileEntityVehicleCrate)
            {
                ((TileEntityVehicleCrate) tileEntity).open(playerIn.getUniqueID());
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if(!worldIn.isRemote)
        {
            if(placer instanceof EntityPlayer && ((EntityPlayer) placer).capabilities.isCreativeMode)
            {
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if(tileEntity instanceof TileEntityVehicleCrate)
                {
                    ((TileEntityVehicleCrate) tileEntity).open(placer.getUniqueID());
                }
            }
        }
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
        Collections.sort(REGISTERED_CRATES);
        REGISTERED_CRATES.forEach(resourceLocation ->
        {
            NBTTagCompound blockEntityTag = new NBTTagCompound();
            blockEntityTag.setString("vehicle", resourceLocation.toString());
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setTag("BlockEntityTag", blockEntityTag);
            ItemStack stack = new ItemStack(this);
            stack.setTagCompound(itemTag);
            items.add(stack);
        });
    }

    public static ItemStack create(ResourceLocation entityId, int color, EngineTier engineTier)
    {
        NBTTagCompound blockEntityTag = new NBTTagCompound();
        blockEntityTag.setString("vehicle", entityId.toString());
        blockEntityTag.setInteger("color", color);
        blockEntityTag.setInteger("engineTier", engineTier.ordinal());
        NBTTagCompound itemTag = new NBTTagCompound();
        itemTag.setTag("BlockEntityTag", blockEntityTag);
        ItemStack stack = new ItemStack(ModBlocks.VEHICLE_CRATE);
        stack.setTagCompound(itemTag);
        return stack;
    }

    public static void registerVehicle(String id)
    {
        ResourceLocation resource = new ResourceLocation(Reference.MOD_ID, id);
        if(!REGISTERED_CRATES.contains(resource))
        {
            REGISTERED_CRATES.add(resource);
        }
    }
}
