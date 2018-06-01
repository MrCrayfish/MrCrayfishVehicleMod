package com.mrcrayfish.vehicle.proxy;

import org.lwjgl.input.Keyboard;

import com.mrcrayfish.vehicle.client.ClientEvents;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHorn;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHornRiding;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicle;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicleRiding;
import com.mrcrayfish.vehicle.client.render.vehicle.RenderATV;
import com.mrcrayfish.vehicle.client.render.vehicle.RenderBumperCar;
import com.mrcrayfish.vehicle.client.render.vehicle.RenderCouch;
import com.mrcrayfish.vehicle.client.render.vehicle.RenderDuneBuggy;
import com.mrcrayfish.vehicle.client.render.vehicle.RenderGoKart;
import com.mrcrayfish.vehicle.client.render.vehicle.RenderJetSki;
import com.mrcrayfish.vehicle.client.render.vehicle.RenderMiniBike;
import com.mrcrayfish.vehicle.client.render.vehicle.RenderShoppingCart;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.EntityATV;
import com.mrcrayfish.vehicle.entity.vehicle.EntityBumperCar;
import com.mrcrayfish.vehicle.entity.vehicle.EntityCouch;
import com.mrcrayfish.vehicle.entity.vehicle.EntityDuneBuggy;
import com.mrcrayfish.vehicle.entity.vehicle.EntityGoKart;
import com.mrcrayfish.vehicle.entity.vehicle.EntityJetSki;
import com.mrcrayfish.vehicle.entity.vehicle.EntityMiniBike;
import com.mrcrayfish.vehicle.entity.vehicle.EntityShoppingCart;
import com.mrcrayfish.vehicle.init.RegistrationHandler;
import com.mrcrayfish.vehicle.item.ItemColoredPart;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandler;

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

        if(Loader.isModLoaded("cfm"))
        {
            RenderingRegistry.registerEntityRenderingHandler(EntityCouch.class, RenderCouch::new);
        }

        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        ClientRegistry.registerKeyBinding(KEY_HORN);
    }

    @Override
    public void init() {
    	IItemColor color = (stack, index) -> {
        	if(stack.hasTagCompound() && index == 1) {
        		return stack.getTagCompound().getInteger("color");
        	}
        	return 0xFFFFFF; // White
        };
        
        for(Item item : RegistrationHandler.Items.getItems()) {
        	if(item instanceof ItemColoredPart) {
        		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(color, item);
        	}
        }
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
