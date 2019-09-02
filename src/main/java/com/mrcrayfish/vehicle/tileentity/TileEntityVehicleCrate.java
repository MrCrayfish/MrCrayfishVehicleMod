package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.WheelType;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
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
    private int color = EntityVehicle.DYE_TO_COLOR[0];
    private EngineTier engineTier = null;
    private WheelType wheelType = null;
    private int wheelColor = -1;
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
                    entity = EntityList.createEntityByIDFromName(entityId, world);
                    if(entity != null)
                    {
                        VehicleMod.proxy.playSound(SoundEvents.ENTITY_ITEM_BREAK, pos, 1.0F, 0.5F);
                        List<EntityDataManager.DataEntry<?>> entryList = entity.getDataManager().getAll();
                        if(entryList != null)
                        {
                            entryList.forEach(dataEntry -> entity.notifyDataManagerChange(dataEntry.getKey()));
                        }
                        if(entity instanceof EntityVehicle)
                        {
                            ((EntityVehicle) entity).setColor(color);
                        }
                        if(entity instanceof EntityPoweredVehicle)
                        {
                            EntityPoweredVehicle entityPoweredVehicle = (EntityPoweredVehicle) entity;

                            if(engineTier != null)
                            {
                                entityPoweredVehicle.setEngine(true);
                                entityPoweredVehicle.setEngineTier(engineTier);
                            }

                            if(wheelType != null)
                            {
                                 entityPoweredVehicle.setWheels(true);
                                 entityPoweredVehicle.setWheelType(wheelType);
                                 if(wheelColor != -1)
                                 {
                                     entityPoweredVehicle.setWheelColor(wheelColor);
                                 }
                            }
                            else
                            {
                                entityPoweredVehicle.setWheels(false);
                            }
                        }
                    }
                    else
                    {
                        entityId = null;
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
                IBlockState state = world.getBlockState(pos);
                EnumFacing facing = state.getValue(BlockVehicleCrate.FACING);

                Entity entity = EntityList.createEntityByIDFromName(entityId, world);
                if(entity != null)
                {
                    if(entity instanceof EntityVehicle)
                    {
                        ((EntityVehicle) entity).setColor(color);
                    }
                    if(opener != null && entity instanceof EntityPoweredVehicle)
                    {
                        EntityPoweredVehicle poweredVehicle = (EntityPoweredVehicle) entity;
                        poweredVehicle.setOwner(opener);
                        if(engineTier != null)
                        {
                            poweredVehicle.setEngine(true);
                            poweredVehicle.setEngineTier(engineTier);
                        }
                        if(wheelType != null)
                        {
                            poweredVehicle.setWheelType(wheelType);
                            if(wheelColor != -1)
                            {
                                poweredVehicle.setWheelColor(wheelColor);
                            }
                        }
                    }
                    entity.setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, facing.getHorizontalIndex() * 90F + 180F, 0F);
                    entity.setRotationYawHead(facing.getHorizontalIndex() * 90F + 180F);
                    world.spawnEntity(entity);
                }
                world.setBlockToAir(pos);
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
        compound.setInteger("color", color);
        if(engineTier != null)
        {
            compound.setInteger("engineTier", engineTier.ordinal());
        }
        if(wheelType != null)
        {
            compound.setInteger("wheelType", wheelType.ordinal());
            if(wheelColor != -1)
            {
                compound.setInteger("wheelColor", wheelColor);
            }
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
        if(compound.hasKey("color", Constants.NBT.TAG_INT))
        {
            color = compound.getInteger("color");
        }
        if(compound.hasKey("engineTier", Constants.NBT.TAG_INT))
        {
            engineTier = EngineTier.getType(compound.getInteger("engineTier"));
        }
        if(compound.hasKey("wheelType", Constants.NBT.TAG_INT))
        {
            wheelType = WheelType.getType(compound.getInteger("wheelType"));
        }
        if(compound.hasKey("wheelColor", Constants.NBT.TAG_INT))
        {
            wheelColor = compound.getInteger("wheelColor");
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

    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 65536.0D;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }
}
