package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
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
public class MovingSoundHorn extends TickableSound
{
    private static final int MAX_FADE_IN_TICKS = 1;

    private final WeakReference<PlayerEntity> playerRef;
    private final WeakReference<PoweredVehicleEntity> vehicleRef;
    private int fadeTicks;

    public MovingSoundHorn(PlayerEntity player, PoweredVehicleEntity vehicle)
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
        if(vehicle == null || player == null || (!vehicle.getHorn() && this.fadeTicks <= 0) || !vehicle.isAlive())
        {
            this.stop();
            return;
        }

        if(vehicle.getHorn())
        {
            if(this.fadeTicks < MAX_FADE_IN_TICKS)
            {
                this.fadeTicks++;
                this.volume = (float) this.fadeTicks / (float) MAX_FADE_IN_TICKS;
            }
        }
        else if(this.fadeTicks > 0)
        {
            this.fadeTicks -= 2;
            this.volume = (float) this.fadeTicks / (float) MAX_FADE_IN_TICKS;
        }

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
