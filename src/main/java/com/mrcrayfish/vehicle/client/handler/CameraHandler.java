package com.mrcrayfish.vehicle.client.handler;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.CameraHelper;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Manages changing the point of view of the camera when mounting and dismount vehicles
 *
 * Author: MrCrayfish
 */
public class CameraHandler
{
    private static CameraHandler instance;

    @Nullable
    private PointOfView originalPointOfView = null;
    private final CameraHelper cameraHelper = new CameraHelper();

    private CameraHandler() {}

    public static CameraHandler instance()
    {
        if(instance == null)
        {
            instance = new CameraHandler();
        }
        return instance;
    }

    @SubscribeEvent
    public void onEntityMount(EntityMountEvent event)
    {
        if(!Config.CLIENT.autoPerspective.get())
            return;

        if(!event.getWorldObj().isClientSide())
            return;

        if(!event.getEntityMounting().equals(Minecraft.getInstance().player))
            return;

        Entity entity = event.getEntityBeingMounted();
        if(!(entity instanceof VehicleEntity))
            return;

        if(event.isMounting())
        {
            this.originalPointOfView = Minecraft.getInstance().options.getCameraType();
            Minecraft.getInstance().options.setCameraType(PointOfView.THIRD_PERSON_BACK);
        }
        else
        {
            if(Config.CLIENT.forceFirstPersonOnExit.get())
            {
                Minecraft.getInstance().options.setCameraType(PointOfView.FIRST_PERSON);
            }
            else if(this.originalPointOfView != null)
            {
                Minecraft.getInstance().options.setCameraType(this.originalPointOfView);
            }
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
        if(!Config.CLIENT.immersiveCamera.get())
            return;

        if(!event.isMounting())
            return;

        if(!(event.getEntityBeingMounted() instanceof VehicleEntity))
            return;

        Entity entity = event.getEntityMounting();
        if(!(entity instanceof PlayerEntity) || !((PlayerEntity) entity).isLocalPlayer())
            return;

        this.cameraHelper.load((VehicleEntity) event.getEntityBeingMounted());
    }

    @SubscribeEvent
    public void onPostClientTick(TickEvent.ClientTickEvent event)
    {
        if(!Config.CLIENT.immersiveCamera.get())
            return;

        if(event.phase != TickEvent.Phase.END)
            return;

        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null || minecraft.player == null)
            return;

        if(minecraft.isPaused())
            return;

        ClientPlayerEntity player = minecraft.player;
        if(!(player.getVehicle() instanceof VehicleEntity))
            return;

        VehicleEntity vehicle = (VehicleEntity) player.getVehicle();
        this.cameraHelper.tick(vehicle, minecraft.options.getCameraType());
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event)
    {
        this.setupVanillaCamera(event.getInfo(), (float) event.getRenderPartialTicks());
    }

    public void setupVanillaCamera(ActiveRenderInfo info, float partialTicks)
    {
        if(!Config.CLIENT.immersiveCamera.get())
            return;

        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null || minecraft.player == null)
            return;

        ClientPlayerEntity player = minecraft.player;
        if(!(player.getVehicle() instanceof VehicleEntity))
            return;

        PointOfView pointOfView = minecraft.options.getCameraType();
        VehicleEntity vehicle = (VehicleEntity) player.getVehicle();
        this.cameraHelper.setupVanillaCamera(info, pointOfView, vehicle, player, partialTicks);
    }

    /*
     * Called via transformer. Do not delete!
     */
    @SuppressWarnings("unused")
    public static void setupVehicleCamera(MatrixStack matrixStack)
    {
        if(!Config.CLIENT.immersiveCamera.get())
            return;

        ActiveRenderInfo info = Minecraft.getInstance().gameRenderer.getMainCamera();
        Entity entity = info.getEntity();
        if(!(entity instanceof PlayerEntity) || !(entity.getVehicle() instanceof VehicleEntity))
            return;

        // Undo the rotations created by vanilla
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-(info.getYRot() + 180F)));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-info.getXRot()));

        // Applies quaternion to rotate camera rather than euler angles
        Quaternion rotation = info.rotation();
        Quaternion quaternion = new Quaternion(rotation);
        quaternion.mul(Vector3f.YP.rotationDegrees(180F));
        quaternion.conj();
        matrixStack.mulPose(quaternion);
    }

    /*
     * Called via transformer. Do not delete!
     */
    public static void setupShaderCamera(ActiveRenderInfo info, float partialTicks)
    {
        CameraHandler.instance().setupVanillaCamera(info, partialTicks);
    }

    @SubscribeEvent
    public void onMouseScroll(InputEvent.MouseScrollEvent event)
    {
        if(!Config.CLIENT.debugCamera.get())
            return;
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null)
            return;
        long windowId = Minecraft.getInstance().getWindow().getWindow();
        for(Map.Entry<Integer, BiConsumer<Float, CameraHelper>> entry : DEBUG_CAMERA_KEY_MAP.entrySet())
        {
            if(GLFW.glfwGetKey(windowId, entry.getKey()) == GLFW.GLFW_PRESS)
            {
                entry.getValue().accept((float) event.getScrollDelta() * 0.1F, this.cameraHelper);
                event.setCanceled(true);
                break;
            }
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event)
    {
        if(!Config.CLIENT.debugCamera.get())
            return;
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null)
            return;
        if(event.getKey() == GLFW.GLFW_KEY_KP_7 && event.getAction() == GLFW.GLFW_PRESS)
        {
            this.cameraHelper.enableStrength = !this.cameraHelper.enableStrength;
        }
        else if(event.getKey() == GLFW.GLFW_KEY_KP_8 && event.getAction() == GLFW.GLFW_PRESS)
        {
            this.cameraHelper.offsetX = 0F;
            this.cameraHelper.offsetY = 0F;
            this.cameraHelper.offsetZ = 0F;
            this.cameraHelper.offsetPitch = 0F;
            this.cameraHelper.offsetYaw = 0F;
            this.cameraHelper.offsetRoll = 0F;
        }
    }

    private static final Map<Integer, BiConsumer<Float, CameraHelper>> DEBUG_CAMERA_KEY_MAP = Util.make(() -> {
        Map<Integer, BiConsumer<Float, CameraHelper>> map = new HashMap<>();
        map.put(GLFW.GLFW_KEY_KP_1, (value, handler) -> handler.offsetX += value);
        map.put(GLFW.GLFW_KEY_KP_2, (value, handler) -> handler.offsetY += value);
        map.put(GLFW.GLFW_KEY_KP_3, (value, handler) -> handler.offsetZ += value);
        map.put(GLFW.GLFW_KEY_KP_4, (value, handler) -> handler.offsetPitch += value * 10F);
        map.put(GLFW.GLFW_KEY_KP_5, (value, handler) -> handler.offsetYaw += value * 10F);
        map.put(GLFW.GLFW_KEY_KP_6, (value, handler) -> handler.offsetRoll += value * 10F);
        return ImmutableMap.copyOf(map);
    });
}
