package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.Action;
import com.mrcrayfish.controllable.client.Buttons;
import com.mrcrayfish.controllable.event.AvailableActionsEvent;
import com.mrcrayfish.controllable.event.ControllerEvent;
import com.mrcrayfish.controllable.event.RenderPlayerPreviewEvent;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.entity.*;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageHitchTrailer;
import com.mrcrayfish.vehicle.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class ControllerEvents
{
    @SubscribeEvent
    public void onButtonInput(ControllerEvent.ButtonInput event)
    {
        if(event.getState())
        {
            PlayerEntity player = Minecraft.getInstance().player;
            if(player == null)
                return;

            switch(event.getButton())
            {
                case Buttons.A:
                    if(player.getRidingEntity() instanceof PoweredVehicleEntity)
                    {
                        event.setCanceled(true);
                    }
                    break;
                case Buttons.X:
                    if(Minecraft.getInstance().currentScreen == null && player.getRidingEntity() instanceof VehicleEntity)
                    {
                        VehicleEntity vehicle = (VehicleEntity) player.getRidingEntity();
                        if(vehicle.canTowTrailer())
                        {
                            PacketHandler.instance.sendToServer(new MessageHitchTrailer(vehicle.getTrailer() == null));
                        }
                        event.setCanceled(true);
                    }
                    break;
                case Buttons.SELECT:
                    if(player.getRidingEntity() instanceof VehicleEntity)
                    {
                        player.rotationYaw = player.getRidingEntity().rotationYaw;
                        player.rotationPitch = 15F;
                        event.setCanceled(true);
                    }
                    break;
                case Buttons.RIGHT_BUMPER:
                case Buttons.LEFT_BUMPER:
                    if(player.getRidingEntity() instanceof VehicleEntity)
                    {
                        event.setCanceled(true);
                    }
                    break;
                case Buttons.RIGHT_TRIGGER:
                case Buttons.LEFT_TRIGGER:
                    if(Config.CLIENT.useTriggers.get() && player.getRidingEntity() instanceof VehicleEntity)
                    {
                        event.setCanceled(true);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @SubscribeEvent
    public void onControllerMove(ControllerEvent.Move event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if(player.getRidingEntity() instanceof VehicleEntity)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onAvailableActions(AvailableActionsEvent event)
    {
        Map<Integer, Action> availableActions = event.getActions();
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if(player.getRidingEntity() instanceof VehicleEntity && mc.currentScreen == null)
        {
            availableActions.remove(Buttons.RIGHT_BUMPER);
            availableActions.remove(Buttons.LEFT_BUMPER);
            availableActions.remove(Buttons.RIGHT_TRIGGER);
            availableActions.remove(Buttons.LEFT_TRIGGER);
            availableActions.remove(Buttons.LEFT_THUMB_STICK);
            availableActions.remove(Buttons.X);
            availableActions.remove(Buttons.DPAD_DOWN);

            availableActions.put(Buttons.LEFT_THUMB_STICK, new Action("Exit Vehicle", Action.Side.LEFT));

            if(Config.CLIENT.useTriggers.get())
            {
                availableActions.put(Buttons.RIGHT_TRIGGER, new Action("Accelerate", Action.Side.RIGHT));
            }
            else
            {
                availableActions.put(Buttons.A, new Action("Accelerate", Action.Side.RIGHT));
            }

            VehicleEntity vehicle = (VehicleEntity) player.getRidingEntity();

            if(vehicle instanceof PoweredVehicleEntity)
            {
                int button = Config.CLIENT.useTriggers.get() ? Buttons.LEFT_TRIGGER : Buttons.B;
                if(((PoweredVehicleEntity) vehicle).getSpeed() > 0.05F)
                {
                    availableActions.put(button, new Action("Brake", Action.Side.RIGHT));
                }
                else
                {
                    availableActions.put(button, new Action("Reverse", Action.Side.RIGHT));
                }
            }

            if(vehicle instanceof LandVehicleEntity)
            {
                availableActions.put(Buttons.RIGHT_BUMPER, new Action("Drift", Action.Side.RIGHT));
            }
            else if(vehicle instanceof PlaneEntity)
            {
                availableActions.put(Buttons.RIGHT_BUMPER, new Action("Pull Up", Action.Side.RIGHT));
                availableActions.put(Buttons.LEFT_BUMPER, new Action("Pull Down", Action.Side.RIGHT));
            }
            else if(vehicle instanceof HelicopterEntity)
            {
                availableActions.put(Buttons.RIGHT_BUMPER, new Action("Increase Elevation", Action.Side.RIGHT));
                availableActions.put(Buttons.LEFT_BUMPER, new Action("Decreased Elevation", Action.Side.RIGHT));
            }
        }
        else
        {
            if(player.getRidingEntity() == null)
            {
                if(mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY)
                {
                    Entity entity = ((EntityRayTraceResult) mc.objectMouseOver).getEntity();
                    if(entity instanceof VehicleEntity)
                    {
                        availableActions.put(Buttons.LEFT_TRIGGER, new Action("Ride Vehicle", Action.Side.RIGHT));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPreview(RenderPlayerPreviewEvent event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if(player.getRidingEntity() instanceof VehicleEntity)
        {
            event.setCanceled(true);
        }
    }

    public static boolean isRightClicking()
    {
        boolean isRightClicking = GLFW.glfwGetMouseButton(Minecraft.getInstance().mainWindow.getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        isRightClicking |= ClientProxy.controllableLoaded && Controllable.getController() != null && Controllable.getController().getLTriggerValue() != 0.0F;
        return isRightClicking;
    }
}
