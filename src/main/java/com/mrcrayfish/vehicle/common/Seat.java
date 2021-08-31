package com.mrcrayfish.vehicle.common;

import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Author: MrCrayfish
 */
public class Seat
{
    public static final Vector3d DEFAULT_POSITION = Vector3d.ZERO;
    public static final boolean DEFAULT_IS_DRIVER_SEAT = false;
    public static final float DEFAULT_YAW_OFFSET = 0F;

    private final Vector3d position;
    private final boolean isDriver;
    private final float yawOffset;

    protected Seat(Vector3d position)
    {
        this(position, false);
    }

    protected Seat(Vector3d position, float yawOffset)
    {
        this(position, false, yawOffset);
    }

    protected Seat(Vector3d position, boolean isDriver)
    {
        this(position, isDriver, DEFAULT_YAW_OFFSET);
    }

    public Seat(Vector3d position, boolean isDriver, float yawOffset)
    {
        this.position = position;
        this.isDriver = isDriver;
        this.yawOffset = yawOffset;
    }

    public Vector3d getPosition()
    {
        return this.position;
    }

    public boolean isDriver()
    {
        return this.isDriver;
    }

    public float getYawOffset()
    {
        return this.yawOffset;
    }

    public JsonObject toJsonObject()
    {
        JsonObject object = new JsonObject();
        ExtraJSONUtils.write(object, "position", this.position, DEFAULT_POSITION);
        ExtraJSONUtils.write(object, "driver", this.isDriver, DEFAULT_IS_DRIVER_SEAT);
        ExtraJSONUtils.write(object, "yawOffset", this.yawOffset, DEFAULT_YAW_OFFSET);
        return object;
    }

    public static Seat fromJsonObject(JsonObject object)
    {
        Vector3d position = ExtraJSONUtils.getAsVector3d(object, "position", DEFAULT_POSITION);
        boolean isDriverSeat = JSONUtils.getAsBoolean(object, "driver", DEFAULT_IS_DRIVER_SEAT);
        float yawOffset = JSONUtils.getAsFloat(object, "yawOffset", DEFAULT_YAW_OFFSET);
        return new Seat(position, isDriverSeat, yawOffset);
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
