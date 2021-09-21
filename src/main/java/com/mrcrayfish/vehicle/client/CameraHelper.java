package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
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

import java.lang.reflect.Field;
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
    private static final Field LEFT_FIELD = ObfuscationReflectionHelper.findField(ActiveRenderInfo.class, "field_216796_h");

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
                this.setupThirdPersonCamera(info, vehicle, player, partialTicks, false);
                break;
            case THIRD_PERSON_FRONT:
                this.setupThirdPersonCamera(info, vehicle, player, partialTicks, true);
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
                if(Config.CLIENT.followVehicleOrientation.get())
                {
                    this.setVehicleRotation(info, vehicle, player, partialTicks);
                }

                Seat seat = this.properties.getSeats().get(index);
                Vector3d eyePos = seat.getPosition().add(0, this.properties.getAxleOffset() + this.properties.getWheelOffset(), 0).scale(this.properties.getBodyTransform().getScale()).multiply(-1, 1, 1).add(this.properties.getBodyTransform().getTranslate()).scale(0.0625);
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

    private void setupThirdPersonCamera(ActiveRenderInfo info, VehicleEntity vehicle, ClientPlayerEntity player, float partialTicks, boolean front)
    {
        try
        {
            if(Config.CLIENT.followVehicleOrientation.get())
            {
                this.setVehicleRotation(info, vehicle, player, partialTicks);
            }

            if(Config.CLIENT.useVehicleAsFocusPoint.get() && !front)
            {
                Vector3d position = this.properties.getCamera().getPosition();
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
                    Vector3d eyePos = seat.getPosition().add(0, this.properties.getAxleOffset() + this.properties.getWheelOffset(), 0).scale(this.properties.getBodyTransform().getScale()).multiply(-1, 1, 1).add(this.properties.getBodyTransform().getTranslate()).scale(0.0625);
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

            double distance = front ? 4.0 : this.properties.getCamera().getDistance();
            MOVE_METHOD.invoke(info, -(double) GET_MAX_MOVE_METHOD.invoke(info, distance), 0, 0);
        }
        catch(InvocationTargetException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void setVehicleRotation(ActiveRenderInfo info, VehicleEntity vehicle, ClientPlayerEntity player, float partialTicks)
    {
        try
        {
            Quaternion rotation = info.rotation();
            rotation.set(0.0F, 0.0F, 0.0F, 1.0F);

            // Applies the vehicle's body rotations to the camera
            if(Config.CLIENT.shouldFollowYaw.get())
            {
                rotation.mul(Vector3f.YP.rotationDegrees(-this.getYaw(partialTicks)));
            }
            if(Config.CLIENT.shouldFollowPitch.get())
            {
                rotation.mul(Vector3f.XP.rotationDegrees(this.getPitch(partialTicks)));
            }
            if(Config.CLIENT.shouldFollowRoll.get())
            {
                rotation.mul(Vector3f.ZP.rotationDegrees(this.getRoll(partialTicks)));
            }

            // Applies the player's pitch and yaw offset
            Quaternion quaternion = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);

            if(VehicleHelper.isThirdPersonFront())
            {
                quaternion.mul(Vector3f.YP.rotationDegrees(180F));
            }

            if(vehicle.canApplyYawOffset(player) && Config.CLIENT.shouldFollowYaw.get())
            {
                quaternion.mul(Vector3f.YP.rotationDegrees(vehicle.getPassengerYawOffset()));
            }
            else
            {
                quaternion.mul(Vector3f.YP.rotationDegrees(-player.getViewYRot(partialTicks)));
                if(Config.CLIENT.shouldFollowYaw.get())
                {
                    quaternion.mul(Vector3f.YP.rotationDegrees(this.getYaw(partialTicks)));
                }
            }

            if(Config.CLIENT.shouldFollowPitch.get())
            {
                quaternion.mul(Vector3f.XP.rotationDegrees(VehicleHelper.isThirdPersonFront() ? -vehicle.getPassengerPitchOffset() : vehicle.getPassengerPitchOffset()));
            }
            else
            {
                quaternion.mul(Vector3f.XP.rotationDegrees(MathHelper.lerp(partialTicks, player.xRotO, player.xRot)));
            }

            // If the player is in third person, applies additional vehicle specific camera rotations
            if(Config.CLIENT.useVehicleAsFocusPoint.get() && VehicleHelper.isThirdPersonBack())
            {
                CameraProperties camera = vehicle.getProperties().getCamera();
                Vector3d cameraRotation = camera.getRotation();
                quaternion.mul(Vector3f.YP.rotationDegrees((float) cameraRotation.y));
                quaternion.mul(Vector3f.XP.rotationDegrees((float) cameraRotation.x));
                quaternion.mul(Vector3f.ZP.rotationDegrees((float) cameraRotation.z));
            }

            // Finally applies local rotations to the camera
            rotation.mul(quaternion);

            Vector3f forward = info.getLookVector();
            forward.set(0.0F, 0.0F, 1.0F);
            forward.transform(rotation);

            Vector3f up = info.getUpVector();
            up.set(0.0F, 1.0F, 0.0F);
            up.transform(rotation);

            Vector3f left = (Vector3f) LEFT_FIELD.get(info);
            left.set(1.0F, 0.0F, 0.0F);
            left.transform(rotation);
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}
