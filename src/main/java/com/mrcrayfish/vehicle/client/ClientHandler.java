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
        setupRayTraceConstructors();

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

    //TODO move these into respective models
    private static void setupRayTraceConstructors()
    {
        /* Aluminum Boat */
        EntityRayTracer.instance().registerTransforms(ModEntities.ALUMINUM_BOAT.get(), (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.ALUMINUM_BOAT_BODY, parts, transforms);
            EntityRayTracer.createFuelPartTransforms(ModEntities.ALUMINUM_BOAT.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        });

        /* ATV */
        EntityRayTracer.instance().registerTransforms(ModEntities.ATV.get(), (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.ATV_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.ATV_HANDLES, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.3375F, 0.25F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.025F, 0.0F));
            EntityRayTracer.createTransformListForPart(SpecialModels.TOW_BAR, parts,
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, 180F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.5F, 1.05F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.ATV.get(), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms(ModEntities.ATV.get(), parts, transforms);
        });

        /* Bumper Car */
        EntityRayTracer.instance().registerTransforms(ModEntities.BUMPER_CAR.get(), (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.BUMPER_CAR_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.2F, 0.0F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                EntityRayTracer.MatrixTransformation.createScale(0.9F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.BUMPER_CAR.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        });

        /* Dirt Bike */
        EntityRayTracer.instance().registerTransforms(ModEntities.DIRT_BIKE.get(), (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.DIRT_BIKE_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.DIRT_BIKE_HANDLES, parts, transforms);
            EntityRayTracer.createFuelPartTransforms(ModEntities.DIRT_BIKE.get(), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
        });

        /* Dune Buggy */
        EntityRayTracer.instance().registerTransforms(ModEntities.DUNE_BUGGY.get(), (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.DUNE_BUGGY_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.DUNE_BUGGY_HANDLES, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.0F, -0.0046875F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.DUNE_BUGGY.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        });

        /* Go Kart */
        EntityRayTracer.instance().registerTransforms(ModEntities.GO_KART.get(), (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.09F, 0.49F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                EntityRayTracer.MatrixTransformation.createScale(0.9F));
            EntityRayTracer.createPartTransforms(ModItems.WOOD_SMALL_ENGINE.get(), VehicleProperties.getProperties(ModEntities.GO_KART.get()).getEnginePosition(), parts, transforms, RayTraceFunction.FUNCTION_FUELING);
        });

        /* Jet Ski */
        EntityRayTracer.instance().registerTransforms(ModEntities.JET_SKI.get(), (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.JET_SKI_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.ATV_HANDLES, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.375F, 0.25F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.02F, 0.0F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.JET_SKI.get(), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
        });

        /* Lawn Mower */
        EntityRayTracer.instance().registerTransforms(ModEntities.LAWN_MOWER.get(), (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.LAWN_MOWER_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.4F, -0.15F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                EntityRayTracer.MatrixTransformation.createScale(0.9F));
            EntityRayTracer.createTransformListForPart(SpecialModels.TOW_BAR, parts,
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, 180F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.5F, 0.6F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.LAWN_MOWER.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        });

        /* Mini Bike */
        EntityRayTracer.instance().registerTransforms(ModEntities.MINI_BIKE.get(), (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.MINI_BIKE_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.MINI_BIKE_HANDLES, parts, transforms);
            EntityRayTracer.createPartTransforms(ModItems.WOOD_SMALL_ENGINE.get(), VehicleProperties.getProperties(ModEntities.MINI_BIKE.get()).getEnginePosition(), parts, transforms, RayTraceFunction.FUNCTION_FUELING);
        });

        /* Moped */
        EntityRayTracer.instance().registerTransforms(ModEntities.MOPED.get(), (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.MOPED_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.MOPED_HANDLES, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.0625F, 0.0F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.835F, 0.525F),
                EntityRayTracer.MatrixTransformation.createScale(0.8F));
            EntityRayTracer.createTransformListForPart(SpecialModels.MOPED_MUD_GUARD, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.0625F, 0.0F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.12F, 0.785F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -22.5F),
                EntityRayTracer.MatrixTransformation.createScale(0.9F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.MOPED.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        });

        /* Shopping Cart */
        EntityRayTracer.instance().registerTransforms(ModEntities.SHOPPING_CART.get(), (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.SHOPPING_CART_BODY, parts, transforms);
        });

        /* Smart Car */
        EntityRayTracer.instance().registerTransforms(ModEntities.SMART_CAR.get(), (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.SMART_CAR_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.2F, 0.3F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -67.5F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                EntityRayTracer.MatrixTransformation.createScale(0.9F));
            EntityRayTracer.createTransformListForPart(SpecialModels.TOW_BAR, parts,
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, 180F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.5F, 1.35F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.SMART_CAR.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        });

        /* Speed Boat */
        EntityRayTracer.instance().registerTransforms(ModEntities.SPEED_BOAT.get(), (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.SPEED_BOAT_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.215F, -0.125F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.02F, 0.0F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.SPEED_BOAT.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        });

        /* Sports Plane */
        EntityRayTracer.instance().registerTransforms(ModEntities.SPORTS_PLANE.get(), (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE, parts, transforms);
            EntityRayTracer.createFuelPartTransforms(ModEntities.SPORTS_PLANE.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms(ModEntities.SPORTS_PLANE.get(), parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_WING, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0, -0.1875F, 0.5F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Z, 180F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.875F, 0.0625F, 0.0F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, 5F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_WING, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.875F, -0.1875F, 0.5F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -5F));
            transforms.add(EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.5F, 0.0F));
            transforms.add(EntityRayTracer.MatrixTransformation.createScale(0.85F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_WHEEL_COVER, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.1875F, 1.5F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_LEG, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.1875F, 1.5F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_WHEEL_COVER, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(-0.46875F, -0.1875F, 0.125F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_LEG, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(-0.46875F, -0.1875F, 0.125F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, -100F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_WHEEL_COVER, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.46875F, -0.1875F, 0.125F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_LEG, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.46875F, -0.1875F, 0.125F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, 100F));
        });

        /* Golf Cart */
        EntityRayTracer.instance().registerTransforms(ModEntities.GOLF_CART.get(), (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.GOLF_CART_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(-0.345F, 0.425F, 0.1F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                EntityRayTracer.MatrixTransformation.createScale(0.95F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.GOLF_CART.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms(ModEntities.GOLF_CART.get(), parts, transforms);
        });

        /* Off Roader */
        EntityRayTracer.instance().registerTransforms(ModEntities.OFF_ROADER.get(), (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.OFF_ROADER_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(-0.3125F, 0.35F, 0.2F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                EntityRayTracer.MatrixTransformation.createScale(0.75F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.OFF_ROADER.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms(ModEntities.OFF_ROADER.get(), parts, transforms);
        });

        /* Tractor */
        EntityRayTracer.instance().registerTransforms(ModEntities.TRACTOR.get(), (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.TRACTOR, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.66F, -0.475F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -67.5F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                EntityRayTracer.MatrixTransformation.createScale(0.9F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.TRACTOR.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms(ModEntities.TRACTOR.get(), parts, transforms);
        });

        /* Mini Bus */
        EntityRayTracer.instance().registerTransforms(ModEntities.MINI_BUS.get(), (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.MINI_BUS_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                EntityRayTracer.MatrixTransformation.createTranslation(-0.2825F, 0.225F, 1.0625F),
                EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -67.5F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                EntityRayTracer.MatrixTransformation.createScale(0.75F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.MINI_BUS.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms(ModEntities.MINI_BUS.get(), parts, transforms);
        });

        if(ModList.get().isLoaded("cfm"))
        {
            /* Bath */
            EntityRayTracer.instance().registerTransforms(ModEntities.BATH.get(), (tracer, transforms, parts) ->
            {
                EntityRayTracer.createTransformListForPart(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cfm:bath")), parts, transforms,
                        EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, 90F));
            });

            /* Sofa */
            EntityRayTracer.instance().registerTransforms(ModEntities.SOFA.get(), (tracer, transforms, parts) ->
            {
                EntityRayTracer.createTransformListForPart(SpecialModels.RAINBOW_SOFA, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, 90F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.0625F, 0.0F));
            });

            /* Sofacopter */
            EntityRayTracer.instance().registerTransforms(ModEntities.SOFACOPTER.get(), (tracer, transforms, parts) ->
            {
                EntityRayTracer.createTransformListForPart(SpecialModels.RED_SOFA, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, 90F));
                EntityRayTracer.createTransformListForPart(SpecialModels.SOFA_HELICOPTER_ARM, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 8 * 0.0625F, 0.0F));
                EntityRayTracer.createFuelPartTransforms(ModEntities.SOFACOPTER.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
                EntityRayTracer.createKeyPortTransforms(ModEntities.SOFACOPTER.get(), parts, transforms);
            });
        }

        /* Vehicle Trailer */
        EntityRayTracer.instance().registerTransforms(ModEntities.VEHICLE_TRAILER.get(),(tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.VEHICLE_TRAILER, parts, transforms);
        });

        /* Storage Trailer */
        EntityRayTracer.instance().registerTransforms(ModEntities.STORAGE_TRAILER.get(),(tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.STORAGE_TRAILER, parts, transforms);
        });

        /* Seeder */
        EntityRayTracer.instance().registerTransforms(ModEntities.SEEDER.get(),(tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.SEEDER_TRAILER, parts, transforms);
        });

        /* Fertilizer */
        EntityRayTracer.instance().registerTransforms(ModEntities.FERTILIZER.get(),(tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.FERTILIZER_TRAILER, parts, transforms);
        });

        /* Fluid Trailer */
        EntityRayTracer.instance().registerTransforms(ModEntities.FLUID_TRAILER.get(),(tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.FLUID_TRAILER, parts, transforms);
        });
    }
}
