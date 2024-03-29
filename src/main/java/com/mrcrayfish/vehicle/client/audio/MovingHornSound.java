package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
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
public class MovingHornSound extends TickableSound
{
    private final WeakReference<PlayerEntity> playerRef;
    private final WeakReference<PoweredVehicleEntity> vehicleRef;

    public MovingHornSound(PlayerEntity player, PoweredVehicleEntity vehicle)
    {
        super(vehicle.getHornSound(), SoundCategory.NEUTRAL);
        this.playerRef = new WeakReference<>(player);
        this.vehicleRef = new WeakReference<>(vehicle);
        this.volume = 0.0F;
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
        if(vehicle == null || player == null || (!vehicle.getHorn() && this.volume <= 0.05F) || !vehicle.isAlive() || vehicle.getPassengers().isEmpty())
        {
            this.stop();
            return;
        }

        if(vehicle.getHorn())
        {
            this.volume = MathHelper.lerp(0.6F, this.volume, 1.0F);
        }
        else
        {
            this.volume = MathHelper.lerp(0.75F, this.volume, 0.0F);
        }

        this.attenuation = vehicle.equals(player.getVehicle()) ? AttenuationType.NONE : AttenuationType.LINEAR;

        if(!vehicle.equals(player.getVehicle()))
        {
            this.x = (vehicle.getX() + (player.getX() - vehicle.getX()) * 0.65);
            this.y = (vehicle.getY() + (player.getY() - vehicle.getY()) * 0.65);
            this.z = (vehicle.getZ() + (player.getZ() - vehicle.getZ()) * 0.65);
        }
        else
        {
            this.x = vehicle.getX();
            this.y = vehicle.getY();
            this.z = vehicle.getZ();
        }
    }
}
