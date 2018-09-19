package com.mrcrayfish.vehicle.proxy;

import com.mrcrayfish.vehicle.client.ClientEvents;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.HeldVehicleEvents;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHorn;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHornRiding;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicle;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicleRiding;
import com.mrcrayfish.vehicle.client.model.CustomLoader;
import com.mrcrayfish.vehicle.client.render.tileentity.FuelDrumRenderer;
import com.mrcrayfish.vehicle.client.render.tileentity.FluidExtractorRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.*;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.init.RegistrationHandler;
import com.mrcrayfish.vehicle.item.ItemPart;
import com.mrcrayfish.vehicle.item.ItemSprayCan;
import com.mrcrayfish.vehicle.tileentity.TileEntityFuelDrum;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
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
        RenderingRegistry.registerEntityRenderingHandler(EntityATV.class, RenderATV::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityDuneBuggy.class, RenderDuneBuggy::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityGoKart.class, RenderGoKart::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityShoppingCart.class, RenderShoppingCart::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMiniBike.class, RenderMiniBike::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBumperCar.class, RenderBumperCar::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityJetSki.class, RenderJetSki::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySpeedBoat.class, RenderSpeedBoat::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityAluminumBoat.class, RenderAluminumBoat::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySmartCar.class, RenderSmartCar::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityLawnMower.class, RenderLawnMower::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMoped.class, RenderMoped::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySportsPlane.class, RenderSportsPlane::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityGolfCart.class, RenderGolfCart::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityOffRoader.class, RenderOffRoader::new);

        if(Loader.isModLoaded("cfm"))
        {
            RenderingRegistry.registerEntityRenderingHandler(EntityCouch.class, RenderCouch::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityBath.class, RenderBath::new);
        }

        RenderingRegistry.registerEntityRenderingHandler(EntityTrailer.class, RenderTrailer::new);

        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new HeldVehicleEvents());
        MinecraftForge.EVENT_BUS.register(this);

        ClientRegistry.registerKeyBinding(KEY_HORN);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluidExtractor.class, new FluidExtractorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFuelDrum.class, new FuelDrumRenderer());

        ModelLoaderRegistry.registerLoader(new CustomLoader());

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
            if(item instanceof ItemSprayCan || (item instanceof ItemPart && ((ItemPart) item).isColored()))
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
