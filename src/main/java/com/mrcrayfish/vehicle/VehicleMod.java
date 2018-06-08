package com.mrcrayfish.vehicle;

import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.entity.CustomDataSerializers;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.RegistrationHandler;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.proxy.Proxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Author: MrCrayfish
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, acceptedMinecraftVersions = Reference.MOD_COMPATIBILITY, dependencies = Reference.MOD_DEPENDS, useMetadata = true)
public class VehicleMod
{
    @SidedProxy(clientSide = Reference.PROXY_CLIENT, serverSide = Reference.PROXY_SERVER)
    public static Proxy proxy;

    public int nextEntityId;

    @Mod.Instance
    public static VehicleMod instance;

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs("tabVehicle")
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(ModItems.ENGINE);
        }
    };

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new CommonEvents());

        RegistrationHandler.init();
        PacketHandler.init();
        CustomDataSerializers.register();

        registerVehicles();

        proxy.preInit();
    }
    
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        proxy.init();
    }

    private void registerVehicles()
    {
        registerVehicle("atv", EntityATV.class);
        registerVehicle("dune_buggy", EntityDuneBuggy.class);
        registerVehicle("go_kart", EntityGoKart.class);
        registerVehicle("shopping_cart", EntityShoppingCart.class);
        registerVehicle("mini_bike", EntityMiniBike.class);
        registerVehicle("bumper_car", EntityBumperCar.class);
        registerVehicle("jet_ski", EntityJetSki.class);
        registerVehicle("speed_boat", EntitySpeedBoat.class);

        if(Loader.isModLoaded("cfm"))
        {
            registerVehicle("couch", EntityCouch.class);
        }
    }

    private void registerVehicle(String id, Class<? extends EntityVehicle> clazz)
    {
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, id), clazz, id, nextEntityId++, this, 64, 1, true);
    }
}
