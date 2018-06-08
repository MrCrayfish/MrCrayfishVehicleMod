package net.hdt.hva.proxy;

import net.hdt.hva.client.render.vehicle.*;
import net.hdt.hva.entity.vehicle.*;
import net.hdt.hva.init.RegistrationHandler;
import net.hdt.hva.items.ItemColoredPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

public class CProxy extends SProxy {

    public static final KeyBinding KEY_HOVERBOARD_LOWERING = new KeyBinding("key.hoverboard_lowering", Keyboard.KEY_X, "key.categories.movement");

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        RenderingRegistry.registerEntityRenderingHandler(EntityRaceCar.class, RenderRacingCar::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityDBIceMotorcart.class, RenderDBIceMotorcart::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRbhTE22.class, RenderRbhTE22::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityC62SteamLocomotive.class, RenderC62SteamLocomotive::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityC62SteamLocomotiveTender.class, RenderC62SteamLocomotiveTender::new);

        RenderingRegistry.registerEntityRenderingHandler(EntityBMXBike.class, RenderBMXBike::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityScooter.class, RenderScooter::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMotorcycle.class, RenderMotorcycle::new);

        RenderingRegistry.registerEntityRenderingHandler(EntitySleight.class, RenderSleight::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySantaSleight.class, RenderSantaSleight::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySnowMobile.class, RenderSnowMobile::new);

        RenderingRegistry.registerEntityRenderingHandler(EntityHighBoosterBoard.class, RenderHighBoosterBoard::new);

        ClientRegistry.registerKeyBinding(KEY_HOVERBOARD_LOWERING);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        IItemColor color = (stack, index) -> {
            if(stack.hasTagCompound() && index == 1) {
                return stack.getTagCompound().getInteger("color");
            }
            return 0x7f0000; // Red
        };

        for(Item item : RegistrationHandler.Items.getItems()) {
            if(item instanceof ItemColoredPart) {
                Minecraft.getMinecraft().getItemColors().registerItemColorHandler(color, item);
            }
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

}
