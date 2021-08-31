package com.mrcrayfish.vehicle.entity.properties;

import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
import net.minecraft.util.JSONUtils;

/**
 * Author: MrCrayfish
 */
public class HelicopterProperties extends ExtendedProperties
{
    public static final float DEFAULT_MOVEMENT_STRENGTH = 0.015F;
    public static final float DEFAULT_ROTATE_STRENGTH = 0.05F;
    public static final float DEFAULT_MAX_LEAN_ANGLE = 30F;
    public static final float DEFAULT_DRAG = 0.001F;

    private final float movementStrength;
    private final float rotateStrength;
    private final float maxLeanAngle;
    private final float drag;

    public HelicopterProperties(JsonObject object)
    {
        this.movementStrength = JSONUtils.getAsFloat(object, "movementStrength", DEFAULT_MOVEMENT_STRENGTH);
        this.rotateStrength = JSONUtils.getAsFloat(object, "rotateStrength", DEFAULT_ROTATE_STRENGTH);
        this.maxLeanAngle = JSONUtils.getAsFloat(object, "maxLeanAngle", DEFAULT_MAX_LEAN_ANGLE);
        this.drag = JSONUtils.getAsFloat(object, "drag", DEFAULT_DRAG);
    }

    public HelicopterProperties(float movementStrength, float rotateStrength, float maxLeanAngle, float drag)
    {
        this.movementStrength = movementStrength;
        this.rotateStrength = rotateStrength;
        this.maxLeanAngle = maxLeanAngle;
        this.drag = drag;
    }

    public float getMovementStrength()
    {
        return this.movementStrength;
    }

    public float getRotateStrength()
    {
        return this.rotateStrength;
    }

    public float getMaxLeanAngle()
    {
        return this.maxLeanAngle;
    }

    public float getDrag()
    {
        return this.drag;
    }

    @Override
    public void serialize(JsonObject object)
    {
        ExtraJSONUtils.write(object, "movementStrength", this.movementStrength, DEFAULT_MOVEMENT_STRENGTH);
        ExtraJSONUtils.write(object, "rotateStrength", this.rotateStrength, DEFAULT_ROTATE_STRENGTH);
        ExtraJSONUtils.write(object, "maxLeanAngle", this.maxLeanAngle, DEFAULT_MAX_LEAN_ANGLE);
        ExtraJSONUtils.write(object, "drag", this.drag, DEFAULT_DRAG);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private float movementStrength = DEFAULT_MOVEMENT_STRENGTH;
        private float rotateStrength = DEFAULT_ROTATE_STRENGTH;
        private float maxLeanAngle = DEFAULT_MAX_LEAN_ANGLE;
        private float drag = DEFAULT_DRAG;

        private Builder() {}

        public Builder setMovementStrength(float movementStrength)
        {
            this.movementStrength = movementStrength;
            return this;
        }

        public Builder setRotateStrength(float rotateStrength)
        {
            this.rotateStrength = rotateStrength;
            return this;
        }

        public Builder setMaxLeanAngle(float maxLeanAngle)
        {
            this.maxLeanAngle = maxLeanAngle;
            return this;
        }

        public Builder setDrag(float drag)
        {
            this.drag = drag;
            return this;
        }

        public HelicopterProperties build()
        {
            return new HelicopterProperties(this.movementStrength, this.rotateStrength, this.maxLeanAngle, this.drag);
        }
    }
}
