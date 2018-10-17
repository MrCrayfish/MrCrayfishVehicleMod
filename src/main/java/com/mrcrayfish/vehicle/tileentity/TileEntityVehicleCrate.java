package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class TileEntityVehicleCrate extends TileEntitySynced implements ITickable
{
    private static final Random RAND = new Random();

    private ResourceLocation entityId;
    private boolean opened = false;
    private int timer;
    private UUID opener;

    @SideOnly(Side.CLIENT)
    private Entity entity;

    public void setEntityId(ResourceLocation entityId)
    {
        this.entityId = entityId;
        this.markDirty();
    }

    public ResourceLocation getEntityId()
    {
        return entityId;
    }

    public void open(UUID opener)
    {
        if(this.entityId != null)
        {
            this.opened = true;
            this.opener = opener;
            this.syncToClient();
        }
    }

    public boolean isOpened()
    {
        return opened;
    }

    public int getTimer()
    {
        return timer;
    }

    @SideOnly(Side.CLIENT)
    public Entity getEntity()
    {
        return entity;
    }

    @Override
    public void update()
    {
        if(opened)
        {
            timer += 5;
            if(world.isRemote)
            {
                if(entityId != null && entity == null)
                {
                    VehicleMod.proxy.playSound(SoundEvents.ENTITY_ITEM_BREAK, pos, 1.0F, 0.5F);
                    entity = EntityList.createEntityByIDFromName(entityId, world);
                    if(entity != null)
                    {
                        List<EntityDataManager.DataEntry<?>> entryList = entity.getDataManager().getAll();
                        if(entryList != null)
                        {
                            entryList.forEach(dataEntry -> entity.notifyDataManagerChange(dataEntry.getKey()));
                        }
                    }
                }
                if(timer == 90 || timer == 110 || timer == 130 || timer == 150)
                {
                    float pitch = (float) (0.9F + 0.2F * RAND.nextDouble());
                    VehicleMod.proxy.playSound(ModSounds.VEHICLE_CRATE_PANEL_LAND, pos, 1.0F, pitch);
                }
                if(timer == 150)
                {
                    VehicleMod.proxy.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, pos, 1.0F, 1.0F);
                    world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, false, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
                }
            }
            if(!world.isRemote && timer > 250)
            {
                Entity entity = EntityList.createEntityByIDFromName(entityId, world);
                if(entity != null)
                {
                    if(opener != null && entity instanceof EntityPoweredVehicle)
                    {
                        ((EntityPoweredVehicle) entity).setOwner(opener);
                    }
                    entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    world.spawnEntity(entity);
                    world.setBlockToAir(pos);
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        if(entityId != null)
        {
            compound.setString("vehicle", entityId.toString());
        }
        if(opener != null)
        {
            compound.setUniqueId("opener", opener);
        }
        compound.setBoolean("opened", opened);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(compound.hasKey("vehicle", Constants.NBT.TAG_STRING))
        {
            entityId = new ResourceLocation(compound.getString("vehicle"));
        }
        if(compound.hasKey("opener", Constants.NBT.TAG_STRING))
        {
            opener = compound.getUniqueId("opener");
        }
        if(compound.hasKey("opened", Constants.NBT.TAG_BYTE))
        {
            opened = compound.getBoolean("opened");
        }
    }
}
