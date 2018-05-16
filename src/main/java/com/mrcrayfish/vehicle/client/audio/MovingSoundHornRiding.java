package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
@SideOnly(Side.CLIENT)
public class MovingSoundHornRiding extends MovingSound
{
    private final EntityPlayer player;
    private final EntityVehicle vehicle;

    public MovingSoundHornRiding(EntityPlayer player, EntityVehicle vehicle)
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
    public void update()
    {
        this.volume = vehicle.getHorn() ? 1.0F : 0.0F;
        if(vehicle.isDead || !player.isRiding() || player.getRidingEntity() != vehicle || player != Minecraft.getMinecraft().player)
        {
            this.donePlaying = true;
        }
    }
}
