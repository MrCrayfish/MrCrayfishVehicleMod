package com.mrcrayfish.vehicle.entity.vehicle;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.common.VehicleTextComponents;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageOpenStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class SportsCarEntity extends LandVehicleEntity implements IStorage
{
    private static final String GLOVE_BOX_STORAGE_KEY = "GloveBox";
    private static final String TRUNK_STORAGE_KEY = "Trunk";

    private final ImmutableMap<String, StorageInventory> storageMap;

    public SportsCarEntity(EntityType<? extends LandVehicleEntity> type, World worldIn)
    {
        super(type, worldIn);
        ImmutableMap.Builder<String, StorageInventory> builder = ImmutableMap.builder();
        builder.put(GLOVE_BOX_STORAGE_KEY, new StorageInventory(this, VehicleTextComponents.GLOVE_BOX, 1));
        builder.put(TRUNK_STORAGE_KEY, new StorageInventory(this, VehicleTextComponents.TRUNK, 3));
        this.storageMap = builder.build();
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound)
    {
        super.readAdditionalSaveData(compound);
        this.readInventories(compound);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound)
    {
        super.addAdditionalSaveData(compound);
        this.writeInventories(compound);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerInteractionBoxes()
    {
        EntityRayTracer.instance().registerInteractionBox(ModEntities.SPORTS_CAR.get(), () -> {
            return createScaledBoundingBox(2.0, 3.5, 5.0, 8.0, 7.5, 3.0, 0.0625);
        }, (entity, rightClick) -> {
            if(rightClick) {
                PacketHandler.getPlayChannel().sendToServer(new MessageOpenStorage(entity.getId(), GLOVE_BOX_STORAGE_KEY));
                Minecraft.getInstance().player.swing(Hand.MAIN_HAND);
            }
        }, entity -> true);

        EntityRayTracer.instance().registerInteractionBox(ModEntities.SPORTS_CAR.get(), () -> {
            return createScaledBoundingBox(-7.0, 4.0, -12.0, 7.0, 7.0, -19.0, 0.0625);
        }, (entity, rightClick) -> {
            if(rightClick) {
                PacketHandler.getPlayChannel().sendToServer(new MessageOpenStorage(entity.getId(), TRUNK_STORAGE_KEY));
                Minecraft.getInstance().player.swing(Hand.MAIN_HAND);
            }
        }, entity -> true);
    }

    @Override
    public Map<String, StorageInventory> getStorageInventories()
    {
        return this.storageMap;
    }
}
