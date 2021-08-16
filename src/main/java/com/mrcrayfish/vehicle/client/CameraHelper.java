package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
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
    private float pitch;
    private float yaw;
    private float roll;
    private float prevPitch;
    private float prevYaw;
    private float prevRoll;

    public void load(VehicleEntity vehicle)
    {
        this.properties = vehicle.getProperties();
        this.pitch = vehicle.getViewPitch(1F);
        this.yaw = vehicle.getViewYaw(1F);
        this.roll = vehicle.getViewRoll(1F);
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
        this.prevRoll = this.roll;
    }

    public void tick(VehicleEntity vehicle, PointOfView pov)
    {
        float strength = this.getStrength(pov);
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
        this.prevRoll = this.roll;
        this.pitch = MathHelper.rotLerp(strength, this.pitch, vehicle.getViewPitch(1F));
        this.yaw = MathHelper.rotLerp(strength, this.yaw, vehicle.getViewYaw(1F));
        this.roll = MathHelper.rotLerp(strength, this.roll, vehicle.getViewRoll(1F));
    }

    private float getStrength(PointOfView pov)
    {
        return pov == PointOfView.THIRD_PERSON_BACK && this.properties.getCamera().getType() != CameraProperties.Type.LOCKED ? this.properties.getCamera().getStrength() : 1.0F;
    }

    public float getPitch(float partialTicks)
    {
        return MathHelper.rotLerp(partialTicks, this.prevPitch, this.pitch);
    }

    public float getYaw(float partialTicks)
    {
        return MathHelper.rotLerp(partialTicks, this.prevYaw, this.yaw);
    }

    public float getRoll(float partialTicks)
    {
        return MathHelper.rotLerp(partialTicks, this.prevRoll, this.roll);
    }

    public void setupVanillaCamera(ActiveRenderInfo info, PointOfView pov, VehicleEntity vehicle, ClientPlayerEntity player, float partialTicks)
    {
        switch(pov)
        {
            case FIRST_PERSON:
                this.setupFirstPersonCamera(info, vehicle, player, partialTicks);
                break;
            case THIRD_PERSON_BACK:
                this.setupThirdPersonCamera(info, vehicle, player, partialTicks);
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
                Quaternion quaternion = new Quaternion(0F, -this.getYaw(partialTicks), 0F, true);
                quaternion.mul(Vector3f.XP.rotationDegrees(this.getPitch(partialTicks)));
                quaternion.mul(Vector3f.ZP.rotationDegrees(this.getRoll(partialTicks)));
                Vector3f rotatedEyePos = new Vector3f(eyePos);
                rotatedEyePos.transform(quaternion);
                float cameraX = (float) (MathHelper.lerp(partialTicks, vehicle.xo, vehicle.getX()) + rotatedEyePos.x());
                float cameraY = (float) (MathHelper.lerp(partialTicks, vehicle.yo, vehicle.getY()) + rotatedEyePos.y());
                float cameraZ = (float) (MathHelper.lerp(partialTicks, vehicle.zo, vehicle.getZ()) + rotatedEyePos.z());
                SET_POSITION_METHOD.invoke(info, cameraX, cameraY, cameraZ);
            }
        }
        catch(InvocationTargetException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void setupThirdPersonCamera(ActiveRenderInfo info, VehicleEntity vehicle, ClientPlayerEntity player, float partialTicks)
    {
        try
        {
            CameraProperties camera = this.properties.getCamera();

            if(Config.CLIENT.followVehicleOrientation.get())
            {
                Vector3d rotation = camera.getRotation();
                float yaw = (float) (this.getYaw(partialTicks) + rotation.y) - vehicle.getPassengerYawOffset();
                float pitch = (float) (this.getPitch(partialTicks) + rotation.x) + vehicle.getPassengerPitchOffset();
                SET_ROTATION_METHOD.invoke(info, yaw, pitch);
            }

            if(Config.CLIENT.useVehicleAsFocusPoint.get())
            {
                Vector3d position = camera.getPosition();
                Quaternion quaternion = new Quaternion(0F, -this.getYaw(partialTicks), 0F, true);
                quaternion.mul(Vector3f.XP.rotationDegrees(this.getPitch(partialTicks)));
                quaternion.mul(Vector3f.ZP.rotationDegrees(this.getRoll(partialTicks)));
                Vector3f rotatedPosition = new Vector3f(position);
                rotatedPosition.transform(quaternion);
                float cameraX = (float) (MathHelper.lerp(partialTicks, vehicle.xo, vehicle.getX()) + rotatedPosition.x());
                float cameraY = (float) (MathHelper.lerp(partialTicks, vehicle.yo, vehicle.getY()) + rotatedPosition.y());
                float cameraZ = (float) (MathHelper.lerp(partialTicks, vehicle.zo, vehicle.getZ()) + rotatedPosition.z());
                SET_POSITION_METHOD.invoke(info, cameraX, cameraY, cameraZ);
            }
            else
            {
                int index = vehicle.getSeatTracker().getSeatIndex(player.getUUID());
                if(index != -1)
                {
                    Seat seat = this.properties.getSeats().get(index);
                    Vector3d eyePos = seat.getPosition().add(0, this.properties.getAxleOffset() + this.properties.getWheelOffset(), 0).scale(this.properties.getBodyPosition().getScale()).multiply(-1, 1, 1).scale(0.0625);
                    eyePos = eyePos.add(0, player.getMyRidingOffset() + player.getEyeHeight(), 0);
                    Quaternion quaternion = new Quaternion(0F, -this.getYaw(partialTicks), 0F, true);
                    quaternion.mul(Vector3f.XP.rotationDegrees(this.getPitch(partialTicks)));
                    quaternion.mul(Vector3f.ZP.rotationDegrees(this.getRoll(partialTicks)));
                    Vector3f rotatedEyePos = new Vector3f(eyePos);
                    rotatedEyePos.transform(quaternion);
                    float cameraX = (float) (MathHelper.lerp(partialTicks, vehicle.xo, vehicle.getX()) + rotatedEyePos.x());
                    float cameraY = (float) (MathHelper.lerp(partialTicks, vehicle.yo, vehicle.getY()) + rotatedEyePos.y());
                    float cameraZ = (float) (MathHelper.lerp(partialTicks, vehicle.zo, vehicle.getZ()) + rotatedEyePos.z());
                    SET_POSITION_METHOD.invoke(info, cameraX, cameraY, cameraZ);
                }
            }

            MOVE_METHOD.invoke(info, -(double) GET_MAX_MOVE_METHOD.invoke(info, camera.getDistance()), 0, 0);
        }
        catch(InvocationTargetException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}
