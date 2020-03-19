package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
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
public class MovingSoundVehicleRiding extends MovingSound
{
    private final WeakReference<EntityPlayer> playerRef;
    private final WeakReference<EntityPoweredVehicle> vehicleRef;

    public MovingSoundVehicleRiding(EntityPlayer player, EntityPoweredVehicle vehicle)
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
    public void update()
    {
        EntityPoweredVehicle vehicle = this.vehicleRef.get();
        EntityPlayer player = this.playerRef.get();
        if(vehicle == null || player == null)
        {
            this.donePlaying = true;
            return;
        }
        if(vehicle.isDead || !vehicle.equals(player.getRidingEntity()) || !player.equals(Minecraft.getMinecraft().player) || vehicle.getPassengers().size() == 0)
        {
            this.donePlaying = true;
        }
        this.volume = vehicle.getControllingPassenger() != null ? 1.0F : 0.0F;
        this.pitch = vehicle.getMinEnginePitch() + (vehicle.getMaxEnginePitch() - vehicle.getMinEnginePitch()) * Math.abs(vehicle.getActualSpeed());
    }
}
