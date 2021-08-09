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
public class MovingSoundVehicle extends TickableSound
{
    private final WeakReference<PoweredVehicleEntity> vehicleRef;

    public MovingSoundVehicle(PoweredVehicleEntity vehicle)
    {
        super(vehicle.getEngineSound(), SoundCategory.NEUTRAL);
        this.vehicleRef = new WeakReference<>(vehicle);
        this.looping = true;
        this.delay = 0;
        this.volume = 0.001F;
        this.pitch = vehicle.getEnginePitch();
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
        this.volume = !vehicle.equals(Minecraft.getInstance().player.getVehicle()) ? vehicle.getEngineVolume() : 0.0F;
        if(vehicle.isAlive() && vehicle.getPassengers().size() > 0)
        {
            PlayerEntity localPlayer = Minecraft.getInstance().player;
            this.x = (float) (vehicle.getX() + (localPlayer.getX() - vehicle.getX()) * 0.65);
            this.y = (float) (vehicle.getY() + (localPlayer.getY() - vehicle.getY()) * 0.65);
            this.z = (float) (vehicle.getZ() + (localPlayer.getZ() - vehicle.getZ()) * 0.65);
            this.pitch = MathHelper.lerp(0.2F, this.pitch, vehicle.getEnginePitch());
        }
        else
        {
            this.stop();
        }
    }
}
