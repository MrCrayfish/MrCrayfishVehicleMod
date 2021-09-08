package com.mrcrayfish.vehicle.client;

import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.vector.Vector3d;

import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class CameraProperties
{
    public static final CameraProperties DEFAULT_CAMERA = new CameraProperties(Type.SMOOTH, 0.25F, new Vector3d(0, 1.5, 0), new Vector3d(10, 0, 0), 4.0);
    public static final Type DEFAULT_TYPE = Type.SMOOTH;
    public static final float DEFAULT_STRENGTH = 0.25F;
    public static final Vector3d DEFAULT_POSITION = new Vector3d(0, 1.5, 0);
    public static final Vector3d DEFAULT_ROTATION = new Vector3d(10, 0, 0);
    public static final float DEFAULT_DISTANCE = 4.0F;

    private final Type type;
    private final float strength;
    private final Vector3d position;
    private final Vector3d rotation;
    private final double distance;

    public CameraProperties(Type type, float strength, Vector3d position, Vector3d rotation, double distance)
    {
        this.type = type;
        this.strength = strength;
        this.position = position;
        this.rotation = rotation;
        this.distance = distance;
    }

    public Type getType()
    {
        return this.type;
    }

    public float getStrength()
    {
        return this.strength;
    }

    public Vector3d getPosition()
    {
        return this.position;
    }

    public Vector3d getRotation()
    {
        return this.rotation;
    }

    public double getDistance()
    {
        return this.distance;
    }

    public JsonObject toJsonObject()
    {
        JsonObject object = new JsonObject();
        ExtraJSONUtils.write(object, "type", this.type, DEFAULT_TYPE);
        ExtraJSONUtils.write(object, "strength", this.strength, DEFAULT_STRENGTH);
        ExtraJSONUtils.write(object, "position", this.position, DEFAULT_POSITION);
        ExtraJSONUtils.write(object, "rotation", this.rotation, DEFAULT_ROTATION);
        ExtraJSONUtils.write(object, "distance", this.distance, DEFAULT_DISTANCE);
        return object;
    }

    public static CameraProperties fromJsonObject(JsonObject object)
    {
        CameraProperties.Type type = ExtraJSONUtils.getAsEnum(object, "type", Type.class, DEFAULT_TYPE);
        float strength = JSONUtils.getAsFloat(object, "strength", DEFAULT_STRENGTH);
        Vector3d position = ExtraJSONUtils.getAsVector3d(object, "position", DEFAULT_POSITION);
        Vector3d rotation = ExtraJSONUtils.getAsVector3d(object, "rotation", DEFAULT_ROTATION);
        double distance = JSONUtils.getAsFloat(object, "distance", DEFAULT_DISTANCE);
        return new CameraProperties(type, strength, position, rotation, distance);
    }

    public enum Type
    {
        LOCKED("locked"),
        SMOOTH("smooth");

        private String id;

        Type(String id)
        {
            this.id = id;
        }

        public String getId()
        {
            return this.id;
        }

        public static Type fromId(String id)
        {
            return Stream.of(values())
                    .filter(type -> type.id.equals(id))
                    .findFirst()
                    .orElse(LOCKED);
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Type type = DEFAULT_TYPE;
        private float strength = DEFAULT_STRENGTH;
        private Vector3d position = DEFAULT_POSITION;
        private Vector3d rotation = DEFAULT_ROTATION;
        private double distance = DEFAULT_DISTANCE;

        public Builder setType(Type type)
        {
            this.type = type;
            return this;
        }

        public Builder setStrength(float strength)
        {
            this.strength = strength;
            return this;
        }

        public Builder setPosition(Vector3d position)
        {
            this.position = position;
            return this;
        }

        public Builder setPosition(double x, double y, double z)
        {
            this.position = new Vector3d(x, y, z);
            return this;
        }

        public Builder setRotation(Vector3d rotation)
        {
            this.rotation = rotation;
            return this;
        }

        public Builder setRotation(double x, double y, double z)
        {
            this.rotation = new Vector3d(x, y, z);
            return this;
        }

        public Builder setDistance(double distance)
        {
            this.distance = distance;
            return this;
        }

        public CameraProperties build()
        {
            return new CameraProperties(this.type, this.strength, this.position, this.rotation, this.distance);
        }
    }
}
