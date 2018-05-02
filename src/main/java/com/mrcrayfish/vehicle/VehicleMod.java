package com.mrcrayfish.vehicle;

import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.entity.CustomDataSerializers;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.init.RegistrationHandler;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.proxy.Proxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Author: MrCrayfish
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, acceptedMinecraftVersions = Reference.MOD_COMPATIBILITY, dependencies = Reference.MOD_DEPENDS)
public class VehicleMod
{
    @SidedProxy(clientSide = Reference.PROXY_CLIENT, serverSide = Reference.PROXY_SERVER)
    public static Proxy proxy;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new CommonEvents());

        RegistrationHandler.init();
        PacketHandler.init();
        CustomDataSerializers.register();

        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "atv"), EntityATV.class, "ATV", 0, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "dune_buggy"), EntityDuneBuggy.class, "Dune Buggy", 1, this, 64, 1, true);
        if(Loader.isModLoaded("cfm"))
        {
            EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "couch"), EntityCouch.class, "Couch", 2, this, 64, 1, true);
        }
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "go_kart"), EntityGoKart.class, "Go-Kart", 3, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "shopping_cart"), EntityShoppingCart.class, "Shopping Cart", 4, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "mini_bike"), EntityMiniBike.class, "mini_bike", 5, this, 64, 1, true);

        proxy.preInit();
    }
}
