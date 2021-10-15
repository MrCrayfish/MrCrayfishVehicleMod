package com.mrcrayfish.vehicle.entity.vehicle;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.common.VehicleTextComponents;
import com.mrcrayfish.vehicle.common.inventory.BasicStorage;
import com.mrcrayfish.vehicle.common.inventory.IMultiStorage;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachChest;
import com.mrcrayfish.vehicle.network.message.MessageOpenMultiStorage;
import com.mrcrayfish.vehicle.network.message.MessageOpenStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class SportsCarEntity extends LandVehicleEntity implements IMultiStorage
{
    private final ImmutableMap<String, IStorage> storageMap;

    public SportsCarEntity(EntityType<? extends LandVehicleEntity> type, World worldIn)
    {
        super(type, worldIn);
        ImmutableMap.Builder<String, IStorage> builder = ImmutableMap.builder();
        builder.put("GloveBox", new BasicStorage(VehicleTextComponents.GLOVE_BOX, 1));
        builder.put("Trunk", new BasicStorage(VehicleTextComponents.TRUNK, 3));
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
                PacketHandler.getPlayChannel().sendToServer(new MessageOpenMultiStorage(entity.getId(), "GloveBox"));
                Minecraft.getInstance().player.swing(Hand.MAIN_HAND);
            }
        }, entity -> true);

        EntityRayTracer.instance().registerInteractionBox(ModEntities.SPORTS_CAR.get(), () -> {
            return createScaledBoundingBox(-7.0, 4.0, -12.0, 7.0, 7.0, -19.0, 0.0625);
        }, (entity, rightClick) -> {
            if(rightClick) {
                PacketHandler.getPlayChannel().sendToServer(new MessageOpenMultiStorage(entity.getId(), "Trunk"));
                Minecraft.getInstance().player.swing(Hand.MAIN_HAND);
            }
        }, entity -> true);
    }

    @Override
    public Map<String, IStorage> getStorageInventories()
    {
        return this.storageMap;
    }
}
