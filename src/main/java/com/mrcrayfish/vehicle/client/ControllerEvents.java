package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.controllable.Buttons;
import com.mrcrayfish.controllable.client.Action;
import com.mrcrayfish.controllable.event.AvailableActionsEvent;
import com.mrcrayfish.controllable.event.ControllerEvent;
import com.mrcrayfish.controllable.event.RenderPlayerPreviewEvent;
import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.entity.*;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageHitchTrailer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
@SideOnly(Side.CLIENT)
public class ControllerEvents
{
    @SubscribeEvent
    public void onButtonInput(ControllerEvent.ButtonInput event)
    {
        if(event.getState())
        {
            EntityPlayer player = Minecraft.getMinecraft().player;
            switch(event.getButton())
            {
                case Buttons.A:
                    if(player.getRidingEntity() instanceof EntityPoweredVehicle)
                    {
                        event.setCanceled(true);
                    }
                    break;
                case Buttons.X:
                    if(Minecraft.getMinecraft().currentScreen == null && player.getRidingEntity() instanceof EntityVehicle)
                    {
                        EntityVehicle vehicle = (EntityVehicle) player.getRidingEntity();
                        if(vehicle.canTowTrailer())
                        {
                            PacketHandler.INSTANCE.sendToServer(new MessageHitchTrailer(vehicle.getTrailer() == null));
                        }
                        event.setCanceled(true);
                    }
                    break;
                case Buttons.TOUCH_PAD:
                    if(player.getRidingEntity() instanceof EntityVehicle)
                    {
                        player.rotationYaw = player.getRidingEntity().rotationYaw;
                        player.rotationPitch = 15F;
                        event.setCanceled(true);
                    }
                    break;
                case Buttons.RIGHT_BUMPER:
                case Buttons.LEFT_BUMPER:
                    if(player.getRidingEntity() instanceof EntityVehicle)
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
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player.getRidingEntity() instanceof EntityVehicle)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onAvailableActions(AvailableActionsEvent event)
    {
        Map<Integer, Action> availableActions = event.getActions();
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if(player.getRidingEntity() instanceof EntityVehicle && mc.currentScreen == null)
        {
            availableActions.remove(Buttons.RIGHT_BUMPER);
            availableActions.remove(Buttons.LEFT_BUMPER);
            availableActions.remove(Buttons.RIGHT_TRIGGER);
            availableActions.remove(Buttons.LEFT_TRIGGER);
            availableActions.remove(Buttons.LEFT_THUMB_STICK);
            availableActions.remove(Buttons.X);
            availableActions.remove(Buttons.DPAD_DOWN);

            availableActions.put(Buttons.LEFT_THUMB_STICK, new Action("Exit Vehicle", Action.Side.RIGHT));
            availableActions.put(Buttons.A, new Action("Accelerate", Action.Side.LEFT));

            EntityVehicle vehicle = (EntityVehicle) player.getRidingEntity();

            if(vehicle instanceof EntityPoweredVehicle)
            {
                if(((EntityPoweredVehicle) vehicle).getSpeed() > 0.05F)
                {
                    availableActions.put(Buttons.B, new Action("Brake", Action.Side.LEFT));
                }
                else
                {
                    availableActions.put(Buttons.B, new Action("Reverse", Action.Side.LEFT));
                }
            }

            if(vehicle instanceof EntityLandVehicle)
            {
                availableActions.put(Buttons.RIGHT_BUMPER, new Action("Drift", Action.Side.RIGHT));
            }
            else if(vehicle instanceof EntityPlane)
            {
                availableActions.put(Buttons.RIGHT_BUMPER, new Action("Pull Up", Action.Side.RIGHT));
                availableActions.put(Buttons.LEFT_BUMPER, new Action("Pull Down", Action.Side.RIGHT));
            }
            else if(vehicle instanceof EntityHelicopter)
            {
                availableActions.put(Buttons.RIGHT_BUMPER, new Action("Increase Elevation", Action.Side.RIGHT));
                availableActions.put(Buttons.LEFT_BUMPER, new Action("Decreased Elevation", Action.Side.RIGHT));
            }
        }
        else
        {
            if(!player.isRiding())
            {
                if(mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY)
                {
                    Entity entity = mc.objectMouseOver.entityHit;
                    if(entity instanceof EntityVehicle)
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
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player.getRidingEntity() instanceof EntityVehicle)
        {
            event.setCanceled(true);
        }
    }

    private EntityPoweredVehicle getClosestVehicle()
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        List<EntityPoweredVehicle> vehicles = Minecraft.getMinecraft().world.getEntitiesWithinAABB(EntityPoweredVehicle.class, player.getEntityBoundingBox().grow(1.0, 0.0, 1.0));
        EntityPoweredVehicle closestVehicle = null;
        float closestDistance = -1.0F;
        for(EntityPoweredVehicle vehicle : vehicles)
        {
            float distance = vehicle.getDistance(player);
            if(closestDistance == -1.0F || distance < closestDistance)
            {
                closestDistance = distance;
                closestVehicle = vehicle;
            }
        }
        return closestVehicle;
    }
}
