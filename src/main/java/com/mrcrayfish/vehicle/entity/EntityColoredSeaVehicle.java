package com.mrcrayfish.vehicle.entity;

import java.awt.Color;

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
public abstract class EntityColoredSeaVehicle extends EntitySeaVehicle
{
    protected static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntitySeaVehicle.class, DataSerializers.VARINT);

    public EntityColoredSeaVehicle(World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void entityInit()
    {
        super.entityInit();
        this.dataManager.register(COLOR, 0xFFFFFF);
    }

    public void setColor(int color)
    {
        this.dataManager.set(COLOR, color);
    }

    public Color getColor()
    {
        return new Color(this.dataManager.get(COLOR));
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if(!world.isRemote)
        {
            ItemStack heldItem = player.getHeldItem(hand);
            if(!heldItem.isEmpty() && heldItem.getItem() instanceof ItemDye)
            {
                this.setColor(EnumDyeColor.byDyeDamage(heldItem.getItemDamage()).getColorValue());
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
            	NBTTagCompound nbt;
            	if(body.hasTagCompound()) {
            		nbt = body.getTagCompound(); 
            	} else {
            		nbt = new NBTTagCompound();
            	}
            	nbt.setInteger("color", this.dataManager.get(COLOR));
                body.setTagCompound(nbt);
            }
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        if(compound.hasKey("color", Constants.NBT.TAG_INT))
        {
            this.setColor(compound.getInteger("color"));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("color", this.getColor().getRGB());
    }
}
