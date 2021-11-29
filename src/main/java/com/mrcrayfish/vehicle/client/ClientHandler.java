package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.client.handler.CameraHandler;
import com.mrcrayfish.vehicle.client.handler.ControllerHandler;
import com.mrcrayfish.vehicle.client.handler.FuelingHandler;
import com.mrcrayfish.vehicle.client.handler.HeldVehicleHandler;
import com.mrcrayfish.vehicle.client.handler.InputHandler;
import com.mrcrayfish.vehicle.client.handler.OverlayHandler;
import com.mrcrayfish.vehicle.client.handler.PlayerModelHandler;
import com.mrcrayfish.vehicle.client.handler.SprayCanHandler;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.particle.TyreSmokeParticle;
import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
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
import com.mrcrayfish.vehicle.entity.trailer.FertilizerTrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.FluidTrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.SeederTrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.StorageTrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.VehicleTrailerEntity;
import com.mrcrayfish.vehicle.entity.vehicle.MopedEntity;
import com.mrcrayfish.vehicle.entity.vehicle.SportsCarEntity;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModParticleTypes;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.item.PartItem;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.util.FluidUtils;
import com.mrcrayfish.vehicle.util.VehicleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
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
        MinecraftForge.EVENT_BUS.register(CosmeticCache.instance());
        MinecraftForge.EVENT_BUS.register(CameraHandler.instance());
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
        setupInteractableVehicles();

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
        VehicleUtil.registerVehicleRenderer(ModEntities.QUAD_BIKE.get(), QuadBikeRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.SPORTS_CAR.get(), SportsCarRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.GO_KART.get(), GoKartRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.JET_SKI.get(), JetSkiRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.LAWN_MOWER.get(), LawnMowerRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.MOPED.get(), MopedRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.SPORTS_PLANE.get(), SportsPlaneRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.GOLF_CART.get(), GolfCartRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.OFF_ROADER.get(), OffRoaderRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.TRACTOR.get(), TractorRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.MINI_BUS.get(), MiniBusRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.DIRT_BIKE.get(), DirtBikeRenderer::new);

        /* Register Trailers */
        VehicleUtil.registerVehicleRenderer(ModEntities.VEHICLE_TRAILER.get(), VehicleTrailerRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.STORAGE_TRAILER.get(), StorageTrailerRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.FLUID_TRAILER.get(), FluidTrailerRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.SEEDER.get(), SeederTrailerRenderer::new);
        VehicleUtil.registerVehicleRenderer(ModEntities.FERTILIZER.get(), FertilizerTrailerRenderer::new);

        /* Register Mod Exclusive Vehicles */
        if(ModList.get().isLoaded("cfm"))
        {
            VehicleUtil.registerVehicleRenderer(ModEntities.SOFACOPTER.get(), SofaHelicopterRenderer::new);
        }

        RenderingRegistry.registerEntityRenderingHandler(ModEntities.JACK.get(), com.mrcrayfish.vehicle.client.render.JackRenderer::new);
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

    private static void setupInteractableVehicles()
    {
        MopedEntity.registerInteractionBoxes();
        FertilizerTrailerEntity.registerInteractionBoxes();
        FluidTrailerEntity.registerInteractionBoxes();
        SeederTrailerEntity.registerInteractionBoxes();
        StorageTrailerEntity.registerInteractionBoxes();
        VehicleTrailerEntity.registerInteractionBoxes();
        SportsCarEntity.registerInteractionBoxes();
    }

    @SubscribeEvent
    public static void registerParticleFactories(ParticleFactoryRegisterEvent event)
    {
        ParticleManager manager = Minecraft.getInstance().particleEngine;
        manager.register(ModParticleTypes.TYRE_SMOKE.get(), TyreSmokeParticle.Factory::new);
    }
}
