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
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.EntityVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.client.render.tileentity.FluidExtractorRenderer;
import com.mrcrayfish.vehicle.client.render.tileentity.FluidPumpRenderer;
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
import com.mrcrayfish.vehicle.init.ModTileEntities;
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
import net.minecraft.util.Unit;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;

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
        MinecraftForge.EVENT_BUS.register(new ClientEvents());

        setupCustomBlockModels();
        setupRenderLayers();
        setupVehicleRenders();
        setupTileEntityRenderers();
        setupScreenFactories();
        setupItemColors();

        IResourceManager manager = Minecraft.getInstance().getResourceManager();
        if(manager instanceof IReloadableResourceManager)
        {
            ((IReloadableResourceManager) manager).registerReloadListener((stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> {
                return stage.wait(Unit.INSTANCE).thenRun(() -> {
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
        RenderTypeLookup.setRenderLayer(ModBlocks.WORKSTATION.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.FLUID_EXTRACTOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.GAS_PUMP.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModFluids.FUELIUM.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_FUELIUM.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModFluids.ENDER_SAP.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_ENDER_SAP.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModFluids.BLAZE_JUICE.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_BLAZE_JUICE.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.FUEL_DRUM.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.INDUSTRIAL_FUEL_DRUM.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.TRAFFIC_CONE.get(), RenderType.cutout());
    }

    private static void setupVehicleRenders()
    {
        /* Register Vehicles */
        registerVehicleRenderer(ModEntities.ATV.get(), ATVRenderer::new);
        registerVehicleRenderer(ModEntities.DUNE_BUGGY.get(), DuneBuggyRenderer::new);
        registerVehicleRenderer(ModEntities.GO_KART.get(), GoKartRenderer::new);
        registerVehicleRenderer(ModEntities.SHOPPING_CART.get(), ShoppingCartRenderer::new);
        registerVehicleRenderer(ModEntities.MINI_BIKE.get(), MiniBikeRenderer::new);
        registerVehicleRenderer(ModEntities.BUMPER_CAR.get(), BumperCarModel::new);
        registerVehicleRenderer(ModEntities.JET_SKI.get(), JetSkiRenderer::new);
        registerVehicleRenderer(ModEntities.SPEED_BOAT.get(), SpeedBoatRenderer::new);
        registerVehicleRenderer(ModEntities.ALUMINUM_BOAT.get(), AluminumBoatRenderer::new);
        registerVehicleRenderer(ModEntities.SMART_CAR.get(), SmartCarRenderer::new);
        registerVehicleRenderer(ModEntities.LAWN_MOWER.get(), LawnMowerRenderer::new);
        registerVehicleRenderer(ModEntities.MOPED.get(), MopedRenderer::new);
        registerVehicleRenderer(ModEntities.SPORTS_PLANE.get(), SportsPlaneRenderer::new);
        registerVehicleRenderer(ModEntities.GOLF_CART.get(), GolfCartRenderer::new);
        registerVehicleRenderer(ModEntities.OFF_ROADER.get(), OffRoaderRenderer::new);
        registerVehicleRenderer(ModEntities.TRACTOR.get(), TractorRenderer::new);
        registerVehicleRenderer(ModEntities.MINI_BUS.get(), MiniBusRenderer::new);
        registerVehicleRenderer(ModEntities.DIRT_BIKE.get(), DirtBikeRenderer::new);

        /* Register Trailers */
        registerVehicleRenderer(ModEntities.VEHICLE_TRAILER.get(), VehicleTrailerRenderer::new);
        registerVehicleRenderer(ModEntities.STORAGE_TRAILER.get(), StorageTrailerRenderer::new);
        registerVehicleRenderer(ModEntities.FLUID_TRAILER.get(), FluidTrailerRenderer::new);
        registerVehicleRenderer(ModEntities.SEEDER.get(), SeederTrailerRenderer::new);
        registerVehicleRenderer(ModEntities.FERTILIZER.get(), FertilizerTrailerRenderer::new);

        /* Register Mod Exclusive Vehicles */
        if(ModList.get().isLoaded("cfm"))
        {
            registerVehicleRenderer(ModEntities.SOFA.get(), SofaCarRenderer::new);
            registerVehicleRenderer(ModEntities.BATH.get(), BathModel::new);
            registerVehicleRenderer(ModEntities.SOFACOPTER.get(), SofaHelicopterRenderer::new);
        }

        RenderingRegistry.registerEntityRenderingHandler(ModEntities.JACK.get(), com.mrcrayfish.vehicle.client.render.JackRenderer::new);
    }

    @SuppressWarnings("unchecked")
    private static <T extends VehicleEntity & EntityRayTracer.IEntityRayTraceable> void registerVehicleRenderer(EntityType<T> type, Function<VehicleProperties, AbstractVehicleRenderer<T>> rendererFunction)
    {
        VehicleProperties properties = VehicleProperties.get(type);
        AbstractVehicleRenderer<T> renderer = rendererFunction.apply(properties);
        RenderingRegistry.registerEntityRenderingHandler(type, manager -> new EntityVehicleRenderer<>(manager, renderer));
        VehicleRenderRegistry.registerVehicleRendererFunction(type, rendererFunction, renderer);

        EntityRayTracer.IRayTraceTransforms transforms = renderer.getRayTraceTransforms();
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
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.FLUID_PUMP.get(), FluidPumpRenderer::new);
    }

    private static void setupScreenFactories()
    {
        ScreenManager.register(ModContainers.FLUID_EXTRACTOR.get(), FluidExtractorScreen::new);
        ScreenManager.register(ModContainers.FLUID_MIXER.get(), FluidMixerScreen::new);
        ScreenManager.register(ModContainers.EDIT_VEHICLE.get(), EditVehicleScreen::new);
        ScreenManager.register(ModContainers.WORKSTATION.get(), WorkstationScreen::new);
        ScreenManager.register(ModContainers.STORAGE.get(), StorageScreen::new);
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
            if(item instanceof SprayCanItem || (item instanceof PartItem && ((PartItem) item).isColored()))
            {
                Minecraft.getInstance().getItemColors().register(color, item);
            }
        });
    }

    public static class PropertiesSupplier
    {
        private VehicleProperties properties;

        private PropertiesSupplier(VehicleProperties properties)
        {
            this.properties = properties;
        }

        public VehicleProperties get()
        {
            return this.properties;
        }

        private static PropertiesSupplier of(VehicleProperties properties)
        {
            return new PropertiesSupplier(properties);
        }
    }
}
