package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageHitchTrailer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.InputUpdateEvent;
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

        if(event.phase == TickEvent.Phase.END)
            return;

        /* Handles rotating the yaw of player */
        if(controller.getZAxisValue() != 0.0F)
        {
            EntityPlayer player = Minecraft.getMinecraft().player;
            player.rotationYaw += 2.0F * (controller.getZAxisValue() > 0.0F ? 1 : -1) * Math.abs(controller.getZAxisValue());
        }

        /* Handles rotating the pitch of player */
        if(controller.getRZAxisValue() != 0.0F)
        {
            EntityPlayer player = Minecraft.getMinecraft().player;
            player.rotationPitch += 0.75F * (controller.getRZAxisValue() > 0.0F ? 1 : -1) * Math.abs(controller.getRZAxisValue());
        }

        while(Controllers.next())
        {
            if(Controllers.getEventSource().equals(controller))
            {
                if(Controllers.isEventButton() && Controllers.getEventButtonState())
                {
                    if(Controllers.getEventControlIndex() == 11)
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
                    else if(Controllers.getEventControlIndex() == 13)
                    {
                        Minecraft.getMinecraft().gameSettings.thirdPersonView++;
                        if(Minecraft.getMinecraft().gameSettings.thirdPersonView > 2)
                        {
                            Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onInputUpdate(InputUpdateEvent event)
    {
        if(!VehicleConfig.CLIENT.experimental.controllerSupport || controller == null)
            return;

        EntityPlayer player = Minecraft.getMinecraft().player;
        if(Minecraft.getMinecraft().currentScreen == null && !(player.getRidingEntity() instanceof EntityVehicle))
        {
            if(controller.getYAxisValue() != 0.0F)
            {
                int dir = controller.getYAxisValue() > 0.0F ? -1 : 1;
                event.getMovementInput().forwardKeyDown = dir > 0;
                event.getMovementInput().backKeyDown = dir < 0;
                event.getMovementInput().moveForward = dir * Math.abs(controller.getYAxisValue());
            }

            if(controller.getXAxisValue() != 0.0F)
            {
                int dir = controller.getXAxisValue() > 0.0F ? -1 : 1;
                event.getMovementInput().rightKeyDown = dir < 0;
                event.getMovementInput().leftKeyDown = dir > 0;
                event.getMovementInput().moveStrafe += dir;
            }
        }
    }
}
