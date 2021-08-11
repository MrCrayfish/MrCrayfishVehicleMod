package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A helper class that manages the camera rotations for vehicles
 *
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class CameraHelper
{
    private static final Method SET_ROTATION_METHOD = ObfuscationReflectionHelper.findMethod(ActiveRenderInfo.class, "func_216776_a", float.class, float.class);
    private static final Method SET_POSITION_METHOD = ObfuscationReflectionHelper.findMethod(ActiveRenderInfo.class, "func_216775_b", double.class, double.class, double.class);
    private static final Method MOVE_METHOD = ObfuscationReflectionHelper.findMethod(ActiveRenderInfo.class, "func_216782_a", double.class, double.class, double.class);
    private static final Method GET_MAX_MOVE_METHOD = ObfuscationReflectionHelper.findMethod(ActiveRenderInfo.class, "func_216779_a", double.class);

    private VehicleProperties properties;
    private float rotX;
    private float rotY;
    private float rotZ;
    private float prevRotX;
    private float prevRotY;
    private float prevRotZ;

    public void load(VehicleEntity vehicle)
    {
        this.properties = vehicle.getProperties();
        this.rotX = vehicle.getBodyRotationX();
        this.rotY = vehicle.getBodyRotationY();
        this.rotZ = vehicle.getBodyRotationZ();
        this.prevRotX = this.rotX;
        this.prevRotY = this.rotY;
        this.prevRotZ = this.rotZ;
    }

    public void tick(VehicleEntity vehicle, PointOfView pov)
    {
        float strength = this.getStrength(pov);
        this.prevRotX = this.rotX;
        this.prevRotY = this.rotY;
        this.prevRotZ = this.rotZ;
        this.rotX = MathHelper.rotLerp(strength, this.rotX, vehicle.getBodyRotationX());
        this.rotY = MathHelper.rotLerp(strength, this.rotY, vehicle.getBodyRotationY());
        this.rotZ = MathHelper.rotLerp(strength, this.rotZ, vehicle.getBodyRotationZ());
    }

    private float getStrength(PointOfView pov)
    {
        return pov == PointOfView.THIRD_PERSON_BACK ? this.properties.getCamera().getStrength() : 1.0F;
    }

    public float getRotX(float partialTicks)
    {
        return MathHelper.rotLerp(partialTicks, this.prevRotX, this.rotX);
    }

    public float getRotY(float partialTicks)
    {
        return MathHelper.rotLerp(partialTicks, this.prevRotY, this.rotY);
    }

    public float getRotZ(float partialTicks)
    {
        return MathHelper.rotLerp(partialTicks, this.prevRotZ, this.rotZ);
    }

    public float getPitch(float partialTicks)
    {
        return -(float) new Vector3d(this.getRotX(partialTicks), 0, this.getRotZ(partialTicks)).yRot((float) Math.toRadians(-(this.getRotY(partialTicks) + 90))).x;
    }

    public float getRoll(float partialTicks)
    {
        return (float) new Vector3d(this.getRotX(partialTicks), 0, this.getRotZ(partialTicks)).yRot((float) Math.toRadians(-(this.getRotY(partialTicks) + 90))).z;
    }

    public void setupVanillaCamera(ActiveRenderInfo info, PointOfView pov, VehicleEntity vehicle, ClientPlayerEntity player, float partialTicks)
    {
        switch(pov)
        {
            case FIRST_PERSON:
                this.setupFirstPersonCamera(info, vehicle, player, partialTicks);
                break;
            case THIRD_PERSON_BACK:
                this.setupThirdPersonCamera(info, vehicle, partialTicks);
                break;
            case THIRD_PERSON_FRONT:
                break;
        }
    }

    private void setupFirstPersonCamera(ActiveRenderInfo info, VehicleEntity vehicle, ClientPlayerEntity player, float partialTicks)
    {
        try
        {
            int index = vehicle.getSeatTracker().getSeatIndex(player.getUUID());
            if(index != -1)
            {
                Seat seat = this.properties.getSeats().get(index);
                Vector3d eyePos = seat.getPosition().add(0, this.properties.getAxleOffset() + this.properties.getWheelOffset(), 0).scale(this.properties.getBodyPosition().getScale()).multiply(-1, 1, 1).scale(0.0625);
                eyePos = eyePos.add(0, player.getMyRidingOffset() + player.getEyeHeight(), 0);
                eyePos = eyePos.yRot(-(this.getRotY(partialTicks) + 90) * 0.017453292F);
                eyePos = eyePos.xRot(this.getRotX(partialTicks) * 0.017453292F);
                eyePos = eyePos.zRot(-this.getRotZ(partialTicks) * 0.017453292F);
                float cameraX = (float) (MathHelper.lerp(partialTicks, vehicle.xo, vehicle.getX()) + eyePos.z);
                float cameraY = (float) (MathHelper.lerp(partialTicks, vehicle.yo, vehicle.getY()) + eyePos.y);
                float cameraZ = (float) (MathHelper.lerp(partialTicks, vehicle.zo, vehicle.getZ()) - eyePos.x);
                SET_POSITION_METHOD.invoke(info, cameraX, cameraY, cameraZ);
            }
        }
        catch(InvocationTargetException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void setupThirdPersonCamera(ActiveRenderInfo info, VehicleEntity vehicle, float partialTicks)
    {
        try
        {
            CameraProperties camera = this.properties.getCamera();

            Vector3d rotation = camera.getRotation();
            float yaw = (float) (this.getRotY(partialTicks) + rotation.y) - vehicle.getPassengerYawOffset();
            float pitch = (float) (this.getPitch(partialTicks) + rotation.x) + vehicle.getPassengerPitchOffset();
            SET_ROTATION_METHOD.invoke(info, yaw, pitch);

            Vector3d position = camera.getPosition().yRot((float) Math.toRadians(-(this.getRotY(partialTicks) + 90)));
            float cameraX = (float) (MathHelper.lerp(partialTicks, vehicle.xo, vehicle.getX()) + position.z);
            float cameraY = (float) (MathHelper.lerp(partialTicks, vehicle.yo, vehicle.getY()) + position.y);
            float cameraZ = (float) (MathHelper.lerp(partialTicks, vehicle.zo, vehicle.getZ()) + position.x);
            SET_POSITION_METHOD.invoke(info, cameraX, cameraY, cameraZ);

            MOVE_METHOD.invoke(info, -(double) GET_MAX_MOVE_METHOD.invoke(info, camera.getDistance()), 0, 0);
        }
        catch(InvocationTargetException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}
