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
public class MovingSoundVehicle extends TickableSound
{
    private final PoweredVehicleEntity vehicle;

    public MovingSoundVehicle(PoweredVehicleEntity vehicle)
    {
        super(vehicle.getMovingSound(), SoundCategory.NEUTRAL);
        this.vehicle = vehicle;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.001F;
    }

    @Override
    public void tick()
    {
        this.volume = vehicle.isEnginePowered() ? 0.8F : 0.8F * vehicle.getActualSpeed();
        if(vehicle.isAlive() && vehicle.getControllingPassenger() != null && Minecraft.getInstance().player != null && vehicle.getControllingPassenger() != Minecraft.getInstance().player)
        {
            PlayerEntity localPlayer = Minecraft.getInstance().player;
            this.x = (float) (vehicle.func_226277_ct_() + (localPlayer.func_226277_ct_() - vehicle.func_226277_ct_()) * 0.65);
            this.y = (float) (vehicle.func_226278_cu_() + (localPlayer.func_226278_cu_() - vehicle.func_226278_cu_()) * 0.65);
            this.z = (float) (vehicle.func_226281_cx_() + (localPlayer.func_226281_cx_() - vehicle.func_226281_cx_()) * 0.65);
            this.pitch = vehicle.getMinEnginePitch() + (vehicle.getMaxEnginePitch() - vehicle.getMinEnginePitch()) * Math.abs(vehicle.getActualSpeed());
        }
        else
        {
            this.donePlaying = true;
        }
    }
}
