package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.ref.WeakReference;

/**
 * Author: MrCrayfish
 */
@SideOnly(Side.CLIENT)
public class MovingSoundHornRiding extends MovingSound
{
    private final WeakReference<EntityPlayer> playerRef;
    private final WeakReference<EntityPoweredVehicle> vehicleRef;

    public MovingSoundHornRiding(EntityPlayer player, EntityPoweredVehicle vehicle)
    {
        super(vehicle.getHornRidingSound(), SoundCategory.NEUTRAL);
        this.playerRef = new WeakReference<>(player);
        this.vehicleRef = new WeakReference<>(vehicle);
        this.attenuationType = AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.001F;
        this.pitch = 0.85F;
    }

    @Override
    public void update()
    {
        EntityPoweredVehicle vehicle = this.vehicleRef.get();
        EntityPlayer player = this.playerRef.get();
        if(vehicle == null || player == null)
        {
            this.donePlaying = true;
            return;
        }
        if(vehicle.isDead || player.getRidingEntity() == null || player.getRidingEntity() != vehicle || !player.equals(Minecraft.getMinecraft().player) || vehicle.getPassengers().size() == 0)
        {
            this.donePlaying = true;
            return;
        }
        this.volume = vehicle.getHorn() ? 1.0F : 0.0F;
    }
}
