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
import com.mrcrayfish.vehicle.client.gui.GuiStorage;
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
        /* Vehicles */
        registerVehicleRender(EntityATV.class, new RenderLandVehicleWrapper<>(new RenderATV()));
        registerVehicleRender(EntityDuneBuggy.class, new RenderLandVehicleWrapper<>(new RenderDuneBuggy()));
        registerVehicleRender(EntityGoKart.class, new RenderLandVehicleWrapper<>(new RenderGoKart()));
        registerVehicleRender(EntityShoppingCart.class, new RenderLandVehicleWrapper<>(new RenderShoppingCart()));
        registerVehicleRender(EntityMiniBike.class, new RenderMotorcycleWrapper<>(new RenderMiniBike()));
        registerVehicleRender(EntityBumperCar.class, new RenderLandVehicleWrapper<>(new RenderBumperCar()));
        registerVehicleRender(EntityJetSki.class, new RenderBoatWrapper<>(new RenderJetSki()));
        registerVehicleRender(EntitySpeedBoat.class, new RenderBoatWrapper<>(new RenderSpeedBoat()));
        registerVehicleRender(EntityAluminumBoat.class, new RenderBoatWrapper<>(new RenderAluminumBoat()));
        registerVehicleRender(EntitySmartCar.class, new RenderLandVehicleWrapper<>(new RenderSmartCar()));
        registerVehicleRender(EntityLawnMower.class, new RenderLandVehicleWrapper<>(new RenderLawnMower()));
        registerVehicleRender(EntityMoped.class, new RenderMotorcycleWrapper<>(new RenderMoped()));
        registerVehicleRender(EntitySportsPlane.class, new RenderPlaneWrapper<>(new RenderSportsPlane()));
        registerVehicleRender(EntityGolfCart.class, new RenderLandVehicleWrapper<>(new RenderGolfCart()));
        registerVehicleRender(EntityOffRoader.class, new RenderLandVehicleWrapper<>(new RenderOffRoader()));

        /* Mod Exclusive Vehicles */
        if(Loader.isModLoaded("cfm"))
        {
            registerVehicleRender(EntityCouch.class, new RenderLandVehicleWrapper<>(new RenderCouch()));
            registerVehicleRender(EntityBath.class, new RenderPlaneWrapper<>(new RenderBath()));
            registerVehicleRender(EntitySofacopter.class, new RenderHelicopterWrapper<>(new RenderCouchHelicopter()));
        }

        /* Trailers */
        registerVehicleRender(EntityVehicleTrailer.class, new RenderVehicleWrapper<>(new RenderVehicleTrailer()));
        registerVehicleRender(EntityStorageTrailer.class, new RenderVehicleWrapper<>(new RenderStorageTrailer()));
        registerVehicleRender(EntitySeederTrailer.class, new RenderVehicleWrapper<>(new RenderSeederTrailer()));
        registerVehicleRender(EntityFertilizerTrailer.class, new RenderVehicleWrapper<>(new RenderFertilizerTrailer()));

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

    private <T extends EntityVehicle & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>> void registerVehicleRender(Class<T> clazz, RenderVehicleWrapper<T, R> wrapper)
    {
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

    @Override
    public void openStorageWindow(int entityId, int windowId)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        World world = player.getEntityWorld();
        Entity entity = world.getEntityByID(entityId);
        if(entity instanceof IStorage)
        {
            IStorage wrapper = (IStorage) entity;
            Minecraft.getMinecraft().displayGuiScreen(new GuiStorage(player.inventory, wrapper.getInventory()));
            player.openContainer.windowId = windowId;
        }
    }
}
