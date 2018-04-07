package com.mrcrayfish.vehicle.proxy;

import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class ServerProxy implements Proxy
{
    @Override
    public void preInit() {}

    @Override
    public void playVehicleSound(EntityPlayer player, EntityVehicle vehicle) {}
}
