package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.util.CommonUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
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
    private ItemStack engineStack = ItemStack.EMPTY;
    private ItemStack wheelStack = ItemStack.EMPTY;
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
        this.setChanged();
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
            if(this.level != null && this.level.isClientSide())
            {
                if(this.entityId != null && this.entity == null)
                {
                    EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(this.entityId);
                    if(entityType != null)
                    {
                        this.entity = entityType.create(this.level);
                        if(this.entity != null)
                        {
                            VehicleHelper.playSound(SoundEvents.ITEM_BREAK, this.worldPosition, 1.0F, 0.5F);
                            List<EntityDataManager.DataEntry<?>> entryList = this.entity.getEntityData().getAll();
                            if(entryList != null)
                            {
                                entryList.forEach(dataEntry -> this.entity.onSyncedDataUpdated(dataEntry.getAccessor()));
                            }
                            if(this.entity instanceof VehicleEntity)
                            {
                                ((VehicleEntity) this.entity).setColor(this.color);
                            }
                            if(this.entity instanceof PoweredVehicleEntity)
                            {
                                PoweredVehicleEntity entityPoweredVehicle = (PoweredVehicleEntity) this.entity;
                                if(this.engineStack != null)
                                {
                                    entityPoweredVehicle.setEngineStack(this.engineStack);
                                }
                                if(!this.wheelStack.isEmpty())
                                {
                                    entityPoweredVehicle.setWheelStack(this.wheelStack);
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
                    VehicleHelper.playSound(ModSounds.BLOCK_VEHICLE_CRATE_PANEL_LAND.get(), this.worldPosition, 1.0F, pitch);
                }
                if(this.timer == 150)
                {
                    VehicleHelper.playSound(SoundEvents.GENERIC_EXPLODE, this.worldPosition, 1.0F, 1.0F);
                    this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, false, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, 0, 0, 0);
                }
            }
            if(!this.level.isClientSide && this.timer > 250)
            {
                BlockState state = this.level.getBlockState(this.worldPosition);
                Direction facing = state.getValue(VehicleCrateBlock.DIRECTION);
                EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(this.entityId);
                if(entityType != null)
                {
                    Entity entity = entityType.create(this.level);
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
                            if(!this.engineStack.isEmpty())
                            {
                                poweredVehicle.setEngineStack(this.engineStack);
                            }
                            if(!this.wheelStack.isEmpty())
                            {
                                poweredVehicle.setWheelStack(this.wheelStack);
                            }
                        }
                        entity.absMoveTo(this.worldPosition.getX() + 0.5, this.worldPosition.getY(), this.worldPosition.getZ() + 0.5, facing.get2DDataValue() * 90F + 180F, 0F);
                        entity.setYHeadRot(facing.get2DDataValue() * 90F + 180F);
                        this.level.addFreshEntity(entity);
                    }
                    this.level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT compound)
    {
        super.load(state, compound);
        if(compound.contains("Vehicle", Constants.NBT.TAG_STRING))
        {
            this.entityId = new ResourceLocation(compound.getString("Vehicle"));
        }
        if(compound.contains("Color", Constants.NBT.TAG_INT))
        {
            this.color = compound.getInt("Color");
        }
        if(compound.contains("EngineStack", Constants.NBT.TAG_COMPOUND))
        {
            this.engineStack = ItemStack.of(compound.getCompound("EngineStack"));
        }
        else if(compound.getBoolean("Creative"))
        {
            VehicleProperties properties = VehicleProperties.get(this.entityId);
            EngineItem engineItem = VehicleRegistry.getEngineItem(properties.getEngineType(), EngineTier.IRON);
            this.engineStack = engineItem != null ? new ItemStack(engineItem) : ItemStack.EMPTY;
        }
        if(compound.contains("WheelStack", Constants.NBT.TAG_COMPOUND))
        {
            this.wheelStack = ItemStack.of(compound.getCompound("WheelStack"));
        }
        else
        {
            this.wheelStack = new ItemStack(ModItems.STANDARD_WHEEL.get());
        }
        if(compound.contains("Opener", Constants.NBT.TAG_STRING))
        {
            this.opener = compound.getUUID("Opener");
        }
        if(compound.contains("Opened", Constants.NBT.TAG_BYTE))
        {
            this.opened = compound.getBoolean("Opened");
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound)
    {
        if(this.entityId != null)
        {
            compound.putString("Vehicle", this.entityId.toString());
        }
        if(this.opener != null)
        {
            compound.putUUID("Opener", this.opener);
        }
        if(!this.engineStack.isEmpty())
        {
            CommonUtils.writeItemStackToTag(compound, "EngineStack", this.engineStack);
        }
        if(!this.wheelStack.isEmpty())
        {
            CommonUtils.writeItemStackToTag(compound, "WheelStack", this.wheelStack);
        }
        compound.putInt("Color", this.color);
        compound.putBoolean("Opened", this.opened);
        return super.save(compound);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public double getViewDistance()
    {
        return 65536.0D;
    }
}
