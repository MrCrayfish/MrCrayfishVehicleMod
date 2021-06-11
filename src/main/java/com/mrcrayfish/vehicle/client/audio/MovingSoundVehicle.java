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
public class MovingSoundVehicle extends TickableSound
{
    private final WeakReference<PoweredVehicleEntity> vehicleRef;

    public MovingSoundVehicle(PoweredVehicleEntity vehicle)
    {
        super(vehicle.getMovingSound(), SoundCategory.NEUTRAL);
        this.vehicleRef = new WeakReference<>(vehicle);
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.5F;
    }

    @Override
    public void tick()
    {
        PoweredVehicleEntity vehicle = this.vehicleRef.get();
        if(vehicle == null || Minecraft.getInstance().player == null)
        {
            this.finishPlaying();
            return;
        }
        this.volume = (vehicle.isEnginePowered() && !vehicle.equals(Minecraft.getInstance().player.getRidingEntity())) ? 1.0F : 0.0F;
        if(vehicle.isAlive() && vehicle.getPassengers().size() > 0)
        {
            PlayerEntity localPlayer = Minecraft.getInstance().player;
            this.x = (float) (vehicle.getPosX() + (localPlayer.getPosX() - vehicle.getPosX()) * 0.65);
            this.y = (float) (vehicle.getPosY() + (localPlayer.getPosY() - vehicle.getPosY()) * 0.65);
            this.z = (float) (vehicle.getPosZ() + (localPlayer.getPosZ() - vehicle.getPosZ()) * 0.65);
            this.pitch = vehicle.getMinEnginePitch() + (vehicle.getMaxEnginePitch() - vehicle.getMinEnginePitch()) * Math.abs(vehicle.getActualSpeed());
        }
        else
        {
            this.finishPlaying();
        }
    }
}
