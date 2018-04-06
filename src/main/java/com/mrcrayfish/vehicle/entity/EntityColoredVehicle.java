package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public abstract class EntityColoredVehicle extends EntityVehicle
{
    private static final DataParameter<EnumDyeColor> COLOR = EntityDataManager.createKey(EntityColoredVehicle.class, CustomDataSerializers.DYE_COLOR);

    protected EntityColoredVehicle(World worldIn)
    {
        super(worldIn);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(COLOR, EnumDyeColor.BLUE);
    }

    public void setColor(EnumDyeColor color)
    {
        this.dataManager.set(COLOR, color);
    }

    public EnumDyeColor getColor()
    {
        return this.dataManager.get(COLOR);
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if(!world.isRemote)
        {
            ItemStack heldItem = player.getHeldItem(hand);
            if(!heldItem.isEmpty() && heldItem.getItem() instanceof ItemDye)
            {
                this.setColor(EnumDyeColor.byDyeDamage(heldItem.getItemDamage()));
            }
            else
            {
                player.startRiding(this);
            }
        }
        return true;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if(world.isRemote)
        {
            if(COLOR.equals(key))
            {
                body = new ItemStack(ModItems.ATV_BODY, 1, this.dataManager.get(COLOR).getMetadata());
            }
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        if(compound.hasKey("color", Constants.NBT.TAG_INT))
        {
            this.setColor(EnumDyeColor.byMetadata(compound.getInteger("color")));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setInteger("color", this.getColor().getMetadata());
    }
}
