package com.mrcrayfish.vehicle.proxy;

import com.mrcrayfish.vehicle.client.ClientEvents;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicle;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicleRiding;
import com.mrcrayfish.vehicle.client.render.*;
import com.mrcrayfish.vehicle.entity.EntityATV;
import com.mrcrayfish.vehicle.entity.EntityDuneBuggy;
import com.mrcrayfish.vehicle.entity.EntityGoKart;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.EntityCouch;
import com.mrcrayfish.vehicle.entity.vehicle.EntityShoppingCart;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

/**
 * Author: MrCrayfish
 */
public class ClientProxy implements Proxy
{
    @Override
    public void preInit()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityATV.class, RenderATV::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityDuneBuggy.class, RenderDuneBuggy::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityCouch.class, RenderCouch::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityGoKart.class, RenderGoKart::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityShoppingCart.class, RenderShoppingCart::new);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }

    @Override
    public void playVehicleSound(EntityPlayer player, EntityVehicle vehicle)
    {
        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            if(vehicle.getRidingSound() != null)
            {
                Minecraft.getMinecraft().getSoundHandler().playSound(new MovingSoundVehicleRiding(player, vehicle));
            }
            if(vehicle.getMovingSound() != null)
            {
                Minecraft.getMinecraft().getSoundHandler().playSound(new MovingSoundVehicle(vehicle));
            }
        });
    }
}
