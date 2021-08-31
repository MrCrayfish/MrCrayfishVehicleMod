package com.mrcrayfish.vehicle.entity.properties;

import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
import net.minecraft.util.JSONUtils;

/**
 * Author: MrCrayfish
 */
public class HelicopterProperties extends ExtendedProperties
{
    public static final float DEFAULT_RESPONSIVENESS =  0.015F;
    public static final float DEFAULT_MAX_LEAN_ANGLE = 30F;
    public static final float DEFAULT_DRAG = 0.001F;

    private final float responsiveness;
    private final float maxLeanAngle;
    private final float drag;

    public HelicopterProperties(JsonObject object)
    {
        this.responsiveness = JSONUtils.getAsFloat(object, "responsiveness", DEFAULT_RESPONSIVENESS);
        this.maxLeanAngle = JSONUtils.getAsFloat(object, "maxLeanAngle", DEFAULT_MAX_LEAN_ANGLE);
        this.drag = JSONUtils.getAsFloat(object, "drag", DEFAULT_DRAG);
    }

    public HelicopterProperties(float responsiveness, float maxLeanAngle, float drag)
    {
        this.responsiveness = responsiveness;
        this.maxLeanAngle = maxLeanAngle;
        this.drag = drag;
    }

    public float getResponsiveness()
    {
        return this.responsiveness;
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
        ExtraJSONUtils.write(object, "responsiveness", this.responsiveness, DEFAULT_RESPONSIVENESS);
        ExtraJSONUtils.write(object, "maxLeanAngle", this.maxLeanAngle, DEFAULT_MAX_LEAN_ANGLE);
        ExtraJSONUtils.write(object, "drag", this.drag, DEFAULT_DRAG);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private float responsiveness = DEFAULT_RESPONSIVENESS;
        private float maxLeanAngle = DEFAULT_MAX_LEAN_ANGLE;
        private float drag = DEFAULT_DRAG;

        private Builder() {}

        public Builder setResponsiveness(float responsiveness)
        {
            this.responsiveness = responsiveness;
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
            return new HelicopterProperties(this.responsiveness, this.maxLeanAngle, this.drag);
        }
    }
}
