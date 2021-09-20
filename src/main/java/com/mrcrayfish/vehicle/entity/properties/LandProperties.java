package com.mrcrayfish.vehicle.entity.properties;

import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
import net.minecraft.util.JSONUtils;

/**
 * Author: MrCrayfish
 */
public final class LandProperties extends ExtendedProperties
{
    public static final boolean DEFAULT_CAN_CHARGE = true;
    public static final boolean DEFAULT_CAN_WHEELIE = true;
    public static final boolean DEFAULT_CAN_SLIDE = true;
    public static final float DEFAULT_BRAKE_POWER = -1.0F;
    public static final float DEFAULT_MAX_REVERSE_SPEED = 5.0F;

    public final boolean canCharge;
    public final boolean canWheelie;
    public final boolean canSlide;
    public final float brakePower;
    public final float maxReverseSpeed;

    public LandProperties(JsonObject object)
    {
        this.canCharge = JSONUtils.getAsBoolean(object, "canCharge", DEFAULT_CAN_CHARGE);
        this.canWheelie = JSONUtils.getAsBoolean(object, "canWheelie", DEFAULT_CAN_WHEELIE);
        this.canSlide = JSONUtils.getAsBoolean(object, "canSlide", DEFAULT_CAN_SLIDE);
        this.brakePower = JSONUtils.getAsFloat(object, "brakePower", DEFAULT_BRAKE_POWER);
        this.maxReverseSpeed = JSONUtils.getAsFloat(object, "maxReverseSpeed", DEFAULT_MAX_REVERSE_SPEED);
    }

    public LandProperties(boolean canCharge, boolean canWheelie, boolean canSlide, float brakePower, float maxReverseSpeed)
    {
        this.canCharge = canCharge;
        this.canWheelie = canWheelie;
        this.canSlide = canSlide;
        this.brakePower = brakePower;
        this.maxReverseSpeed = maxReverseSpeed;
    }

    public boolean canCharge()
    {
        return this.canCharge;
    }

    public boolean canWheelie()
    {
        return this.canWheelie;
    }

    public boolean canSlide()
    {
        return this.canSlide;
    }

    public float getBrakePower()
    {
        return this.brakePower;
    }

    public float getMaxReverseSpeed()
    {
        return this.maxReverseSpeed;
    }

    @Override
    public void serialize(JsonObject object)
    {
        ExtraJSONUtils.write(object, "canCharge", this.canCharge, DEFAULT_CAN_CHARGE);
        ExtraJSONUtils.write(object, "canWheelie", this.canWheelie, DEFAULT_CAN_WHEELIE);
        ExtraJSONUtils.write(object, "canSlide", this.canSlide, DEFAULT_CAN_SLIDE);
        ExtraJSONUtils.write(object, "brakePower", this.brakePower, DEFAULT_BRAKE_POWER);
        ExtraJSONUtils.write(object, "maxReverseSpeed", this.maxReverseSpeed, DEFAULT_MAX_REVERSE_SPEED);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public final static class Builder
    {
        public boolean canCharge = DEFAULT_CAN_CHARGE;
        public boolean canWheelie = DEFAULT_CAN_WHEELIE;
        public boolean canSlide = DEFAULT_CAN_SLIDE;
        public float brakePower = DEFAULT_BRAKE_POWER;
        public float maxReverseSpeed = DEFAULT_MAX_REVERSE_SPEED;

        private Builder() {}

        public Builder setCanCharge(boolean canCharge)
        {
            this.canCharge = canCharge;
            return this;
        }

        public Builder setCanWheelie(boolean canWheelie)
        {
            this.canWheelie = canWheelie;
            return this;
        }

        public Builder setCanSlide(boolean canSlide)
        {
            this.canSlide = canSlide;
            return this;
        }

        public Builder setBrakePower(float brakePower)
        {
            this.brakePower = brakePower;
            return this;
        }

        public Builder setMaxReverseSpeed(float maxReverseSpeed)
        {
            this.maxReverseSpeed = maxReverseSpeed;
            return this;
        }

        public LandProperties build()
        {
            return new LandProperties(this.canCharge, this.canWheelie, this.canSlide, this.brakePower, this.maxReverseSpeed);
        }
    }
}
