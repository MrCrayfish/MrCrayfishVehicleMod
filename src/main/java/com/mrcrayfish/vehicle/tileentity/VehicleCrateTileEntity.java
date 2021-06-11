package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.WheelType;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class VehicleCrateTileEntity extends TileEntitySynced implements ITickableTileEntity
{
    private static final Random RAND = new Random();

    private ResourceLocation entityId;
    private int color = VehicleEntity.DYE_TO_COLOR[0];
    private EngineTier engineTier = null;
    private WheelType wheelType = null;
    private int wheelColor = -1;
    private boolean opened = false;
    private int timer;
    private UUID opener;

    @OnlyIn(Dist.CLIENT)
    private Entity entity;

    public VehicleCrateTileEntity()
    {
        super(ModTileEntities.VEHICLE_CRATE.get());
    }

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

    @OnlyIn(Dist.CLIENT)
    public <E extends Entity> E getEntity()
    {
        return (E) entity;
    }

    @Override
    public void tick()
    {
        if(this.opened)
        {
            this.timer += 5;
            if(this.world != null && this.world.isRemote())
            {
                if(this.entityId != null && this.entity == null)
                {
                    EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(this.entityId);
                    if(entityType != null)
                    {
                        this.entity = entityType.create(this.world);
                        if(this.entity != null)
                        {
                            VehicleHelper.playSound(SoundEvents.ENTITY_ITEM_BREAK, this.pos, 1.0F, 0.5F);
                            List<EntityDataManager.DataEntry<?>> entryList = this.entity.getDataManager().getAll();
                            if(entryList != null)
                            {
                                entryList.forEach(dataEntry -> this.entity.notifyDataManagerChange(dataEntry.getKey()));
                            }
                            if(this.entity instanceof VehicleEntity)
                            {
                                ((VehicleEntity) this.entity).setColor(this.color);
                            }
                            if(this.entity instanceof PoweredVehicleEntity)
                            {
                                PoweredVehicleEntity entityPoweredVehicle = (PoweredVehicleEntity) this.entity;
                                if(this.engineTier != null)
                                {
                                    entityPoweredVehicle.setEngine(true);
                                    entityPoweredVehicle.setEngineTier(this.engineTier);
                                }
                                if(this.wheelType != null)
                                {
                                    entityPoweredVehicle.setWheels(true);
                                    entityPoweredVehicle.setWheelType(this.wheelType);
                                    if(this.wheelColor != -1)
                                    {
                                        entityPoweredVehicle.setWheelColor(this.wheelColor);
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
                            this.entityId = null;
                        }
                    }
                    else
                    {
                        this.entityId = null;
                    }
                }
                if(this.timer == 90 || this.timer == 110 || this.timer == 130 || this.timer == 150)
                {
                    float pitch = (float) (0.9F + 0.2F * RAND.nextDouble());
                    VehicleHelper.playSound(ModSounds.VEHICLE_CRATE_PANEL_LAND.get(), this.pos, 1.0F, pitch);
                }
                if(this.timer == 150)
                {
                    VehicleHelper.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, this.pos, 1.0F, 1.0F);
                    this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, false, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, 0, 0, 0);
                }
            }
            if(!this.world.isRemote && this.timer > 250)
            {
                BlockState state = this.world.getBlockState(this.pos);
                Direction facing = state.get(BlockVehicleCrate.DIRECTION);
                EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(this.entityId);
                if(entityType != null)
                {
                    Entity entity = entityType.create(this.world);
                    if(entity != null)
                    {
                        if(entity instanceof VehicleEntity)
                        {
                            ((VehicleEntity) entity).setColor(this.color);
                        }
                        if(this.opener != null && entity instanceof PoweredVehicleEntity)
                        {
                            PoweredVehicleEntity poweredVehicle = (PoweredVehicleEntity) entity;
                            poweredVehicle.setOwner(this.opener);
                            if(this.engineTier != null)
                            {
                                poweredVehicle.setEngine(true);
                                poweredVehicle.setEngineTier(this.engineTier);
                            }
                            if(this.wheelType != null)
                            {
                                poweredVehicle.setWheelType(this.wheelType);
                                if(this.wheelColor != -1)
                                {
                                    poweredVehicle.setWheelColor(this.wheelColor);
                                }
                            }
                        }
                        entity.setPositionAndRotation(this.pos.getX() + 0.5, this.pos.getY(), this.pos.getZ() + 0.5, facing.getHorizontalIndex() * 90F + 180F, 0F);
                        entity.setRotationYawHead(facing.getHorizontalIndex() * 90F + 180F);
                        this.world.addEntity(entity);
                    }
                    this.world.setBlockState(this.pos, Blocks.AIR.getDefaultState());
                }
            }
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT compound)
    {
        super.read(state, compound);
        if(compound.contains("Vehicle", Constants.NBT.TAG_STRING))
        {
            this.entityId = new ResourceLocation(compound.getString("Vehicle"));
        }
        if(compound.contains("Color", Constants.NBT.TAG_INT))
        {
            this.color = compound.getInt("Color");
        }
        if(compound.contains("EngineTier", Constants.NBT.TAG_INT))
        {
            this.engineTier = EngineTier.getType(compound.getInt("EngineTier"));
        }
        if(compound.contains("WheelType", Constants.NBT.TAG_INT))
        {
            this.wheelType = WheelType.getType(compound.getInt("WheelType"));
        }
        if(compound.contains("WheelColor", Constants.NBT.TAG_INT))
        {
            this.wheelColor = compound.getInt("WheelColor");
        }
        if(compound.contains("Opener", Constants.NBT.TAG_STRING))
        {
            this.opener = compound.getUniqueId("Opener");
        }
        if(compound.contains("Opened", Constants.NBT.TAG_BYTE))
        {
            this.opened = compound.getBoolean("Opened");
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        if(this.entityId != null)
        {
            compound.putString("Vehicle", this.entityId.toString());
        }
        if(this.opener != null)
        {
            compound.putUniqueId("Opener", this.opener);
        }
        if(this.engineTier != null)
        {
            compound.putInt("EngineTier", this.engineTier.ordinal());
        }
        if(this.wheelType != null)
        {
            compound.putInt("WheelType", this.wheelType.ordinal());
            if(this.wheelColor != -1)
            {
                compound.putInt("WheelColor", this.wheelColor);
            }
        }
        compound.putInt("Color", this.color);
        compound.putBoolean("Opened", this.opened);
        return super.write(compound);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 65536.0D;
    }
}
