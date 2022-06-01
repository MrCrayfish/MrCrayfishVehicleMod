package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.client.model.IComplexModel;
import com.mrcrayfish.vehicle.client.model.VehicleModels;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public enum FuelFillerType
{
    DEFAULT(() -> VehicleModels.FUEL_DOOR_CLOSED, () -> VehicleModels.FUEL_DOOR_OPEN, ModSounds.ENTITY_VEHICLE_FUEL_PORT_LARGE_OPEN.get(), 0.25F, 0.6F, ModSounds.ENTITY_VEHICLE_FUEL_PORT_LARGE_CLOSE.get(), 0.12F, 0.6F),
    SMALL(() -> VehicleModels.SMALL_FUEL_DOOR_CLOSED, () -> VehicleModels.SMALL_FUEL_DOOR_OPEN, ModSounds.ENTITY_VEHICLE_FUEL_PORT_SMALL_OPEN.get(), 0.4F, 0.6F, ModSounds.ENTITY_VEHICLE_FUEL_PORT_SMALL_CLOSE.get(), 0.3F, 0.6F);

    private final Supplier<Object> closedModel;
    private final Supplier<Object> openModel;
    private final SoundEvent openSound;
    private final SoundEvent closeSound;
    private final float openVolume;
    private final float closeVolume;
    private final float openPitch;
    private final float closePitch;

    FuelFillerType(Supplier<Object> closedModel, Supplier<Object> openModel, SoundEvent openSound, float openVolume, float openPitch, SoundEvent closeCount, float closeVolume, float closePitch)
    {
        this.closedModel = closedModel;
        this.openModel = openModel;
        this.openSound = openSound;
        this.openVolume = openVolume;
        this.openPitch = openPitch;
        this.closeSound = closeCount;
        this.closeVolume = closeVolume;
        this.closePitch = closePitch;
    }

    public Supplier<Object> getClosedModel()
    {
        return this.closedModel;
    }

    public Supplier<Object> getOpenModel()
    {
        return this.openModel;
    }

    @OnlyIn(Dist.CLIENT)
    public void playOpenSound()
    {
        VehicleHelper.playSound(this.openSound, this.openVolume, this.openPitch);
    }

    @OnlyIn(Dist.CLIENT)
    public void playCloseSound()
    {
        VehicleHelper.playSound(this.closeSound, this.closeVolume, this.closePitch);
    }
}
