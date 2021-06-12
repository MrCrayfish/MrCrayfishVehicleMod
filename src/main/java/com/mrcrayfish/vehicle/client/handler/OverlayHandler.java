package com.mrcrayfish.vehicle.client.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Author: MrCrayfish
 */
public class OverlayHandler
{
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if(!Config.CLIENT.enabledSpeedometer.get())
            return;

        if(event.phase != TickEvent.Phase.END)
            return;

        Minecraft mc = Minecraft.getInstance();
        if(!mc.isWindowActive() || mc.options.hideGui)
            return;

        PlayerEntity player = mc.player;
        if(player == null)
            return;

        Entity entity = player.getVehicle();
        if(!(entity instanceof PoweredVehicleEntity))
            return;

        MatrixStack matrixStack = new MatrixStack();
        PoweredVehicleEntity vehicle = (PoweredVehicleEntity) entity;
        String speed = new DecimalFormat("0.0").format(vehicle.getKilometersPreHour());
        mc.font.drawShadow(matrixStack, TextFormatting.BOLD + "BPS: " + TextFormatting.YELLOW + speed, 10, 10, Color.WHITE.getRGB());

        if(vehicle.requiresFuel())
        {
            DecimalFormat format = new DecimalFormat("0.0");
            String fuel = format.format(vehicle.getCurrentFuel()) + "/" + format.format(vehicle.getFuelCapacity());
            mc.font.drawShadow(matrixStack, TextFormatting.BOLD + "Fuel: " + TextFormatting.YELLOW + fuel, 10, 25, Color.WHITE.getRGB());
        }
    }
}
