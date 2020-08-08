package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
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
public class MovingSoundVehicleRiding extends TickableSound
{
    private final WeakReference<PlayerEntity> playerRef;
    private final WeakReference<PoweredVehicleEntity> vehicleRef;

    public MovingSoundVehicleRiding(PlayerEntity player, PoweredVehicleEntity vehicle)
    {
        super(vehicle.getRidingSound(), SoundCategory.NEUTRAL);
        this.playerRef = new WeakReference<>(player);
        this.vehicleRef = new WeakReference<>(vehicle);
        this.attenuationType = ISound.AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.001F;
    }

    @Override
    public void tick()
    {
        PoweredVehicleEntity vehicle = this.vehicleRef.get();
        PlayerEntity player = this.playerRef.get();
        if(vehicle == null || player == null)
        {
            this.func_239509_o_();
            return;
        }
        if(!vehicle.isAlive() || !vehicle.equals(player.getRidingEntity()) || !player.equals(Minecraft.getInstance().player) || vehicle.getPassengers().size() == 0)
        {
            this.func_239509_o_();
            return;
        }
        this.volume = vehicle.getControllingPassenger() != null && vehicle.isEnginePowered() ? 1.0F : 0.0F;
        this.pitch = vehicle.getMinEnginePitch() + (vehicle.getMaxEnginePitch() - vehicle.getMinEnginePitch()) * Math.abs(vehicle.getActualSpeed());
    }
}
