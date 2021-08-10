package com.mrcrayfish.vehicle.client.handler;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.CameraHelper;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;

/**
 * Manages changing the point of view of the camera when mounting and dismount vehicles
 *
 * Author: MrCrayfish
 */
public class CameraHandler
{
    @Nullable
    private PointOfView originalPointOfView = null;

    private CameraHelper cameraHelper = new CameraHelper();

    @SubscribeEvent
    public void onEntityMount(EntityMountEvent event)
    {
        if(!Config.CLIENT.autoPerspective.get())
            return;

        if(!event.getWorldObj().isClientSide())
            return;

        if(!event.getEntityMounting().equals(Minecraft.getInstance().player))
            return;

        if(event.isMounting())
        {
            Entity entity = event.getEntityBeingMounted();
            if(!(entity instanceof VehicleEntity))
                return;

            this.originalPointOfView = Minecraft.getInstance().options.getCameraType();
            Minecraft.getInstance().options.setCameraType(PointOfView.THIRD_PERSON_BACK);
        }
        else if(this.originalPointOfView != null)
        {
            Minecraft.getInstance().options.setCameraType(this.originalPointOfView);
            this.originalPointOfView = null;
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if(!Config.CLIENT.autoPerspective.get())
            return;

        PlayerEntity player = Minecraft.getInstance().player;
        if(player == null)
            return;

        Entity entity = player.getVehicle();
        if(!(entity instanceof VehicleEntity))
            return;

        if(!Minecraft.getInstance().options.keyTogglePerspective.isDown())
            return;

        this.originalPointOfView = null;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if(event.phase != TickEvent.Phase.END || player == null)
            return;

        if(player.getVehicle() != null)
            return;

        this.originalPointOfView = null;
    }

    @SubscribeEvent
    public void onFovUpdate(FOVUpdateEvent event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if(player == null)
            return;

        Entity ridingEntity = player.getVehicle();
        if(ridingEntity instanceof VehicleEntity)
        {
            event.setNewfov(1.0F);
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onMountEntity(EntityMountEvent event)
    {
        if(!event.isMounting())
            return;

        if(!(event.getEntityBeingMounted() instanceof VehicleEntity))
            return;

        VehicleEntity vehicle = (VehicleEntity) event.getEntityBeingMounted();
        this.cameraHelper.load(vehicle);
    }

    @SubscribeEvent
    public void onPostClientTick(TickEvent.ClientTickEvent event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null || minecraft.player == null)
            return;

        ClientPlayerEntity player = minecraft.player;
        if(!(player.getVehicle() instanceof VehicleEntity))
            return;

        if(minecraft.options.getCameraType() != PointOfView.THIRD_PERSON_BACK)
            return;

        VehicleEntity vehicle = (VehicleEntity) player.getVehicle();
        this.cameraHelper.tick(vehicle);
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null || minecraft.player == null)
            return;

        ClientPlayerEntity player = minecraft.player;
        if(!(player.getVehicle() instanceof VehicleEntity))
            return;

        if(minecraft.options.getCameraType() != PointOfView.THIRD_PERSON_BACK)
            return;

        float partialTicks = (float) event.getRenderPartialTicks();
        VehicleEntity vehicle = (VehicleEntity) player.getVehicle();
        this.cameraHelper.setupVanillaCamera(event.getInfo(), vehicle, partialTicks);
        event.setPitch(this.cameraHelper.getPitch(partialTicks));
        event.setYaw(this.cameraHelper.getRotY(partialTicks));
        event.setRoll(this.cameraHelper.getRoll(partialTicks));
    }
}
