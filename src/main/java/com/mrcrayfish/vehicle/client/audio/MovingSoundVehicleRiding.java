package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.ref.WeakReference;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class MovingSoundVehicleRiding extends TickableSound
{
    private final WeakReference<PlayerEntity> playerRef;
    private final WeakReference<PoweredVehicleEntity> vehicleRef;

    public MovingSoundVehicleRiding(PlayerEntity player, PoweredVehicleEntity vehicle)
    {
        super(vehicle.getEngineSound(), SoundCategory.NEUTRAL);
        this.playerRef = new WeakReference<>(player);
        this.vehicleRef = new WeakReference<>(vehicle);
        this.attenuation = AttenuationType.NONE;
        this.relative = true;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.001F;
        this.pitch = vehicle.getEnginePitch();
    }

    @Override
    public void tick()
    {
        PoweredVehicleEntity vehicle = this.vehicleRef.get();
        PlayerEntity player = this.playerRef.get();
        if(vehicle == null || player == null)
        {
            this.stop();
            return;
        }
        if(!vehicle.isAlive() || !vehicle.equals(player.getVehicle()) || !player.equals(Minecraft.getInstance().player) || vehicle.getPassengers().size() == 0)
        {
            this.stop();
            return;
        }

        this.volume = MathHelper.lerp(0.2F, this.volume, vehicle.getEngineVolume());
        this.pitch = MathHelper.lerp(0.2F, this.pitch, vehicle.getEnginePitch());
    }
}
