package com.mrcrayfish.vehicle.proxy;

import com.mrcrayfish.vehicle.client.ClientEvents;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.HeldVehicleEvents;
import com.mrcrayfish.vehicle.client.Models;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHorn;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHornRiding;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicle;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicleRiding;
import com.mrcrayfish.vehicle.client.gui.GuiEditVehicle;
import com.mrcrayfish.vehicle.client.model.CustomLoader;
import com.mrcrayfish.vehicle.client.render.*;
import com.mrcrayfish.vehicle.client.render.tileentity.FluidExtractorRenderer;
import com.mrcrayfish.vehicle.client.render.tileentity.FuelDrumRenderer;
import com.mrcrayfish.vehicle.client.render.tileentity.JackRenderer;
import com.mrcrayfish.vehicle.client.render.tileentity.VehicleCrateRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.*;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.entity.*;
import com.mrcrayfish.vehicle.entity.trailer.EntityFertilizerTrailer;
import com.mrcrayfish.vehicle.entity.trailer.EntitySeederTrailer;
import com.mrcrayfish.vehicle.entity.trailer.EntityStorageTrailer;
import com.mrcrayfish.vehicle.entity.trailer.EntityVehicleTrailer;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.RegistrationHandler;
import com.mrcrayfish.vehicle.item.ItemKey;
import com.mrcrayfish.vehicle.item.ItemPart;
import com.mrcrayfish.vehicle.item.ItemSprayCan;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidExtractor;
import com.mrcrayfish.vehicle.tileentity.TileEntityFuelDrum;
import com.mrcrayfish.vehicle.tileentity.TileEntityJack;
import com.mrcrayfish.vehicle.tileentity.TileEntityVehicleCrate;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

/**
 * Author: MrCrayfish
 */
public class ClientProxy implements Proxy
{
    public static final KeyBinding KEY_HORN = new KeyBinding("key.horn", Keyboard.KEY_H, "key.categories.vehicle");

    @Override
    public void preInit()
    {
        registerLandVehicleRender(EntityATV.class, new RenderATV());
        registerLandVehicleRender(EntityDuneBuggy.class, new RenderDuneBuggy());
        registerLandVehicleRender(EntityGoKart.class, new RenderGoKart());
        registerLandVehicleRender(EntityShoppingCart.class, new RenderShoppingCart());
        registerMotorcycleRender(EntityMiniBike.class, new RenderMiniBike());
        registerLandVehicleRender(EntityBumperCar.class, new RenderBumperCar());
        registerBoatRender(EntityJetSki.class, new RenderJetSki());
        registerBoatRender(EntitySpeedBoat.class, new RenderSpeedBoat());
        registerBoatRender(EntityAluminumBoat.class, new RenderAluminumBoat());
        registerLandVehicleRender(EntitySmartCar.class, new RenderSmartCar());
        registerLandVehicleRender(EntityLawnMower.class, new RenderLawnMower());
        registerMotorcycleRender(EntityMoped.class, new RenderMoped());
        registerPlaneRender(EntitySportsPlane.class, new RenderSportsPlane());
        registerLandVehicleRender(EntityGolfCart.class, new RenderGolfCart());
        registerLandVehicleRender(EntityOffRoader.class, new RenderOffRoader());

        if(Loader.isModLoaded("cfm"))
        {
            registerLandVehicleRender(EntityCouch.class, new RenderCouch());
            registerPlaneRender(EntityBath.class, new RenderBath());
            registerHelicopterRender(EntitySofacopter.class, new RenderCouchHelicopter());
        }

        RenderingRegistry.registerEntityRenderingHandler(EntityVehicleTrailer.class, RenderVehicleTrailer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityStorageTrailer.class, RenderChestTrailer::new);
        registerVehicleRender(EntitySeederTrailer.class, new RenderSeederTrailer());
        registerVehicleRender(EntityFertilizerTrailer.class, new RenderFertilizerTrailer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityJack.class, new JackRenderer());

        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new HeldVehicleEvents());
        MinecraftForge.EVENT_BUS.register(this);

        ClientRegistry.registerKeyBinding(KEY_HORN);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluidExtractor.class, new FluidExtractorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFuelDrum.class, new FuelDrumRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVehicleCrate.class, new VehicleCrateRenderer());

        ModelLoaderRegistry.registerLoader(new CustomLoader());

        Models.registerModels(ModItems.MODELS);
    }

    private <T extends EntityLandVehicle & EntityRaytracer.IEntityRaytraceable> void registerLandVehicleRender(Class<T> clazz, AbstractRenderLandVehicle<T> render)
    {
        RenderLandVehicleWrapper<T, AbstractRenderLandVehicle<T>> wrapper = new RenderLandVehicleWrapper<>(render);
        RenderingRegistry.registerEntityRenderingHandler(clazz, manager -> new RenderEntityVehicle<>(manager, wrapper));
        VehicleRenderRegistry.registerRenderWrapper(clazz, wrapper);
    }

    private <T extends EntityPlane & EntityRaytracer.IEntityRaytraceable> void registerPlaneRender(Class<T> clazz, AbstractRenderVehicle<T> render)
    {
        RenderPlaneWrapper<T, AbstractRenderVehicle<T>> wrapper = new RenderPlaneWrapper<>(render);
        RenderingRegistry.registerEntityRenderingHandler(clazz, manager -> new RenderEntityVehicle<>(manager, wrapper));
        VehicleRenderRegistry.registerRenderWrapper(clazz, wrapper);
    }

    private <T extends EntityHelicopter & EntityRaytracer.IEntityRaytraceable> void registerHelicopterRender(Class<T> clazz, AbstractRenderVehicle<T> render)
    {
        RenderHelicopterWrapper<T, AbstractRenderVehicle<T>> wrapper = new RenderHelicopterWrapper<>(render);
        RenderingRegistry.registerEntityRenderingHandler(clazz, manager -> new RenderEntityVehicle<>(manager, wrapper));
        VehicleRenderRegistry.registerRenderWrapper(clazz, wrapper);
    }

    private <T extends EntityMotorcycle & EntityRaytracer.IEntityRaytraceable> void registerMotorcycleRender(Class<T> clazz, AbstractRenderLandVehicle<T> render)
    {
        RenderMotorcycleWrapper<T, AbstractRenderLandVehicle<T>> wrapper = new RenderMotorcycleWrapper<>(render);
        RenderingRegistry.registerEntityRenderingHandler(clazz, manager -> new RenderEntityVehicle<>(manager, wrapper));
        VehicleRenderRegistry.registerRenderWrapper(clazz, wrapper);
    }

    private <T extends EntityBoat & EntityRaytracer.IEntityRaytraceable> void registerBoatRender(Class<T> clazz, AbstractRenderVehicle<T> render)
    {
        RenderBoatWrapper<T, AbstractRenderVehicle<T>> wrapper = new RenderBoatWrapper<>(render);
        RenderingRegistry.registerEntityRenderingHandler(clazz, manager -> new RenderEntityVehicle<>(manager, wrapper));
        VehicleRenderRegistry.registerRenderWrapper(clazz, wrapper);
    }

    private <T extends EntityVehicle & EntityRaytracer.IEntityRaytraceable> void registerVehicleRender(Class<T> clazz, AbstractRenderVehicle<T> render)
    {
        RenderVehicleWrapper<T, AbstractRenderVehicle<T>> wrapper = new RenderVehicleWrapper<>(render);
        RenderingRegistry.registerEntityRenderingHandler(clazz, manager -> new RenderEntityVehicle<>(manager, wrapper));
        VehicleRenderRegistry.registerRenderWrapper(clazz, wrapper);
    }

    @Override
    public void init()
    {
        IItemColor color = (stack, index) ->
        {
            if(index == 0 && stack.hasTagCompound() && stack.getTagCompound().hasKey("color", Constants.NBT.TAG_INT))
            {
                return stack.getTagCompound().getInteger("color");
            }
            return -1;
        };
        RegistrationHandler.Items.getItems().forEach(item ->
        {
            if(item instanceof ItemSprayCan || item instanceof ItemKey || (item instanceof ItemPart && ((ItemPart) item).isColored()) || item == ModItems.MODELS)
            {
                Minecraft.getMinecraft().getItemColors().registerItemColorHandler(color, item);
            }
        });
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(resourceManager ->
        {
            FluidUtils.clearCacheFluidColor();
            EntityRaytracer.clearDataForReregistration();
        });
    }

    @Override
    public void playVehicleSound(EntityPlayer player, EntityPoweredVehicle vehicle)
    {
        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            if(vehicle.getRidingSound() != null)
            {
                Minecraft.getMinecraft().getSoundHandler().playSound(new MovingSoundVehicleRiding(player, vehicle));
            }
            if(vehicle.getMovingSound() != null)
            {
                Minecraft.getMinecraft().getSoundHandler().playSound(new MovingSoundVehicle(vehicle));
            }
            if(vehicle.getHornSound() != null)
            {
                Minecraft.getMinecraft().getSoundHandler().playSound(new MovingSoundHorn(vehicle));
            }
            if(vehicle.getHornRidingSound() != null)
            {
                Minecraft.getMinecraft().getSoundHandler().playSound(new MovingSoundHornRiding(player, vehicle));
            }
        });
    }

    @Override
    public void playSound(SoundEvent soundEvent, BlockPos pos, float volume, float pitch)
    {
        ISound sound = new PositionedSoundRecord(soundEvent, SoundCategory.BLOCKS, volume, pitch, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
        Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().getSoundHandler().playSound(sound));
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
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
        EntityPlayer player = Minecraft.getMinecraft().player;
        World world = player.getEntityWorld();
        Entity entity = world.getEntityByID(entityId);
        if(entity instanceof EntityPoweredVehicle)
        {
            EntityPoweredVehicle poweredVehicle = (EntityPoweredVehicle) entity;
            Minecraft.getMinecraft().displayGuiScreen(new GuiEditVehicle(poweredVehicle.getVehicleInventory(), poweredVehicle, player));
            player.openContainer.windowId = windowId;
        }
    }

    @Override
    public void syncStorageInventory(int entityId, NBTTagCompound tagCompound)
    {
        World world = Minecraft.getMinecraft().world;
        Entity entity = world.getEntityByID(entityId);
        if(entity instanceof IStorage)
        {
            IStorage wrapper = (IStorage) entity;
            wrapper.getInventory().readFromNBT(tagCompound);
        }
    }
}
