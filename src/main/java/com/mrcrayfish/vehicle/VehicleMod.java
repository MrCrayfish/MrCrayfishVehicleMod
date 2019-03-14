package com.mrcrayfish.vehicle;

import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.client.ControllerEvents;
import com.mrcrayfish.vehicle.client.gui.GuiHandler;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.CustomDataSerializers;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.trailer.EntityFertilizerTrailer;
import com.mrcrayfish.vehicle.entity.trailer.EntitySeederTrailer;
import com.mrcrayfish.vehicle.entity.trailer.EntityStorageTrailer;
import com.mrcrayfish.vehicle.entity.trailer.EntityVehicleTrailer;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.init.RegistrationHandler;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.proxy.Proxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Author: MrCrayfish
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, acceptedMinecraftVersions = Reference.MOD_COMPATIBILITY, dependencies = Reference.MOD_DEPENDS)
public class VehicleMod
{
    @Mod.Instance
    public static VehicleMod instance;

    @SidedProxy(clientSide = Reference.PROXY_CLIENT, serverSide = Reference.PROXY_SERVER)
    public static Proxy proxy;

    public int nextEntityId;

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs("tabVehicle")
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(ModItems.SMALL_ENGINE);
        }

        @Override
        public void displayAllRelevantItems(NonNullList<ItemStack> items)
        {
            super.displayAllRelevantItems(items);
            items.add(FluidUtil.getFilledBucket(new FluidStack(ModFluids.BLAZE_JUICE, 1)));
            items.add(FluidUtil.getFilledBucket(new FluidStack(ModFluids.ENDER_SAP, 1)));
            items.add(FluidUtil.getFilledBucket(new FluidStack(ModFluids.FUELIUM, 1)));
        }
    };

    static
    {
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new CommonEvents());

        ModFluids.register();
        RegistrationHandler.init();
        PacketHandler.init();
        CustomDataSerializers.register();
        HeldVehicleDataHandler.register();
        ModTileEntities.register();
        registerVehicles();
        VehicleProperties.register();

        proxy.preInit();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        proxy.init();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
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
        registerVehicle("aluminum_boat", EntityAluminumBoat.class);
        registerVehicle("smart_car", EntitySmartCar.class);
        registerVehicle("lawn_mower", EntityLawnMower.class);
        registerVehicle("moped", EntityMoped.class);
        registerVehicle("sports_plane", EntitySportsPlane.class);
        registerVehicle("golf_cart", EntityGolfCart.class);
        registerVehicle("off_roader", EntityOffRoader.class);

        if(Loader.isModLoaded("cfm"))
        {
            registerVehicle("couch", EntityCouch.class);
            registerVehicle("bath", EntityBath.class);
            registerVehicle("sofacopter", EntitySofacopter.class);
        }

        registerVehicle("trailer", EntityVehicleTrailer.class);
        registerVehicle("storage_trailer", EntityStorageTrailer.class);
        registerVehicle("seeder_trailer", EntitySeederTrailer.class);
        registerVehicle("fertilizer_trailer", EntityFertilizerTrailer.class);

        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "jack"), EntityJack.class, Reference.MOD_ID + "." + "jack", nextEntityId++, this, 256, 1, true);
    }

    private void registerVehicle(String id, Class<? extends EntityVehicle> clazz)
    {
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, id), clazz, Reference.MOD_ID + "." + id, nextEntityId++, this, 256, 1, true);
        BlockVehicleCrate.registerVehicle(id);
    }
}
