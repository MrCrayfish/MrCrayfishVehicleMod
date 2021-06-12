package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.client.handler.CameraHandler;
import com.mrcrayfish.vehicle.client.handler.ControllerHandler;
import com.mrcrayfish.vehicle.client.handler.FuelingHandler;
import com.mrcrayfish.vehicle.client.handler.HeldVehicleHandler;
import com.mrcrayfish.vehicle.client.handler.InputHandler;
import com.mrcrayfish.vehicle.client.handler.OverlayHandler;
import com.mrcrayfish.vehicle.client.handler.PlayerModelHandler;
import com.mrcrayfish.vehicle.client.handler.SprayCanHandler;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.*;
import com.mrcrayfish.vehicle.client.render.tileentity.FluidExtractorRenderer;
import com.mrcrayfish.vehicle.client.render.tileentity.FuelDrumRenderer;
import com.mrcrayfish.vehicle.client.render.tileentity.GasPumpRenderer;
import com.mrcrayfish.vehicle.client.render.tileentity.GasPumpTankRenderer;
import com.mrcrayfish.vehicle.client.render.tileentity.VehicleCrateRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.*;
import com.mrcrayfish.vehicle.client.screen.EditVehicleScreen;
import com.mrcrayfish.vehicle.client.screen.FluidExtractorScreen;
import com.mrcrayfish.vehicle.client.screen.FluidMixerScreen;
import com.mrcrayfish.vehicle.client.screen.StorageScreen;
import com.mrcrayfish.vehicle.client.screen.WorkstationScreen;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.item.KeyItem;
import com.mrcrayfish.vehicle.item.PartItem;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.EntityType;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
public class ClientHandler
{
    private static boolean controllableLoaded = false;

    public static boolean isControllableLoaded()
    {
        return controllableLoaded;
    }

    public static void setup()
    {
        if(ModList.get().isLoaded("controllable"))
        {
            ClientHandler.controllableLoaded = true;
            MinecraftForge.EVENT_BUS.register(new ControllerHandler());
        }

        MinecraftForge.EVENT_BUS.register(EntityRayTracer.instance());
        MinecraftForge.EVENT_BUS.register(new CameraHandler());
        MinecraftForge.EVENT_BUS.register(new FuelingHandler());
        MinecraftForge.EVENT_BUS.register(new HeldVehicleHandler());
        MinecraftForge.EVENT_BUS.register(new InputHandler());
        MinecraftForge.EVENT_BUS.register(new OverlayHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerModelHandler());
        MinecraftForge.EVENT_BUS.register(new SprayCanHandler());

        setupCustomBlockModels();
        setupRenderLayers();
        setupVehicleRenders();
        setupTileEntityRenderers();
        setupScreenFactories();
        setupItemColors();

        IResourceManager manager = Minecraft.getInstance().getResourceManager();
        if(manager instanceof IReloadableResourceManager)
        {
            ((IReloadableResourceManager) manager).addReloadListener((stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> {
                return stage.markCompleteAwaitingOthers(Unit.INSTANCE).thenRun(() -> {
                    FluidUtils.clearCacheFluidColor();
                    EntityRayTracer.instance().clearDataForReregistration();
                    SpecialModels.clearModelCache();
                });
            });
        }
    }

    private static void setupCustomBlockModels()
    {
        //TODO add custom loader
        //ModelLoaderRegistry.registerLoader(new CustomLoader());
        //ModelLoaderRegistry.registerLoader(new ResourceLocation(Reference.MOD_ID, "ramp"), new CustomLoader());
    }

    private static void setupRenderLayers()
    {
        RenderTypeLookup.setRenderLayer(ModBlocks.WORKSTATION.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.FLUID_EXTRACTOR.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.GAS_PUMP.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModFluids.FUELIUM.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_FUELIUM.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModFluids.ENDER_SAP.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_ENDER_SAP.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModFluids.BLAZE_JUICE.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_BLAZE_JUICE.get(), RenderType.getTranslucent());
    }

    private static void setupVehicleRenders()
    {
        /* Register Vehicles */
        registerVehicleRender(ModEntities.ATV.get(), new RenderLandVehicleWrapper<>(new RenderATV()));
        registerVehicleRender(ModEntities.DUNE_BUGGY.get(), new RenderLandVehicleWrapper<>(new RenderDuneBuggy()));
        registerVehicleRender(ModEntities.GO_KART.get(), new RenderLandVehicleWrapper<>(new RenderGoKart()));
        registerVehicleRender(ModEntities.SHOPPING_CART.get(), new RenderLandVehicleWrapper<>(new RenderShoppingCart()));
        registerVehicleRender(ModEntities.MINI_BIKE.get(), new RenderMotorcycleWrapper<>(new RenderMiniBike()));
        registerVehicleRender(ModEntities.BUMPER_CAR.get(), new RenderLandVehicleWrapper<>(new RenderBumperCar()));
        registerVehicleRender(ModEntities.JET_SKI.get(), new RenderBoatWrapper<>(new RenderJetSki()));
        registerVehicleRender(ModEntities.SPEED_BOAT.get(), new RenderBoatWrapper<>(new RenderSpeedBoat()));
        registerVehicleRender(ModEntities.ALUMINUM_BOAT.get(), new RenderBoatWrapper<>(new RenderAluminumBoat()));
        registerVehicleRender(ModEntities.SMART_CAR.get(), new RenderLandVehicleWrapper<>(new RenderSmartCar()));
        registerVehicleRender(ModEntities.LAWN_MOWER.get(), new RenderLandVehicleWrapper<>(new RenderLawnMower()));
        registerVehicleRender(ModEntities.MOPED.get(), new RenderMotorcycleWrapper<>(new RenderMoped()));
        registerVehicleRender(ModEntities.SPORTS_PLANE.get(), new RenderPlaneWrapper<>(new RenderSportsPlane()));
        registerVehicleRender(ModEntities.GOLF_CART.get(), new RenderLandVehicleWrapper<>(new RenderGolfCart()));
        registerVehicleRender(ModEntities.OFF_ROADER.get(), new RenderLandVehicleWrapper<>(new RenderOffRoader()));
        registerVehicleRender(ModEntities.TRACTOR.get(), new RenderLandVehicleWrapper<>(new RenderTractor()));
        registerVehicleRender(ModEntities.MINI_BUS.get(), new RenderLandVehicleWrapper<>(new RenderMiniBus()));
        registerVehicleRender(ModEntities.DIRT_BIKE.get(), new RenderMotorcycleWrapper<>(new RenderDirtBike()));

        /* Register Trailers */
        registerVehicleRender(ModEntities.VEHICLE_TRAILER.get(), new RenderVehicleWrapper<>(new RenderVehicleTrailer()));
        registerVehicleRender(ModEntities.STORAGE_TRAILER.get(), new RenderVehicleWrapper<>(new RenderStorageTrailer()));
        registerVehicleRender(ModEntities.FLUID_TRAILER.get(), new RenderVehicleWrapper<>(new RenderFluidTrailer()));
        registerVehicleRender(ModEntities.SEEDER.get(), new RenderVehicleWrapper<>(new RenderSeederTrailer()));
        registerVehicleRender(ModEntities.FERTILIZER.get(), new RenderVehicleWrapper<>(new RenderFertilizerTrailer()));

        /* Register Mod Exclusive Vehicles */
        if(ModList.get().isLoaded("cfm"))
        {
            registerVehicleRender(ModEntities.SOFA.get(), new RenderLandVehicleWrapper<>(new RenderCouch()));
            registerVehicleRender(ModEntities.BATH.get(), new RenderPlaneWrapper<>(new RenderBath()));
            registerVehicleRender(ModEntities.SOFACOPTER.get(), new RenderHelicopterWrapper<>(new RenderCouchHelicopter()));
        }

        RenderingRegistry.registerEntityRenderingHandler(ModEntities.JACK.get(), com.mrcrayfish.vehicle.client.render.JackRenderer::new);
    }

    private static <T extends VehicleEntity & EntityRayTracer.IEntityRayTraceable, R extends AbstractRenderVehicle<T>> void registerVehicleRender(EntityType<T> type, RenderVehicleWrapper<T, R> wrapper)
    {
        RenderingRegistry.registerEntityRenderingHandler(type, manager -> new RenderEntityVehicle<>(manager, wrapper));
        VehicleRenderRegistry.registerRenderWrapper(type, wrapper);

        EntityRayTracer.IRayTraceTransforms transforms = wrapper.getRenderVehicle().getRayTraceTransforms();
        if(transforms != null)
        {
            EntityRayTracer.instance().registerTransforms(type, transforms);
        }
    }

    private static void setupTileEntityRenderers()
    {
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.FLUID_EXTRACTOR.get(), FluidExtractorRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.FUEL_DRUM.get(), FuelDrumRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.INDUSTRIAL_FUEL_DRUM.get(), FuelDrumRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.VEHICLE_CRATE.get(), VehicleCrateRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.JACK.get(), com.mrcrayfish.vehicle.client.render.tileentity.JackRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.GAS_PUMP.get(), GasPumpRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.GAS_PUMP_TANK.get(), GasPumpTankRenderer::new);
    }

    private static void setupScreenFactories()
    {
        ScreenManager.registerFactory(ModContainers.FLUID_EXTRACTOR.get(), FluidExtractorScreen::new);
        ScreenManager.registerFactory(ModContainers.FLUID_MIXER.get(), FluidMixerScreen::new);
        ScreenManager.registerFactory(ModContainers.EDIT_VEHICLE.get(), EditVehicleScreen::new);
        ScreenManager.registerFactory(ModContainers.WORKSTATION.get(), WorkstationScreen::new);
        ScreenManager.registerFactory(ModContainers.STORAGE.get(), StorageScreen::new);
    }

    private static void setupItemColors()
    {
        IItemColor color = (stack, index) ->
        {
            if(index == 0 && stack.hasTag() && stack.getTag().contains("Color", Constants.NBT.TAG_INT))
            {
                return stack.getTag().getInt("Color");
            }
            return 0xFFFFFF;
        };

        ForgeRegistries.ITEMS.forEach(item ->
        {
            if(item instanceof SprayCanItem || item instanceof KeyItem || (item instanceof PartItem && ((PartItem) item).isColored()))
            {
                Minecraft.getInstance().getItemColors().register(color, item);
            }
        });
    }
}
