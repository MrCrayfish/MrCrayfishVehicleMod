package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.util.MathUtil;
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
import net.minecraftforge.common.model.TransformationHelper;
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
    private static final Method SET_POSITION_METHOD = ObfuscationReflectionHelper.findMethod(ActiveRenderInfo.class, "func_216775_b", double.class, double.class, double.class);
    private static final Method MOVE_METHOD = ObfuscationReflectionHelper.findMethod(ActiveRenderInfo.class, "func_216782_a", double.class, double.class, double.class);
    private static final Method GET_MAX_MOVE_METHOD = ObfuscationReflectionHelper.findMethod(ActiveRenderInfo.class, "func_216779_a", double.class);
    private static final Field LEFT_FIELD = ObfuscationReflectionHelper.findField(ActiveRenderInfo.class, "field_216796_h");

    private VehicleProperties properties;
    private Quaternion currentRotation;
    private Quaternion prevRotation;
    private float pitchOffset;
    private float yawOffset;

    // Debug properties
    public float debugOffsetX;
    public float debugOffsetY;
    public float debugOffsetZ;
    public float debugOffsetPitch;
    public float debugOffsetYaw;
    public float debugOffsetRoll;
    public boolean debugEnableStrength = true;

    public void load(VehicleEntity vehicle)
    {
        this.properties = vehicle.getProperties();
        this.pitchOffset = 0F;
        this.yawOffset = 0F;
        this.currentRotation = new Quaternion(vehicle.getViewPitch(1F), -vehicle.getViewYaw(1F), vehicle.getViewRoll(1F), true);
        this.prevRotation = new Quaternion(this.currentRotation);
    }

    public void tick(VehicleEntity vehicle, PointOfView pov)
    {
        float strength = this.getStrength(pov);
        this.prevRotation = this.currentRotation;
        Quaternion quaternion = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
        quaternion.mul(Vector3f.YP.rotationDegrees(-vehicle.getViewYaw(1F) + (Config.CLIENT.debugCamera.get() ? this.debugOffsetYaw : 0F)));
        quaternion.mul(Vector3f.XP.rotationDegrees(vehicle.getViewPitch(1F) + (Config.CLIENT.debugCamera.get() ? this.debugOffsetPitch : 0F)));
        quaternion.mul(Vector3f.ZP.rotationDegrees(vehicle.getViewRoll(1F) + (Config.CLIENT.debugCamera.get() ? this.debugOffsetRoll : 0F)));
        this.currentRotation = MathUtil.slerp(this.currentRotation, quaternion, strength);
    }

    private float getStrength(PointOfView pov)
    {
        return (!Config.CLIENT.debugCamera.get() || this.debugEnableStrength) && pov == PointOfView.THIRD_PERSON_BACK && this.properties.getCamera().getType() != CameraProperties.Type.LOCKED ? this.properties.getCamera().getStrength() : 1.0F;
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
                Vector3f rotatedEyePos = new Vector3f(eyePos);
                rotatedEyePos.transform(MathUtil.slerp(this.prevRotation, this.currentRotation, partialTicks));
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
                Vector3f rotatedPosition = new Vector3f(position);
                if(Config.CLIENT.debugCamera.get()) rotatedPosition.add(this.debugOffsetX, this.debugOffsetY, this.debugOffsetZ);
                rotatedPosition.transform(MathUtil.slerp(this.prevRotation, this.currentRotation, partialTicks));
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
                    Vector3f rotatedEyePos = new Vector3f(eyePos);
                    rotatedEyePos.transform(TransformationHelper.slerp(this.prevRotation, this.currentRotation, partialTicks));
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
            //TODO add this back
            /*if(Config.CLIENT.shouldFollowYaw.get())
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
            }*/

            rotation.mul(MathUtil.slerp(this.prevRotation, this.currentRotation, partialTicks));

            // Applies the player's pitch and yaw offset
            Quaternion quaternion = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);

            if(VehicleHelper.isThirdPersonFront())
            {
                quaternion.mul(Vector3f.YP.rotationDegrees(180F));
            }

            if(vehicle.canApplyYawOffset(player) && Config.CLIENT.shouldFollowYaw.get())
            {
                quaternion.mul(Vector3f.YP.rotationDegrees(-this.yawOffset));
            }
            else
            {
                quaternion.mul(Vector3f.YP.rotationDegrees(-player.getViewYRot(partialTicks)));
                /*if(Config.CLIENT.shouldFollowYaw.get())
                {
                    quaternion.mul(Vector3f.YP.rotationDegrees(this.getYaw(partialTicks)));
                }*/
            }

            if(Config.CLIENT.shouldFollowPitch.get())
            {
                quaternion.mul(Vector3f.XP.rotationDegrees(VehicleHelper.isThirdPersonFront() ? -this.pitchOffset : this.pitchOffset));
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

    public void turnPlayerView(double x, double y)
    {
        this.pitchOffset += y * 0.15F;
        this.yawOffset += x * 0.15F;
        this.pitchOffset = MathHelper.clamp(this.pitchOffset, -90F, 90F);
        this.yawOffset = MathHelper.clamp(this.yawOffset, -135F, 135F);
    }
}
