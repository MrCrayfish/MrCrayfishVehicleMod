package com.mrcrayfish.vehicle.proxy;

import com.mrcrayfish.vehicle.client.ClientEvents;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.HeldVehicleEvents;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHorn;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHornRiding;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicle;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicleRiding;
import com.mrcrayfish.vehicle.client.render.vehicle.*;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.init.RegistrationHandler;
import com.mrcrayfish.vehicle.item.ItemPart;
import com.mrcrayfish.vehicle.item.ItemSprayCan;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.input.Keyboard;

import java.awt.*;

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

        if(Loader.isModLoaded("cfm"))
        {
            RenderingRegistry.registerEntityRenderingHandler(EntityCouch.class, RenderCouch::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityBath.class, RenderBath::new);
        }

        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new HeldVehicleEvents());
        ClientRegistry.registerKeyBinding(KEY_HORN);
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
    public void playVehicleSound(EntityPlayer player, EntityVehicle vehicle)
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
}
