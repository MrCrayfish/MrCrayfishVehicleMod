package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.ref.WeakReference;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class MovingEngineSound extends TickableSound
{
    private final WeakReference<PlayerEntity> playerRef;
    private final WeakReference<PoweredVehicleEntity> vehicleRef;

    public MovingEngineSound(PlayerEntity player, PoweredVehicleEntity vehicle)
    {
        super(vehicle.getEngineSound(), SoundCategory.NEUTRAL);
        this.playerRef = new WeakReference<>(player);
        this.vehicleRef = new WeakReference<>(vehicle);
        this.volume = 0.0F;
        this.pitch = 0.5F;
        this.looping = true;
        this.delay = 0;
    }

    @Override
    public boolean canStartSilent()
    {
        return true;
    }

    @Override
    public void tick()
    {
        // Minecraft will still tick the sound even after stop has been called
        if(this.isStopped())
            return;

        PoweredVehicleEntity vehicle = this.vehicleRef.get();
        PlayerEntity player = this.playerRef.get();
        if(vehicle == null || player == null || ((vehicle.getControllingPassenger() == null || !vehicle.isEnginePowered()) && this.volume <= 0.05F) || !vehicle.isAlive())
        {
            this.stop();
            return;
        }

        this.volume = MathHelper.lerp(0.2F, this.volume, vehicle.getEngineVolume());
        this.pitch = MathHelper.lerp(0.2F, this.pitch, vehicle.getEnginePitch());
        this.attenuation = vehicle.equals(player.getVehicle()) ? AttenuationType.NONE : AttenuationType.LINEAR;

        if(!vehicle.equals(player.getVehicle()))
        {
            this.x = (float) (vehicle.getX() + (player.getX() - vehicle.getX()) * 0.65);
            this.y = (float) (vehicle.getY() + (player.getY() - vehicle.getY()) * 0.65);
            this.z = (float) (vehicle.getZ() + (player.getZ() - vehicle.getZ()) * 0.65);
        }
        else
        {
            this.x = vehicle.getX();
            this.y = vehicle.getY();
            this.z = vehicle.getZ();
        }
    }
}
