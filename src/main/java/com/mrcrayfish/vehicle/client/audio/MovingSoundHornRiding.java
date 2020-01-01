package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class MovingSoundHornRiding extends TickableSound
{
    private final PlayerEntity player;
    private final PoweredVehicleEntity vehicle;

    public MovingSoundHornRiding(PlayerEntity player, PoweredVehicleEntity vehicle)
    {
        super(vehicle.getHornRidingSound(), SoundCategory.NEUTRAL);
        this.player = player;
        this.vehicle = vehicle;
        this.attenuationType = AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.001F;
        this.pitch = 0.85F;
    }

    @Override
    public void tick()
    {
        this.volume = vehicle.getHorn() ? 1.0F : 0.0F;
        if(!this.vehicle.isAlive() || this.player.getRidingEntity() == null || this.player.getRidingEntity() != this.vehicle || this.player != Minecraft.getInstance().player)
        {
            this.donePlaying = true;
        }
    }
}
