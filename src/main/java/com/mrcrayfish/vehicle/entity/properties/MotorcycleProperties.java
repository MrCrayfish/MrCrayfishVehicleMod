package com.mrcrayfish.vehicle.entity.properties;

import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
import net.minecraft.util.JSONUtils;

/**
 * Author: MrCrayfish
 */
public final class MotorcycleProperties extends ExtendedProperties
{
    public static final float DEFAULT_LEAN_ANGLE = 45F;

    private final float maxLeanAngle;

    public MotorcycleProperties(JsonObject object)
    {
        this.maxLeanAngle = JSONUtils.getAsFloat(object, "maxLeanAngle", DEFAULT_LEAN_ANGLE);
    }

    public MotorcycleProperties(float leanAngle)
    {
        this.maxLeanAngle = leanAngle;
    }

    public float getMaxLeanAngle()
    {
        return this.maxLeanAngle;
    }

    @Override
    public void serialize(JsonObject object)
    {
        ExtraJSONUtils.write(object, "maxLeanAngle", this.maxLeanAngle, DEFAULT_LEAN_ANGLE);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public final static class Builder
    {
        public float maxLeanAngle = DEFAULT_LEAN_ANGLE;

        private Builder() {}

        public Builder setMaxLeanAngle(float maxLeanAngle)
        {
            this.maxLeanAngle = maxLeanAngle;
            return this;
        }

        public MotorcycleProperties build()
        {
            return new MotorcycleProperties(this.maxLeanAngle);
        }
    }
}
