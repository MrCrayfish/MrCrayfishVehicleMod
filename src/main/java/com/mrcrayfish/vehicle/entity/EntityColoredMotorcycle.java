package com.mrcrayfish.vehicle.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public abstract class EntityColoredMotorcycle extends EntityMotorcycle
{
    protected static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);

    public EntityColoredMotorcycle(World worldIn)
    {
        super(worldIn);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(COLOR, EnumDyeColor.BLUE.getMetadata());
    }

    public void setColor(EnumDyeColor color)
    {
        this.dataManager.set(COLOR, color.getMetadata());
    }

    public EnumDyeColor getColor()
    {
        return EnumDyeColor.byMetadata(this.dataManager.get(COLOR));
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
                body.setItemDamage(this.dataManager.get(COLOR));
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
