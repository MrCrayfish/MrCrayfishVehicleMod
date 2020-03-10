package com.mrcrayfish.vehicle.common;

import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class Seat
{
    private Vec3d position;
    private boolean driver;
    private float yawOffset;

    public Seat(Vec3d position)
    {
        this(position, false);
    }

    public Seat(Vec3d position, float yawOffset)
    {
        this(position, false);
        this.yawOffset = yawOffset;
    }

    public Seat(Vec3d position, boolean driver)
    {
        this.position = position;
        this.driver = driver;
    }

    public Vec3d getPosition()
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