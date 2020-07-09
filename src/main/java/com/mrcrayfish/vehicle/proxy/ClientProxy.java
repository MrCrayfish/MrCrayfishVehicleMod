package com.mrcrayfish.vehicle.proxy;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.Buttons;
import com.mrcrayfish.controllable.client.Controller;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.ClientEvents;
import com.mrcrayfish.vehicle.client.ControllerEvents;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.HeldVehicleEvents;
import com.mrcrayfish.vehicle.client.RayTraceFunction;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHorn;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHornRiding;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicle;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicleRiding;
import com.mrcrayfish.vehicle.client.render.*;
import com.mrcrayfish.vehicle.client.render.tileentity.*;
import com.mrcrayfish.vehicle.client.render.vehicle.*;
import com.mrcrayfish.vehicle.client.screen.*;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.entity.PlaneEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.*;
import com.mrcrayfish.vehicle.item.KeyItem;
import com.mrcrayfish.vehicle.item.PartItem;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public class ClientProxy implements Proxy
{
    public static final KeyBinding KEY_HORN = new KeyBinding("key.vehicle.horn", GLFW.GLFW_KEY_H, "key.categories.vehicle");
    public static final KeyBinding KEY_CYCLE_SEATS = new KeyBinding("key.vehicle.cycle_seats", GLFW.GLFW_KEY_C, "key.categories.vehicle");

    public static boolean controllableLoaded = false;

    private static final WeakHashMap<UUID, Map<SoundType, ITickableSound>> SOUND_TRACKER = new WeakHashMap<>();

    @Override
    public void setupClient()
    {
        if(ModList.get().isLoaded("controllable"))
        {
            controllableLoaded = true;
            MinecraftForge.EVENT_BUS.register(new ControllerEvents());
        }

        MinecraftForge.EVENT_BUS.register(EntityRayTracer.instance());
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new HeldVehicleEvents());
        MinecraftForge.EVENT_BUS.register(this);

        this.setupRenderLayers();
        this.registerEntityRenders();
        this.bindTileEntityRenders();
        this.registerKeyBindings();
        this.registerScreenFactories();
        this.registerItemColors();
        this.registerRayTraceConstructors();

        //TODO add custom loader
        //ModelLoaderRegistry.registerLoader(new CustomLoader());
        //ModelLoaderRegistry.registerLoader(new ResourceLocation(Reference.MOD_ID, "ramp"), new CustomLoader());

        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener((stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> CompletableFuture.runAsync(() -> {
            FluidUtils.clearCacheFluidColor();
            EntityRayTracer.instance().clearDataForReregistration();
        }));
    }

    private void setupRenderLayers()
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
    }

    private void registerEntityRenders()
    {
        /* Register Vehicles */
        this.registerVehicleRender(ModEntities.ATV.get(), new RenderLandVehicleWrapper<>(new RenderATV()));
        this.registerVehicleRender(ModEntities.DUNE_BUGGY.get(), new RenderLandVehicleWrapper<>(new RenderDuneBuggy()));
        this.registerVehicleRender(ModEntities.GO_KART.get(), new RenderLandVehicleWrapper<>(new RenderGoKart()));
        this.registerVehicleRender(ModEntities.SHOPPING_CART.get(), new RenderLandVehicleWrapper<>(new RenderShoppingCart()));
        this.registerVehicleRender(ModEntities.MINI_BIKE.get(), new RenderMotorcycleWrapper<>(new RenderMiniBike()));
        this.registerVehicleRender(ModEntities.BUMPER_CAR.get(), new RenderLandVehicleWrapper<>(new RenderBumperCar()));
        this.registerVehicleRender(ModEntities.JET_SKI.get(), new RenderBoatWrapper<>(new RenderJetSki()));
        this.registerVehicleRender(ModEntities.SPEED_BOAT.get(), new RenderBoatWrapper<>(new RenderSpeedBoat()));
        this.registerVehicleRender(ModEntities.ALUMINUM_BOAT.get(), new RenderBoatWrapper<>(new RenderAluminumBoat()));
        this.registerVehicleRender(ModEntities.SMART_CAR.get(), new RenderLandVehicleWrapper<>(new RenderSmartCar()));
        this.registerVehicleRender(ModEntities.LAWN_MOWER.get(), new RenderLandVehicleWrapper<>(new RenderLawnMower()));
        this.registerVehicleRender(ModEntities.MOPED.get(), new RenderMotorcycleWrapper<>(new RenderMoped()));
        this.registerVehicleRender(ModEntities.SPORTS_PLANE.get(), new RenderPlaneWrapper<>(new RenderSportsPlane()));
        this.registerVehicleRender(ModEntities.GOLF_CART.get(), new RenderLandVehicleWrapper<>(new RenderGolfCart()));
        this.registerVehicleRender(ModEntities.OFF_ROADER.get(), new RenderLandVehicleWrapper<>(new RenderOffRoader()));
        this.registerVehicleRender(ModEntities.TRACTOR.get(), new RenderLandVehicleWrapper<>(new RenderTractor()));
        this.registerVehicleRender(ModEntities.MINI_BUS.get(), new RenderLandVehicleWrapper<>(new RenderMiniBus()));
        this.registerVehicleRender(ModEntities.DIRT_BIKE.get(), new RenderMotorcycleWrapper<>(new RenderDirtBike()));

        /* Register Trailers */
        this.registerVehicleRender(ModEntities.VEHICLE_TRAILER.get(), new RenderVehicleWrapper<>(new RenderVehicleTrailer()));
        this.registerVehicleRender(ModEntities.STORAGE_TRAILER.get(), new RenderVehicleWrapper<>(new RenderStorageTrailer()));
        this.registerVehicleRender(ModEntities.FLUID_TRAILER.get(), new RenderVehicleWrapper<>(new RenderFluidTrailer()));
        this.registerVehicleRender(ModEntities.SEEDER.get(), new RenderVehicleWrapper<>(new RenderSeederTrailer()));
        this.registerVehicleRender(ModEntities.FERTILIZER.get(), new RenderVehicleWrapper<>(new RenderFertilizerTrailer()));

        /* Register Mod Exclusive Vehicles */
        if(ModList.get().isLoaded("cfm"))
        {
            this.registerVehicleRender(ModEntities.SOFA.get(), new RenderLandVehicleWrapper<>(new RenderCouch()));
            this.registerVehicleRender(ModEntities.BATH.get(), new RenderPlaneWrapper<>(new RenderBath()));
            this.registerVehicleRender(ModEntities.SOFACOPTER.get(), new RenderHelicopterWrapper<>(new RenderCouchHelicopter()));
        }

        RenderingRegistry.registerEntityRenderingHandler(ModEntities.JACK.get(), com.mrcrayfish.vehicle.client.render.JackRenderer::new);
    }

    private <T extends VehicleEntity & EntityRayTracer.IEntityRayTraceable, R extends AbstractRenderVehicle<T>> void registerVehicleRender(EntityType<T> type, RenderVehicleWrapper<T, R> wrapper)
    {
        RenderingRegistry.registerEntityRenderingHandler(type, manager -> new RenderEntityVehicle<>(manager, wrapper));
        VehicleRenderRegistry.registerRenderWrapper(type, wrapper);
    }

    private void bindTileEntityRenders()
    {
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.FLUID_EXTRACTOR.get(), FluidExtractorRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.FUEL_DRUM.get(), FuelDrumRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.VEHICLE_CRATE.get(), VehicleCrateRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.JACK.get(), com.mrcrayfish.vehicle.client.render.tileentity.JackRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.GAS_PUMP.get(), GasPumpRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.GAS_PUMP_TANK.get(), GasPumpTankRenderer::new);
    }

    private void registerKeyBindings()
    {
        ClientRegistry.registerKeyBinding(KEY_HORN);
        ClientRegistry.registerKeyBinding(KEY_CYCLE_SEATS);
    }

    private void registerScreenFactories()
    {
        ScreenManager.registerFactory(ModContainers.FLUID_EXTRACTOR.get(), FluidExtractorScreen::new);
        ScreenManager.registerFactory(ModContainers.FLUID_MIXER.get(), FluidMixerScreen::new);
        ScreenManager.registerFactory(ModContainers.EDIT_VEHICLE.get(), EditVehicleScreen::new);
        ScreenManager.registerFactory(ModContainers.WORKSTATION.get(), WorkstationScreen::new);
        ScreenManager.registerFactory(ModContainers.STORAGE.get(), StorageScreen::new);
    }

    private void registerItemColors()
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

    private void registerRayTraceConstructors()
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

    @Override
    public void playVehicleSound(PlayerEntity player, PoweredVehicleEntity vehicle)
    {
        Minecraft.getInstance().enqueue(() ->
        {
            Map<SoundType, ITickableSound> soundMap = SOUND_TRACKER.computeIfAbsent(vehicle.getUniqueID(), uuid -> new HashMap<>());
            if(vehicle.getRidingSound() != null && player.equals(Minecraft.getInstance().player))
            {
                ITickableSound sound = soundMap.get(SoundType.ENGINE_RIDING);
                if(sound == null || sound.isDonePlaying() || !Minecraft.getInstance().getSoundHandler().isPlaying(sound))
                {
                    sound = new MovingSoundVehicleRiding(player, vehicle);
                    soundMap.put(SoundType.ENGINE_RIDING, sound);
                    Minecraft.getInstance().getSoundHandler().play(sound);
                }
            }
            if(vehicle.getMovingSound() != null && !player.equals(Minecraft.getInstance().player))
            {
                ITickableSound sound = soundMap.get(SoundType.ENGINE);
                if(sound == null || sound.isDonePlaying() || !Minecraft.getInstance().getSoundHandler().isPlaying(sound))
                {
                    sound = new MovingSoundVehicle(vehicle);
                    soundMap.put(SoundType.ENGINE, sound);
                    Minecraft.getInstance().getSoundHandler().play(new MovingSoundVehicle(vehicle));
                }
            }
            if(vehicle.getHornSound() != null && !player.equals(Minecraft.getInstance().player))
            {
                ITickableSound sound = soundMap.get(SoundType.HORN);
                if(sound == null || sound.isDonePlaying() || !Minecraft.getInstance().getSoundHandler().isPlaying(sound))
                {
                    sound = new MovingSoundHorn(vehicle);
                    soundMap.put(SoundType.HORN, sound);
                    Minecraft.getInstance().getSoundHandler().play(sound);
                }
            }
            if(vehicle.getHornRidingSound() != null && player.equals(Minecraft.getInstance().player))
            {
                ITickableSound sound = soundMap.get(SoundType.HORN_RIDING);
                if(sound == null || sound.isDonePlaying() || !Minecraft.getInstance().getSoundHandler().isPlaying(sound))
                {
                    sound = new MovingSoundHornRiding(player, vehicle);
                    soundMap.put(SoundType.HORN_RIDING, sound);
                    Minecraft.getInstance().getSoundHandler().play(sound);
                }
            }
        });
    }

    @Override
    public void playSound(SoundEvent soundEvent, BlockPos pos, float volume, float pitch)
    {
        ISound sound = new SimpleSound(soundEvent, SoundCategory.BLOCKS, volume, pitch, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
        Minecraft.getInstance().deferTask(() -> Minecraft.getInstance().getSoundHandler().play(sound));
    }

    @Override
    public void playSound(SoundEvent soundEvent, float volume, float pitch)
    {
        Minecraft.getInstance().deferTask(() -> Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(soundEvent, volume, pitch)));
    }

    //@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onFogDensity(EntityViewRenderEvent.FogDensity event)
    {
        /*if(event.getEntity().isInsideOfMaterial(ModMaterials.FUELIUM))
        {
            event.setDensity(0.5F);
        }
        else
        {
            event.setDensity(0.01F);
        }
        event.setCanceled(true);*/
    }

    @Override
    public void syncStorageInventory(int entityId, CompoundNBT compound)
    {
        World world = Minecraft.getInstance().world;
        if(world == null) return;
        Entity entity = world.getEntityByID(entityId);
        if(entity instanceof IStorage)
        {
            IStorage wrapper = (IStorage) entity;
            wrapper.getInventory().read(compound);
        }
    }

    @Override
    public PoweredVehicleEntity.AccelerationDirection getAccelerationDirection(LivingEntity entity)
    {
        if(controllableLoaded)
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                if(Config.CLIENT.useTriggers.get())
                {
                    if(controller.getRTriggerValue() != 0.0F && controller.getLTriggerValue() == 0.0F)
                    {
                        return PoweredVehicleEntity.AccelerationDirection.FORWARD;
                    }
                    else if(controller.getLTriggerValue() != 0.0F && controller.getRTriggerValue() == 0.0F)
                    {
                        return PoweredVehicleEntity.AccelerationDirection.REVERSE;
                    }
                }

                boolean forward = controller.getButtonsStates().getState(Buttons.A);
                boolean reverse = controller.getButtonsStates().getState(Buttons.B);
                if(forward && reverse)
                {
                    return PoweredVehicleEntity.AccelerationDirection.CHARGING;
                }
                else if(forward)
                {
                    return PoweredVehicleEntity.AccelerationDirection.FORWARD;
                }
                else if(reverse)
                {
                    return PoweredVehicleEntity.AccelerationDirection.REVERSE;
                }
            }
        }

        GameSettings settings = Minecraft.getInstance().gameSettings;
        boolean forward = settings.keyBindForward.isKeyDown();
        boolean reverse = settings.keyBindBack.isKeyDown();
        if(forward && reverse)
        {
            return PoweredVehicleEntity.AccelerationDirection.CHARGING;
        }
        else if(forward)
        {
            return PoweredVehicleEntity.AccelerationDirection.FORWARD;
        }
        else if(reverse)
        {
            return PoweredVehicleEntity.AccelerationDirection.REVERSE;
        }

        return PoweredVehicleEntity.AccelerationDirection.fromEntity(entity);
    }

    @Override
    public PoweredVehicleEntity.TurnDirection getTurnDirection(LivingEntity entity)
    {
        if(controllableLoaded)
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                if(controller.getLThumbStickXValue() > 0.0F)
                {
                    return PoweredVehicleEntity.TurnDirection.RIGHT;
                }
                if(controller.getLThumbStickXValue() < 0.0F)
                {
                    return PoweredVehicleEntity.TurnDirection.LEFT;
                }
                if(controller.getButtonsStates().getState(Buttons.DPAD_RIGHT))
                {
                    return PoweredVehicleEntity.TurnDirection.RIGHT;
                }
                if(controller.getButtonsStates().getState(Buttons.DPAD_LEFT))
                {
                    return PoweredVehicleEntity.TurnDirection.LEFT;
                }
            }
        }
        if(entity.moveStrafing < 0)
        {
            return PoweredVehicleEntity.TurnDirection.RIGHT;
        }
        else if(entity.moveStrafing > 0)
        {
            return PoweredVehicleEntity.TurnDirection.LEFT;
        }
        return PoweredVehicleEntity.TurnDirection.FORWARD;
    }

    @Override
    public float getTargetTurnAngle(PoweredVehicleEntity vehicle, boolean drifting)
    {
        PoweredVehicleEntity.TurnDirection direction = vehicle.getTurnDirection();
        if(vehicle.getControllingPassenger() != null)
        {
            if(controllableLoaded)
            {
                Controller controller = Controllable.getController();
                if(controller != null)
                {
                    float turnNormal = controller.getLThumbStickXValue();
                    if(turnNormal != 0.0F)
                    {
                        float newTurnAngle = vehicle.turnAngle + ((vehicle.getMaxTurnAngle() * -turnNormal) - vehicle.turnAngle) * 0.15F;
                        if(Math.abs(newTurnAngle) > vehicle.getMaxTurnAngle())
                        {
                            return vehicle.getMaxTurnAngle() * direction.getDir();
                        }
                        return newTurnAngle;
                    }
                }
            }

            if(direction != PoweredVehicleEntity.TurnDirection.FORWARD)
            {
                float amount = direction.getDir() * vehicle.getTurnSensitivity();
                if(drifting)
                {
                    amount *= 0.45F;
                }
                float newTurnAngle = vehicle.turnAngle + amount;
                if(Math.abs(newTurnAngle) > vehicle.getMaxTurnAngle())
                {
                    return vehicle.getMaxTurnAngle() * direction.getDir();
                }
                return newTurnAngle;
            }
        }

        if(drifting)
        {
            return vehicle.turnAngle * 0.95F;
        }
        return vehicle.turnAngle * 0.75F;
    }

    @Override
    public boolean isDrifting()
    {
        if(controllableLoaded)
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                if(controller.getButtonsStates().getState(Buttons.RIGHT_BUMPER))
                {
                    return true;
                }
            }
        }
        return Minecraft.getInstance().gameSettings.keyBindJump.isKeyDown();
    }

    @Override
    public boolean isHonking()
    {
        if(controllableLoaded)
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                if(controller.isButtonPressed(Buttons.RIGHT_THUMB_STICK))
                {
                    return true;
                }
            }
        }
        return ClientProxy.KEY_HORN.isKeyDown();
    }

    @Override
    public PlaneEntity.FlapDirection getFlapDirection()
    {
        boolean flapUp = Minecraft.getInstance().gameSettings.keyBindJump.isKeyDown();
        boolean flapDown = Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown();
        if(controllableLoaded)
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                flapUp |= controller.getButtonsStates().getState(Buttons.RIGHT_BUMPER);
                flapDown |= controller.getButtonsStates().getState(Buttons.LEFT_BUMPER);
            }
        }
        return PlaneEntity.FlapDirection.fromInput(flapUp, flapDown);
    }

    @Override
    public HelicopterEntity.AltitudeChange getAltitudeChange()
    {
        boolean flapUp = Minecraft.getInstance().gameSettings.keyBindJump.isKeyDown();
        boolean flapDown = Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown();
        if(controllableLoaded)
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                flapUp |= controller.getButtonsStates().getState(Buttons.RIGHT_BUMPER);
                flapDown |= controller.getButtonsStates().getState(Buttons.LEFT_BUMPER);
            }
        }
        return HelicopterEntity.AltitudeChange.fromInput(flapUp, flapDown);
    }

    @Override
    public float getTravelDirection(HelicopterEntity vehicle)
    {
        if(controllableLoaded)
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                float xAxis = controller.getLThumbStickXValue();
                float yAxis = controller.getLThumbStickYValue();
                if(xAxis != 0.0F || yAxis != 0.0F)
                {
                    float angle = (float) Math.toDegrees(Math.atan2(-xAxis, yAxis)) + 180F;
                    return vehicle.rotationYaw + angle;
                }
            }
        }

        PoweredVehicleEntity.AccelerationDirection accelerationDirection = vehicle.getAcceleration();
        PoweredVehicleEntity.TurnDirection turnDirection = vehicle.getTurnDirection();
        if(vehicle.getControllingPassenger() != null)
        {
            if(accelerationDirection == PoweredVehicleEntity.AccelerationDirection.FORWARD)
            {
                return vehicle.rotationYaw + turnDirection.getDir() * -45F;
            }
            else if(accelerationDirection == PoweredVehicleEntity.AccelerationDirection.REVERSE)
            {
                return vehicle.rotationYaw + 180F + turnDirection.getDir() * 45F;
            }
            else
            {
                return vehicle.rotationYaw + turnDirection.getDir() * -90F;
            }
        }
        return vehicle.rotationYaw;
    }

    @Override
    public float getTravelSpeed(HelicopterEntity helicopter)
    {
        if(controllableLoaded)
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                float xAxis = controller.getLThumbStickXValue();
                float yAxis = controller.getLThumbStickYValue();
                if(xAxis != 0.0F || yAxis != 0.0F)
                {
                    return (float) Math.min(1.0, Math.sqrt(Math.pow(xAxis, 2) + Math.pow(yAxis, 2)));
                }
            }
        }
        return helicopter.getAcceleration() != PoweredVehicleEntity.AccelerationDirection.NONE || helicopter.getTurnDirection() != PoweredVehicleEntity.TurnDirection.FORWARD ? 1.0F : 0.0F;
    }

    @Override
    public float getPower(PoweredVehicleEntity vehicle)
    {
        if(controllableLoaded && Config.CLIENT.useTriggers.get())
        {
            Controller controller = Controllable.getController();
            if(controller != null)
            {
                PoweredVehicleEntity.AccelerationDirection accelerationDirection = vehicle.getAcceleration();
                if(accelerationDirection == PoweredVehicleEntity.AccelerationDirection.FORWARD)
                {
                    return controller.getRTriggerValue();
                }
                else if(accelerationDirection == PoweredVehicleEntity.AccelerationDirection.REVERSE)
                {
                    return controller.getLTriggerValue();
                }
            }
        }
        return 1.0F;
    }

    @Override
    public void syncEntityFluid(int entityId, FluidStack stack)
    {
        World world = Minecraft.getInstance().world;
        if(world == null) return;

        Entity entity = world.getEntityByID(entityId);
        if(entity == null) return;

        LazyOptional<IFluidHandler> optional = entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        optional.ifPresent(handler -> {
            if(handler instanceof FluidTank)
            {
                FluidTank tank = (FluidTank) handler;
                tank.setFluid(stack);
            }
        });
    }

    @Override
    public boolean canApplyVehicleYaw(Entity passenger)
    {
        if(passenger.equals(Minecraft.getInstance().player))
        {
            return Config.CLIENT.rotateCameraWithVehicle.get();
        }
        return false;
    }

    @Override
    public void syncPlayerSeat(int entityId, int seatIndex, UUID uuid)
    {
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        if(clientPlayer != null)
        {
            Entity entity = clientPlayer.worldClient.getEntityByID(entityId);
            if(entity instanceof VehicleEntity)
            {
                VehicleEntity vehicle = (VehicleEntity) entity;
                vehicle.getSeatTracker().setSeatIndex(seatIndex, uuid);
            }
        }
    }

    @Override
    public void syncHeldVehicle(int entityId, CompoundNBT compound)
    {
        World world = Minecraft.getInstance().world;
        if(world != null)
        {
            Entity entity = world.getEntityByID(entityId);
            if(entity instanceof PlayerEntity)
            {
                HeldVehicleDataHandler.setHeldVehicle((PlayerEntity) entity, compound);
            }
        }
    }

    @Override
    public void spawnWheelParticle(BlockPos pos, BlockState state, double x, double y, double z, Vec3d motion)
    {
        Minecraft mc = Minecraft.getInstance();
        World world = mc.world;
        if(world != null)
        {
            DiggingParticle particle = new DiggingParticle(world, x, y, z, motion.x, motion.y, motion.z, state);
            particle.setBlockPos(pos);
            particle.multiplyVelocity((float) motion.length());
            mc.particles.addEffect(particle);
        }
    }

    private enum SoundType
    {
        ENGINE,
        ENGINE_RIDING,
        HORN,
        HORN_RIDING;
    }
}
