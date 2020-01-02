package com.mrcrayfish.vehicle.proxy;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.Buttons;
import com.mrcrayfish.controllable.client.Controller;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.ClientEvents;
import com.mrcrayfish.vehicle.client.ControllerEvents;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.HeldVehicleEvents;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHorn;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHornRiding;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicle;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicleRiding;
import com.mrcrayfish.vehicle.client.render.*;
import com.mrcrayfish.vehicle.client.render.tileentity.*;
import com.mrcrayfish.vehicle.client.render.vehicle.*;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.entity.*;
import com.mrcrayfish.vehicle.entity.PlaneEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.item.KeyItem;
import com.mrcrayfish.vehicle.item.PartItem;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
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

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public class ClientProxy implements Proxy
{
    public static final KeyBinding KEY_HORN = new KeyBinding("key.horn", GLFW.GLFW_KEY_H, "key.categories.vehicle");

    public static boolean controllableLoaded = false;
    
    @Override
    public void setupClient()
    {
        /* Register Vehicles */
        registerVehicleRender(ModEntities.ATV, new RenderLandVehicleWrapper<>(new RenderATV()));
        registerVehicleRender(ModEntities.DUNE_BUGGY, new RenderLandVehicleWrapper<>(new RenderDuneBuggy()));
        registerVehicleRender(ModEntities.GO_KART, new RenderLandVehicleWrapper<>(new RenderGoKart()));
        registerVehicleRender(ModEntities.SHOPPING_CART, new RenderLandVehicleWrapper<>(new RenderShoppingCart()));
        registerVehicleRender(ModEntities.MINI_BIKE, new RenderMotorcycleWrapper<>(new RenderMiniBike()));
        registerVehicleRender(ModEntities.BUMPER_CAR, new RenderLandVehicleWrapper<>(new RenderBumperCar()));
        registerVehicleRender(ModEntities.JET_SKI, new RenderBoatWrapper<>(new RenderJetSki()));
        registerVehicleRender(ModEntities.SPEED_BOAT, new RenderBoatWrapper<>(new RenderSpeedBoat()));
        registerVehicleRender(ModEntities.ALUMINUM_BOAT, new RenderBoatWrapper<>(new RenderAluminumBoat()));
        registerVehicleRender(ModEntities.SMART_CAR, new RenderLandVehicleWrapper<>(new RenderSmartCar()));
        registerVehicleRender(ModEntities.LAWN_MOWER, new RenderLandVehicleWrapper<>(new RenderLawnMower()));
        registerVehicleRender(ModEntities.MOPED, new RenderMotorcycleWrapper<>(new RenderMoped()));
        registerVehicleRender(ModEntities.SPORTS_PLANE, new RenderPlaneWrapper<>(new RenderSportsPlane()));
        registerVehicleRender(ModEntities.GOLF_CART, new RenderLandVehicleWrapper<>(new RenderGolfCart()));
        registerVehicleRender(ModEntities.OFF_ROADER, new RenderLandVehicleWrapper<>(new RenderOffRoader()));
        registerVehicleRender(ModEntities.TRACTOR, new RenderLandVehicleWrapper<>(new RenderTractor()));

        /* Register Trailers */
        registerVehicleRender(ModEntities.VEHICLE_TRAILER, new RenderVehicleWrapper<>(new RenderVehicleTrailer()));
        registerVehicleRender(ModEntities.STORAGE_TRAILER, new RenderVehicleWrapper<>(new RenderStorageTrailer()));
        registerVehicleRender(ModEntities.FLUID_TRAILER, new RenderVehicleWrapper<>(new RenderFluidTrailer()));
        registerVehicleRender(ModEntities.SEEDER, new RenderVehicleWrapper<>(new RenderSeederTrailer()));
        registerVehicleRender(ModEntities.FERTILIZER, new RenderVehicleWrapper<>(new RenderFertilizerTrailer()));

        /* Register Mod Exclusive Vehicles */
        if(ModList.get().isLoaded("cfm"))
        {
            registerVehicleRender(ModEntities.SOFA, new RenderLandVehicleWrapper<>(new RenderCouch()));
            registerVehicleRender(ModEntities.BATH, new RenderPlaneWrapper<>(new RenderBath()));
            registerVehicleRender(ModEntities.SOFACOPTER, new RenderHelicopterWrapper<>(new RenderCouchHelicopter()));
        }

        /* Client Events */
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new HeldVehicleEvents());
        MinecraftForge.EVENT_BUS.register(this);

        /* Tile Entity Special Renderer*/
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.FLUID_EXTRACTOR, FluidExtractorRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.FUEL_DRUM, FuelDrumRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.VEHICLE_CRATE, VehicleCrateRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.JACK, JackRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.GAS_PUMP, GasPumpRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.GAS_PUMP_TANK, GasPumpTankRenderer::new);

        /* Key Bindings */
        ClientRegistry.registerKeyBinding(KEY_HORN);

        //TODO add custom loader
        //ModelLoaderRegistry.registerLoader(new CustomLoader());
        //ModelLoaderRegistry.registerLoader(new ResourceLocation(Reference.MOD_ID, "ramp"), new CustomLoader());

        IItemColor color = (stack, index) ->
        {
            if(index == 0 && stack.hasTag() && stack.getTag().contains("Color", Constants.NBT.TAG_INT))
            {
                return stack.getTag().getInt("Color");
            }
            return 0;
        };

        ForgeRegistries.ITEMS.forEach(item ->
        {
            if(item instanceof SprayCanItem || item instanceof KeyItem || (item instanceof PartItem && ((PartItem) item).isColored()))
            {
                Minecraft.getInstance().getItemColors().register(color, item);
            }
        });

        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener((stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> CompletableFuture.runAsync(() ->
        {
            FluidUtils.clearCacheFluidColor();
            EntityRaytracer.clearDataForReregistration();
        }));

        if(ModList.get().isLoaded("controllable"))
        {
            controllableLoaded = true;
            MinecraftForge.EVENT_BUS.register(new ControllerEvents());
        }
    }

    private <T extends VehicleEntity & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>> void registerVehicleRender(EntityType<T> type, RenderVehicleWrapper<T, R> wrapper)
    {
        RenderingRegistry.registerEntityRenderingHandler(type, manager -> new RenderEntityVehicle<>(manager, wrapper));
        VehicleRenderRegistry.registerRenderWrapper(type, wrapper);
    }

    @Override
    public void playVehicleSound(PlayerEntity player, PoweredVehicleEntity vehicle)
    {
        Minecraft.getInstance().deferTask(() ->
        {
            if(vehicle.getRidingSound() != null)
            {
                Minecraft.getInstance().getSoundHandler().play(new MovingSoundVehicleRiding(player, vehicle));
            }
            if(vehicle.getMovingSound() != null)
            {
                Minecraft.getInstance().getSoundHandler().play(new MovingSoundVehicle(vehicle));
            }
            if(vehicle.getHornSound() != null)
            {
                Minecraft.getInstance().getSoundHandler().play(new MovingSoundHorn(vehicle));
            }
            if(vehicle.getHornRidingSound() != null)
            {
                Minecraft.getInstance().getSoundHandler().play(new MovingSoundHornRiding(player, vehicle));
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
    public void openVehicleEditWindow(int entityId, int windowId)
    {
        //TODO convert to new gui system
        /*PlayerEntity player = Minecraft.getInstance().player;
        if(player != null)
        {
            World world = player.getEntityWorld();
            Entity entity = world.getEntityByID(entityId);
            if(entity instanceof PoweredVehicleEntity)
            {
                PoweredVehicleEntity poweredVehicle = (PoweredVehicleEntity) entity;
                Minecraft.getInstance().displayGuiScreen(new EditVehicleScreen(poweredVehicle.getVehicleInventory(), poweredVehicle, player));
                player.openContainer.windowId = windowId;
            }
        }*/
    }

    @Override
    public void syncStorageInventory(int entityId, CompoundNBT compound)
    {
        World world = Minecraft.getInstance().world;
        if(world == null)
            return;
        Entity entity = world.getEntityByID(entityId);
        if(entity instanceof IStorage)
        {
            IStorage wrapper = (IStorage) entity;
            wrapper.getInventory().read(compound);
        }
    }

    @Override
    public void openStorageWindow(int entityId, int windowId)
    {
        //TODO convert to normal gui system
        /*EntityPlayer player = Minecraft.getMinecraft().player;
        World world = player.getEntityWorld();
        Entity entity = world.getEntityByID(entityId);
        if(entity instanceof IStorage)
        {
            IStorage wrapper = (IStorage) entity;
            Minecraft.getMinecraft().displayGuiScreen(new StorageScreen(player.inventory, wrapper.getInventory()));
            player.openContainer.windowId = windowId;
        }*/
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
                else if(controller.getButtonsStates().getState(Buttons.A))
                {
                    return PoweredVehicleEntity.AccelerationDirection.FORWARD;
                }
                else if(controller.getButtonsStates().getState(Buttons.B))
                {
                    return PoweredVehicleEntity.AccelerationDirection.REVERSE;
                }

            }
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
        if(world == null)
            return;

        Entity entity = world.getEntityByID(entityId);
        if(entity == null)
            return;

        LazyOptional<IFluidHandler> optional = entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        optional.ifPresent(handler ->
        {
            if(handler instanceof FluidTank)
            {
                FluidTank tank = (FluidTank) handler;
                tank.setFluid(stack);
            }
        });
    }
}
