package com.mrcrayfish.vehicle.client.render.complex.value;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mrcrayfish.vehicle.entity.VehicleEntity;

import java.lang.reflect.Type;

/**
 * Author: MrCrayfish
 */
public class Static implements IValue
{
    public static final Static ZERO = new Static(0.0);

    private final double value;

    private Static(double value)
    {
        this.value = value;
    }

    public double getValue(VehicleEntity entity, float partialTicks)
    {
        return this.value;
    }

    public static class Deserializer implements JsonDeserializer<Static>
    {
        @Override
        public Static deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            if(json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber())
            {
                return new Static(json.getAsDouble());
            }
            throw new JsonParseException("Static values must be a number");
        }
    }
}
