package com.mrcrayfish.vehicle.common.entity;

import net.minecraft.util.math.vector.Vector3d;

/**
 * Author: MrCrayfish
 */
public class PartPosition
{
    public static final PartPosition DEFAULT = new PartPosition(1.0);

    private Vector3d translate = Vector3d.ZERO;
    private Vector3d rotation = Vector3d.ZERO;
    private double scale;

    public PartPosition(double scale)
    {
        this.scale = scale;
    }

    public PartPosition(double offsetX, double offsetY, double offsetZ, double scale)
    {
        this.translate = new Vector3d(offsetX, offsetY, offsetZ);
        this.scale = scale;
    }

    public PartPosition(double offsetX, double offsetY, double offsetZ, double rotX, double rotY, double rotZ, double scale)
    {
        this.translate = new Vector3d(offsetX, offsetY, offsetZ);
        this.rotation = new Vector3d(rotX, rotY, rotZ);
        this.scale = scale;
    }

    public Vector3d getTranslate()
    {
        return translate;
    }

    public Vector3d getRotation()
    {
        return rotation;
    }

    public double getX()
    {
        return translate.x;
    }

    public double getY()
    {
        return translate.y;
    }

    public double getZ()
    {
        return translate.z;
    }

    public double getRotX()
    {
        return rotation.x;
    }

    public double getRotY()
    {
        return rotation.y;
    }

    public double getRotZ()
    {
        return rotation.z;
    }

    public double getScale()
    {
        return scale;
    }

    public void update(double x, double y, double z, double rotX, double rotY, double rotZ, double scale)
    {
        this.translate = new Vector3d(x, y, z);
        this.rotation = new Vector3d(rotX, rotY, rotZ);
        this.scale = scale;
    }

    public static PartPosition create(double scale)
    {
        return new PartPosition(scale);
    }

    public static PartPosition create(double x, double y, double z, double scale)
    {
        return new PartPosition(x, y, z, scale);
    }

    public static PartPosition create(double x, double y, double z, double rx, double ry, double rz, double scale)
    {
        return new PartPosition(x, y, z, rx, ry, rz, scale);
    }
}
