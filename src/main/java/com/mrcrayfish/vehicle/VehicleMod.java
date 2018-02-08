package com.mrcrayfish.vehicle;

import com.mrcrayfish.vehicle.entity.EntityATV;
import com.mrcrayfish.vehicle.init.RegistrationHandler;
import com.mrcrayfish.vehicle.proxy.Proxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Author: MrCrayfish
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, acceptedMinecraftVersions = Reference.MOD_COMPATIBILITY)
public class VehicleMod
{
    @SidedProxy(clientSide = Reference.PROXY_CLIENT, serverSide = Reference.PROXY_SERVER)
    public static Proxy proxy;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        RegistrationHandler.init();

        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "vehicle_atv"), EntityATV.class, "atv", 0, this, 64, 1, true);

        proxy.preInit();
    }
}
