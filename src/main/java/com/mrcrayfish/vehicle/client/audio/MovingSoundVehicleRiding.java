package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.item.EntityMinecart;
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
        super(ModSounds.DRIVING, SoundCategory.NEUTRAL);
        this.player = player;
        this.vehicle = vehicle;
        this.attenuationType = ISound.AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
    }

    @Override
    public void update()
    {
        if(!vehicle.isDead && player.isRiding() && player.getRidingEntity() == vehicle)
        {
            this.pitch = 0.5F + 0.8F * vehicle.getNormalSpeed();
        }
        else
        {
            this.donePlaying = true;
        }
    }
}
