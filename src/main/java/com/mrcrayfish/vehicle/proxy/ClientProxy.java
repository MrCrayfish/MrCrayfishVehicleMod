package com.mrcrayfish.vehicle.proxy;

import com.mrcrayfish.vehicle.client.render.RenderATV;
import com.mrcrayfish.vehicle.entity.EntityATV;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
    }
}
