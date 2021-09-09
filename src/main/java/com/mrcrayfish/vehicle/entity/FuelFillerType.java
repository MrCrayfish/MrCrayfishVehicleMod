package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
public enum FuelFillerType
{
    DEFAULT(SpecialModels.FUEL_DOOR_CLOSED, SpecialModels.FUEL_DOOR_OPEN, ModSounds.ENTITY_VEHICLE_FUEL_PORT_LARGE_OPEN.get(), 0.25F, 0.6F, ModSounds.ENTITY_VEHICLE_FUEL_PORT_LARGE_CLOSE.get(), 0.12F, 0.6F),
    SMALL(SpecialModels.SMALL_FUEL_DOOR_CLOSED, SpecialModels.SMALL_FUEL_DOOR_OPEN, ModSounds.ENTITY_VEHICLE_FUEL_PORT_SMALL_OPEN.get(), 0.4F, 0.6F, ModSounds.ENTITY_VEHICLE_FUEL_PORT_SMALL_CLOSE.get(), 0.3F, 0.6F);

    private ISpecialModel closed;
    private ISpecialModel open;
    private SoundEvent openSound;
    private SoundEvent closeSound;
    private float openVolume;
    private float closeVolume;
    private float openPitch;
    private float closePitch;

    FuelFillerType(ISpecialModel closed, ISpecialModel open, SoundEvent openSound, float openVolume, float openPitch, SoundEvent closeCount, float closeVolume, float closePitch)
    {
        this.closed = closed;
        this.open = open;
        this.openSound = openSound;
        this.openVolume = openVolume;
        this.openPitch = openPitch;
        this.closeSound = closeCount;
        this.closeVolume = closeVolume;
        this.closePitch = closePitch;
    }

    public ISpecialModel getClosedModel()
    {
        return closed;
    }

    public ISpecialModel getOpenModel()
    {
        return open;
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
