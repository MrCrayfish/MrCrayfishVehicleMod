package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageHitchTrailer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ControllerEvents
{
    public static Controller controller = null;

    @SubscribeEvent
    public static void onRender(RenderWorldLastEvent event)
    {
        if(!VehicleConfig.CLIENT.experimental.controllerSupport || controller == null)
            return;

        /* Updates the values in the controller */
        controller.poll();
    }

    @SubscribeEvent
    public static void onTick(TickEvent.RenderTickEvent event)
    {
        if(!VehicleConfig.CLIENT.experimental.controllerSupport || controller == null)
            return;

        while(Controllers.next())
        {
            if(Controllers.getEventSource().equals(controller))
            {
                if(Controllers.getEventButtonState() && Controllers.getEventControlIndex() == 11)
                {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    if(Minecraft.getMinecraft().currentScreen == null && player.getRidingEntity() instanceof EntityVehicle)
                    {
                        EntityVehicle vehicle = (EntityVehicle) player.getRidingEntity();
                        if(vehicle.canTowTrailer())
                        {
                            PacketHandler.INSTANCE.sendToServer(new MessageHitchTrailer(vehicle.getTrailer() == null));
                        }
                    }
                }
            }
        }
    }
}
