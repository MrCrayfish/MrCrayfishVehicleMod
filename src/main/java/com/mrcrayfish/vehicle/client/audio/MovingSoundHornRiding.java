package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.ref.WeakReference;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class MovingSoundHornRiding extends TickableSound
{
    private final WeakReference<PlayerEntity> playerRef;
    private final WeakReference<PoweredVehicleEntity> vehicleRef;

    public MovingSoundHornRiding(PlayerEntity player, PoweredVehicleEntity vehicle)
    {
        super(vehicle.getHornRidingSound(), SoundCategory.NEUTRAL);
        this.playerRef = new WeakReference<>(player);
        this.vehicleRef = new WeakReference<>(vehicle);
        this.attenuationType = AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.001F;
        this.pitch = 0.85F;
    }

    @Override
    public void tick()
    {
        PoweredVehicleEntity vehicle = this.vehicleRef.get();
        PlayerEntity player = this.playerRef.get();
        if(vehicle == null || player == null)
        {
            this.finishPlaying();
            return;
        }
        if(!vehicle.isAlive() || player.getRidingEntity() == null || player.getRidingEntity() != vehicle || !player.equals(Minecraft.getInstance().player) || vehicle.getPassengers().size() == 0)
        {
            this.finishPlaying();
            return;
        }
        this.volume = vehicle.getHorn() ? 1.0F : 0.0F;
    }
}
