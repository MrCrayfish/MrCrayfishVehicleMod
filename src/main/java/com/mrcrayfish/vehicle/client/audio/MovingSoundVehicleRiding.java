package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class MovingSoundVehicleRiding extends TickableSound
{
    private final PlayerEntity player;
    private final PoweredVehicleEntity vehicle;

    public MovingSoundVehicleRiding(PlayerEntity player, PoweredVehicleEntity vehicle)
    {
        super(vehicle.getRidingSound(), SoundCategory.NEUTRAL);
        this.player = player;
        this.vehicle = vehicle;
        this.attenuationType = ISound.AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.001F;
    }

    @Override
    public void tick()
    {
        this.volume = vehicle.isEnginePowered() ? 0.8F : 0.8F * vehicle.getActualSpeed();
        if(vehicle.isAlive() && vehicle.equals(player.getRidingEntity()) && player.equals(Minecraft.getInstance().player))
        {
            this.pitch = vehicle.getMinEnginePitch() + (vehicle.getMaxEnginePitch() - vehicle.getMinEnginePitch()) * Math.abs(vehicle.getActualSpeed());
        }
        else
        {
            this.donePlaying = true;
        }
    }
}
