package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
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

    // Allow customisation like smoothness through properties
    private float rotX;
    private float rotY;
    private float rotZ;
    private float prevRotX;
    private float prevRotY;
    private float prevRotZ;

    public void load(VehicleEntity vehicle)
    {
        this.rotX = vehicle.getBodyRotationX();
        this.rotY = vehicle.getBodyRotationY();
        this.rotZ = vehicle.getBodyRotationZ();
        this.prevRotX = this.rotX;
        this.prevRotY = this.rotY;
        this.prevRotZ = this.rotZ;
    }

    public void tick(VehicleEntity vehicle)
    {
        this.prevRotX = this.rotX;
        this.prevRotY = this.rotY;
        this.prevRotZ = this.rotZ;
        float newRotX = vehicle.getBodyRotationX();
        float newRotY = vehicle.getBodyRotationY();
        float newRotZ = vehicle.getBodyRotationZ();
        this.rotX = MathHelper.rotLerp(0.05F, this.rotX, newRotX);
        this.rotY = MathHelper.rotLerp(0.05F, this.rotY, newRotY);
        this.rotZ = MathHelper.rotLerp(0.05F, this.rotZ, newRotZ);
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
        return MathHelper.rotLerp(partialTicks, this.prevRotX, this.rotX);
    }

    public float getPitch(float partialTicks)
    {
        return -(float) new Vector3d(this.getRotX(partialTicks), 0, this.getRotZ(partialTicks)).yRot((float) Math.toRadians(-(this.getRotY(partialTicks) + 90))).x;
    }

    public float getRoll(float partialTicks)
    {
        return (float) new Vector3d(this.getRotX(partialTicks), 0, this.getRotZ(partialTicks)).yRot((float) Math.toRadians(-(this.getRotY(partialTicks) + 90))).z;
    }

    /* I call this a good ol' reflection hack */
    public void setupVanillaCamera(ActiveRenderInfo info, VehicleEntity vehicle, float partialTicks)
    {
        try
        {
            SET_ROTATION_METHOD.invoke(info, this.getRotY(partialTicks), this.getPitch(partialTicks));
            SET_POSITION_METHOD.invoke(info, MathHelper.lerp(partialTicks, vehicle.xo, vehicle.getX()), MathHelper.lerp(partialTicks, vehicle.yo, vehicle.getY()), MathHelper.lerp(partialTicks, vehicle.zo, vehicle.getZ()));
            MOVE_METHOD.invoke(info, -(double) GET_MAX_MOVE_METHOD.invoke(info, 4.0), 0, 0);
        }
        catch(InvocationTargetException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}
