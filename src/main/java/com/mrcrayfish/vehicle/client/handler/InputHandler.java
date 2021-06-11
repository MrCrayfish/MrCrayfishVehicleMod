package com.mrcrayfish.vehicle.client.handler;

import com.mrcrayfish.vehicle.client.KeyBinds;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageCycleSeats;
import com.mrcrayfish.vehicle.network.message.MessageHitchTrailer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Author: MrCrayfish
 */
public class InputHandler
{
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.player == null || minecraft.currentScreen != null)
            return;

        if(event.getAction() != GLFW.GLFW_PRESS)
            return;

        if(KeyBinds.KEY_CYCLE_SEATS.isPressed())
        {
            if(minecraft.player.getRidingEntity() instanceof VehicleEntity)
            {
                PacketHandler.instance.sendToServer(new MessageCycleSeats());
            }
        }

        if(KeyBinds.KEY_HITCH_TRAILER.isPressed())
        {
            if(minecraft.player.getRidingEntity() instanceof VehicleEntity)
            {
                VehicleEntity vehicle = (VehicleEntity) minecraft.player.getRidingEntity();
                if(vehicle.canTowTrailer())
                {
                    PacketHandler.instance.sendToServer(new MessageHitchTrailer(vehicle.getTrailer() == null));
                }
            }
        }
    }
}
