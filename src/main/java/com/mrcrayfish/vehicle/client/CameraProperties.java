package com.mrcrayfish.vehicle.client;

import net.minecraft.util.math.vector.Vector3d;

import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class CameraProperties
{
    public static final CameraProperties DEFAULT_CAMERA = new CameraProperties(Type.SMOOTH, 0.15F, new Vector3d(0, 1, 0), new Vector3d(10, 0, 0), 5.0);

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
        private Type type;
        private float strength;
        private Vector3d position;
        private Vector3d rotation;
        private double distance;

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

        public Builder setRotation(Vector3d rotation)
        {
            this.rotation = rotation;
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
