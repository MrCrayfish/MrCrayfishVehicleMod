package com.mrcrayfish.vehicle.client.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLLoader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class OverlayHandler
{
    private List<ITextComponent> stats = new ArrayList<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END)
            return;

        this.stats.clear();

        if(!Config.CLIENT.enabledSpeedometer.get())
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

        PoweredVehicleEntity vehicle = (PoweredVehicleEntity) entity;
        DecimalFormat format = new DecimalFormat("0.00");
        this.addStat("BPS", format.format(vehicle.getSpeed()));

        if(vehicle.requiresEnergy())
        {
            String fuel = format.format(vehicle.getCurrentEnergy()) + "/" + format.format(vehicle.getEnergyCapacity());
            this.addStat("Fuel", fuel);
        }

        if(!FMLLoader.isProduction())
        {
            if(vehicle instanceof LandVehicleEntity)
            {
                LandVehicleEntity landVehicle = (LandVehicleEntity) vehicle;
                String traction = format.format(landVehicle.getTraction());
                this.addStat("Traction", traction);

                Vector3d forward = Vector3d.directionFromRotation(landVehicle.getRotationVector());
                float side = (float) landVehicle.getVelocity().normalize().cross(forward.normalize()).length();
                String sideString = format.format(side);
                this.addStat("Side", sideString);
            }
        }
    }

    private void addStat(String label, String value)
    {
        this.stats.add(new StringTextComponent(label + ": ").withStyle(TextFormatting.BOLD).withStyle(TextFormatting.RESET).append(new StringTextComponent(value).withStyle(TextFormatting.YELLOW)));
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END)
            return;

        MatrixStack stack = new MatrixStack();
        Minecraft mc = Minecraft.getInstance();
        for(int i = 0; i < this.stats.size(); i++)
        {
            mc.font.drawShadow(stack, this.stats.get(i), 10, 10 + 15 * i, 0xFFFFFF);
        }
    }
}
