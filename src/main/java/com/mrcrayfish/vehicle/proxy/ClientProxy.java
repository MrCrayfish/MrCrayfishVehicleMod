package com.mrcrayfish.vehicle.proxy;

import com.mrcrayfish.vehicle.client.ClientEvents;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.HeldVehicleEvents;
import com.mrcrayfish.vehicle.client.Models;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHorn;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHornRiding;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicle;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicleRiding;
import com.mrcrayfish.vehicle.client.model.CustomLoader;
import com.mrcrayfish.vehicle.client.render.*;
import com.mrcrayfish.vehicle.client.render.tileentity.FluidExtractorRenderer;
import com.mrcrayfish.vehicle.client.render.tileentity.FuelDrumRenderer;
import com.mrcrayfish.vehicle.client.render.tileentity.VehicleCrateRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.*;
import com.mrcrayfish.vehicle.entity.*;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.RegistrationHandler;
import com.mrcrayfish.vehicle.item.ItemKey;
import com.mrcrayfish.vehicle.item.ItemPart;
import com.mrcrayfish.vehicle.item.ItemSprayCan;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidExtractor;
import com.mrcrayfish.vehicle.tileentity.TileEntityFuelDrum;
import com.mrcrayfish.vehicle.tileentity.TileEntityVehicleCrate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
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
        registerLandVehicleRenderingHandler(EntityATV.class, new RenderATV());
        registerLandVehicleRenderingHandler(EntityDuneBuggy.class, new RenderDuneBuggy());
        registerLandVehicleRenderingHandler(EntityGoKart.class, new RenderGoKart());
        RenderingRegistry.registerEntityRenderingHandler(EntityShoppingCart.class, RenderShoppingCart::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMiniBike.class, RenderMiniBike::new);
        registerLandVehicleRenderingHandler(EntityBumperCar.class, new RenderBumperCar());
        RenderingRegistry.registerEntityRenderingHandler(EntityJetSki.class, RenderJetSki::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySpeedBoat.class, RenderSpeedBoat::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityAluminumBoat.class, RenderAluminumBoat::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySmartCar.class, RenderSmartCar::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityLawnMower.class, RenderLawnMower::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMoped.class, RenderMoped::new);
        registerPlaneRenderingHandler(EntitySportsPlane.class, new RenderSportsPlane());
        registerLandVehicleRenderingHandler(EntityGolfCart.class, new RenderGolfCart());
        registerLandVehicleRenderingHandler(EntityOffRoader.class, new RenderOffRoader());

        if(Loader.isModLoaded("cfm"))
        {
            registerLandVehicleRenderingHandler(EntityCouch.class, new RenderCouch());
            RenderingRegistry.registerEntityRenderingHandler(EntityBath.class, RenderBath::new);
            registerHelicopterRenderingHandler(EntitySofacopter.class, new RenderCouchHelicopter());
        }

        RenderingRegistry.registerEntityRenderingHandler(EntityTrailer.class, RenderTrailer::new);

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

    private <T extends EntityLandVehicle & EntityRaytracer.IEntityRaytraceable> void registerLandVehicleRenderingHandler(Class<T> clazz, AbstractRenderLandVehicle<T> render)
    {
        RenderingRegistry.registerEntityRenderingHandler(clazz, manager -> new RenderVehicleLand<>(manager, render));
        VehicleRenderRegistry.registerRender(clazz, render);
    }

    private <T extends EntityPlane & EntityRaytracer.IEntityRaytraceable> void registerPlaneRenderingHandler(Class<T> clazz, AbstractRenderVehicle<T> render)
    {
        RenderingRegistry.registerEntityRenderingHandler(clazz, manager -> new RenderVehicleAir<>(manager, render));
        VehicleRenderRegistry.registerRender(clazz, render);
    }

    private <T extends EntityHelicopter & EntityRaytracer.IEntityRaytraceable> void registerHelicopterRenderingHandler(Class<T> clazz, AbstractRenderVehicle<T> render)
    {
        RenderingRegistry.registerEntityRenderingHandler(clazz, manager -> new RenderVehicleHelicopter<>(manager, render));
        VehicleRenderRegistry.registerRender(clazz, render);
    }

    private <T extends EntityVehicle & EntityRaytracer.IEntityRaytraceable> void registerVehicleRenderingHandler(Class<T> clazz, AbstractRenderVehicle<T> render)
    {
        RenderingRegistry.registerEntityRenderingHandler(clazz, manager -> new RenderVehicle<>(manager, render));
        VehicleRenderRegistry.registerRender(clazz, render);
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
            if(item instanceof ItemSprayCan || item instanceof ItemKey || (item instanceof ItemPart && ((ItemPart) item).isColored()))
            {
                Minecraft.getMinecraft().getItemColors().registerItemColorHandler(color, item);
            }
        });
        EntityRaytracer.init();
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
}
