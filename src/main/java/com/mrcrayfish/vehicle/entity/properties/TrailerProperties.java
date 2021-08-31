package com.mrcrayfish.vehicle.entity.properties;

import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
import net.minecraft.util.JSONUtils;

/**
 * Author: MrCrayfish
 */
public class TrailerProperties extends ExtendedProperties
{
    public static final float DEFAULT_HITCH_OFFSET = 1.0F;

    private final double hitchOffset;

    public TrailerProperties(JsonObject object)
    {
        this.hitchOffset = JSONUtils.getAsFloat(object, "hitchOffset", DEFAULT_HITCH_OFFSET);
    }

    public TrailerProperties(double hitchOffset)
    {
        this.hitchOffset = hitchOffset;
    }

    public double getHitchOffset()
    {
        return this.hitchOffset;
    }

    @Override
    public void serialize(JsonObject object)
    {
        ExtraJSONUtils.write(object, "hitchOffset", this.hitchOffset, DEFAULT_HITCH_OFFSET);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private double hitchOffset = DEFAULT_HITCH_OFFSET;

        private Builder() {}

        public Builder setHitchOffset(double hitchOffset)
        {
            this.hitchOffset = hitchOffset;
            return this;
        }

        public TrailerProperties build()
        {
            return new TrailerProperties(this.hitchOffset);
        }
    }
}
