package com.mrcrayfish.vehicle.common.entity;

import net.minecraft.util.math.vector.Vector3d;

import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public class Transform
{
    public static final Transform DEFAULT = new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0);

    private final Vector3d translate;
    private final Vector3d rotation;
    private final double scale;

    private Transform(Vector3d translate, Vector3d rotation, double scale)
    {
        this.translate = translate;
        this.rotation = rotation;
        this.scale = scale;
    }

    private Transform(double x, double y, double z, double rx, double ry, double rz, double s)
    {
        this(new Vector3d(x, y, z), new Vector3d(rx, ry, rz), s);
    }

    public Vector3d getTranslate()
    {
        return this.translate;
    }

    public Vector3d getRotation()
    {
        return this.rotation;
    }

    public double getX()
    {
        return this.translate.x;
    }

    public double getY()
    {
        return this.translate.y;
    }

    public double getZ()
    {
        return this.translate.z;
    }

    public double getRotX()
    {
        return this.rotation.x;
    }

    public double getRotY()
    {
        return this.rotation.y;
    }

    public double getRotZ()
    {
        return this.rotation.z;
    }

    public double getScale()
    {
        return this.scale;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null || this.getClass() != o.getClass())
            return false;
        Transform transform = (Transform) o;
        return Double.compare(transform.scale, scale) == 0 && this.translate.equals(transform.translate) && this.rotation.equals(transform.rotation);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.translate, this.rotation, this.scale);
    }

    public static Transform create(double scale)
    {
        return new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, scale);
    }

    public static Transform create(double x, double y, double z, double scale)
    {
        return new Transform(x, y, z, 0.0, 0.0, 0.0, scale);
    }

    public static Transform create(double x, double y, double z, double rx, double ry, double rz, double scale)
    {
        return new Transform(x, y, z, rx, ry, rz, scale);
    }

    public static Transform create(Vector3d translate, Vector3d rotation, double scale)
    {
        return new Transform(translate, rotation, scale);
    }
}
