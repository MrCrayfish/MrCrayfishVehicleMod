package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
@SideOnly(Side.CLIENT)
public class MovingSoundVehicleRiding extends MovingSound
{
    private final EntityPlayer player;
    private final EntityVehicle vehicle;

    public MovingSoundVehicleRiding(EntityPlayer player, EntityVehicle vehicle)
    {
        super(vehicle.getRidingSound(), SoundCategory.NEUTRAL);
        this.player = player;
        this.vehicle = vehicle;
        this.attenuationType = AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.001F;
    }

    @Override
    public void update()
    {
        this.volume = 0.8F;
        if(!vehicle.isDead && player.isRiding() && player.getRidingEntity() == vehicle && player == Minecraft.getMinecraft().player)
        {
            this.pitch = vehicle.getMinEnginePitch() + (vehicle.getMaxEnginePitch() - vehicle.getMinEnginePitch()) * Math.abs(vehicle.getNormalSpeed());
        }
        else
        {
            this.donePlaying = true;
        }
    }
}
