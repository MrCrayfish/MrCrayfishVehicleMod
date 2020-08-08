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

    public Seat(Vector3d position)
    {
        this(position, false);
    }

    public Seat(Vector3d position, float yawOffset)
    {
        this(position, false);
        this.yawOffset = yawOffset;
    }

    public Seat(Vector3d position, boolean driver)
    {
        this.position = position;
        this.driver = driver;
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
}
