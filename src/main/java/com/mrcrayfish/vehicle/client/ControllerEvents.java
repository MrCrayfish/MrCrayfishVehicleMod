package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageDismount;
import com.mrcrayfish.vehicle.network.message.MessageHitchTrailer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import java.util.List;

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
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player == null)
            return;

        if(!VehicleConfig.CLIENT.experimental.controllerSupport || controller == null)
            return;

        if(event.phase == TickEvent.Phase.END)
            return;

        /* Handles rotating the yaw of player */
        if(controller.getZAxisValue() != 0.0F)
        {
            player.rotationYaw += 2.0F * (controller.getZAxisValue() > 0.0F ? 1 : -1) * Math.abs(controller.getZAxisValue());
        }

        /* Handles rotating the pitch of player */
        if(controller.getRZAxisValue() != 0.0F)
        {
            player.rotationPitch += 0.75F * (controller.getRZAxisValue() > 0.0F ? 1 : -1) * Math.abs(controller.getRZAxisValue());
        }

        while(Controllers.next())
        {
            if(Controllers.getEventSource().equals(controller))
            {
                if(Controllers.isEventButton() && Controllers.getEventButtonState())
                {
                    if(Controllers.getEventControlIndex() == 0) // Square
                    {
                        if(Minecraft.getMinecraft().currentScreen == null && player.getRidingEntity() instanceof EntityVehicle)
                        {
                            EntityVehicle vehicle = (EntityVehicle) player.getRidingEntity();
                            if(vehicle.canTowTrailer())
                            {
                                PacketHandler.INSTANCE.sendToServer(new MessageHitchTrailer(vehicle.getTrailer() == null));
                            }
                        }
                    }
                    else if(Controllers.getEventControlIndex() == 13) // Touch Pad
                    {
                        Minecraft.getMinecraft().gameSettings.thirdPersonView++;
                        if(Minecraft.getMinecraft().gameSettings.thirdPersonView > 2)
                        {
                            Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
                        }
                    }
                    else if(Controllers.getEventControlIndex() == 3) // Triangle
                    {
                        if(!player.isRiding())
                        {
                            List<EntityPoweredVehicle> vehicles = Minecraft.getMinecraft().world.getEntitiesWithinAABB(EntityPoweredVehicle.class, player.getEntityBoundingBox().grow(1.0, 0.0, 1.0));
                            Entity closestVehicle = null;
                            float closestDistance = -1.0F;
                            for(Entity vehicle : vehicles)
                            {
                                float distance = vehicle.getDistance(player);
                                if(closestDistance == -1.0F || distance < closestDistance)
                                {
                                    closestDistance = distance;
                                    closestVehicle = vehicle;
                                }
                            }
                            if(closestVehicle != null)
                            {
                                Minecraft.getMinecraft().playerController.interactWithEntity(Minecraft.getMinecraft().player, closestVehicle, EnumHand.MAIN_HAND);
                            }
                        }
                        else
                        {
                            PacketHandler.INSTANCE.sendToServer(new MessageDismount());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onInputUpdate(InputUpdateEvent event)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player == null)
            return;

        if(!VehicleConfig.CLIENT.experimental.controllerSupport || controller == null)
            return;

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
                event.getMovementInput().moveStrafe = dir * Math.abs(controller.getXAxisValue());
            }
        }
    }
}
