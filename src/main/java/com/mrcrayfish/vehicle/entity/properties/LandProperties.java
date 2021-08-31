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

    public final boolean canCharge;
    public final boolean canWheelie;

    public LandProperties(JsonObject object)
    {
        this.canCharge = JSONUtils.getAsBoolean(object, "canCharge", DEFAULT_CAN_CHARGE);
        this.canWheelie = JSONUtils.getAsBoolean(object, "canWheelie", DEFAULT_CAN_WHEELIE);
    }

    public LandProperties(boolean canCharge, boolean canWheelie)
    {
        this.canCharge = canCharge;
        this.canWheelie = canWheelie;
    }

    public boolean canCharge()
    {
        return this.canCharge;
    }

    public boolean canWheelie()
    {
        return this.canWheelie;
    }

    @Override
    public void serialize(JsonObject object)
    {
        ExtraJSONUtils.write(object, "canCharge", this.canCharge, DEFAULT_CAN_CHARGE);
        ExtraJSONUtils.write(object, "canWheelie", this.canWheelie, DEFAULT_CAN_WHEELIE);
    }

    public final static class Builder
    {
        public boolean canCharge = DEFAULT_CAN_CHARGE;
        public boolean canWheelie = DEFAULT_CAN_WHEELIE;

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

        public LandProperties build()
        {
            return new LandProperties(this.canCharge, this.canWheelie);
        }
    }
}
