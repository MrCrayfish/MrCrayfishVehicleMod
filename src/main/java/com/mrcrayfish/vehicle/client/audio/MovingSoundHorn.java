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
public class MovingSoundHorn extends TickableSound
{
    private final WeakReference<PoweredVehicleEntity> vehicleRef;

    public MovingSoundHorn(PoweredVehicleEntity vehicle)
    {
        super(vehicle.getHornSound(), SoundCategory.NEUTRAL);
        this.vehicleRef = new WeakReference<>(vehicle);
        this.looping = true;
        this.delay = 0;
        this.volume = 0.001F;
        this.pitch = 0.85F;
    }

    @Override
    public void tick()
    {
        PoweredVehicleEntity vehicle = this.vehicleRef.get();
        if(vehicle == null || Minecraft.getInstance().player == null)
        {
            this.stop();
            return;
        }
        this.volume = vehicle.getHorn() ? 1.0F : 0.0F;
        if(vehicle.isAlive() && vehicle.getPassengers().size() > 0)
        {
            PlayerEntity localPlayer = Minecraft.getInstance().player;
            this.x = (float) (vehicle.getX() + (localPlayer.getX() - vehicle.getX()) * 0.65);
            this.y = (float) (vehicle.getY() + (localPlayer.getY() - vehicle.getY()) * 0.65);
            this.z = (float) (vehicle.getZ() + (localPlayer.getZ() - vehicle.getZ()) * 0.65);
        }
        else
        {
            this.stop();
        }
    }
}
