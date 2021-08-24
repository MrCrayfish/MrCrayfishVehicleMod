package com.mrcrayfish.vehicle.common;

import net.minecraft.util.math.vector.Vector3d;

/**
 * Author: MrCrayfish
 */
public class Seat
{
    private Vector3d position;
    private boolean driver;
    private float yawOffset;

    protected Seat(Vector3d position)
    {
        this(position, false);
    }

    protected Seat(Vector3d position, float yawOffset)
    {
        this(position, false);
        this.yawOffset = yawOffset;
    }

    protected Seat(Vector3d position, boolean driver)
    {
        this.position = position;
        this.driver = driver;
    }

    public Seat(Vector3d position, boolean driver, float yawOffset)
    {
        this.position = position;
        this.driver = driver;
        this.yawOffset = yawOffset;
    }

    public Vector3d getPosition()
    {
        return position;
    }

    public boolean isDriverSeat()
    {
        return driver;
    }

    public float getYawOffset()
    {
        return yawOffset;
    }

    public static Seat of(double x, double y, double z)
    {
        return new Seat(new Vector3d(x, y, z));
    }

    public static Seat of(double x, double y, double z, boolean driver)
    {
        return new Seat(new Vector3d(x, y, z), driver);
    }

    public static Seat of(double x, double y, double z, float yawOffset)
    {
        return new Seat(new Vector3d(x, y, z), yawOffset);
    }

    public static Seat of(double x, double y, double z, boolean driver, float yawOffset)
    {
        return new Seat(new Vector3d(x, y, z), driver, yawOffset);
    }
}
