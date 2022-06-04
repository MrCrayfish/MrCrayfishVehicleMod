package com.mrcrayfish.vehicle.client.render.complex.transforms;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.raytrace.MatrixTransform;
import com.mrcrayfish.vehicle.client.render.complex.value.Dynamic;
import com.mrcrayfish.vehicle.client.render.complex.value.IValue;
import com.mrcrayfish.vehicle.client.render.complex.value.Static;
import com.mrcrayfish.vehicle.entity.VehicleEntity;

import java.lang.reflect.Type;

/**
 * Author: MrCrayfish
 */
public class Translate implements Transform
{
    private final IValue x, y, z;

    public Translate(IValue x, IValue y, IValue z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void apply(VehicleEntity entity, MatrixStack stack, float partialTicks)
    {
        stack.translate(this.x.getValue(entity, partialTicks) * 0.0625, this.y.getValue(entity, partialTicks) * 0.0625, this.z.getValue(entity, partialTicks) * 0.0625);
    }

    @Override
    public MatrixTransform create(VehicleEntity entity, float partialTicks)
    {
        return MatrixTransform.translate((float) this.x.getValue(entity, partialTicks) * 0.0625F, (float) this.y.getValue(entity, partialTicks) * 0.0625F, (float) this.z.getValue(entity, partialTicks) * 0.0625F);
    }

    public static class Deserializer implements JsonDeserializer<Translate>
    {
        @Override
        public Translate deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject object = json.getAsJsonObject();
            IValue x = this.get(object, "x", context);
            IValue y = this.get(object, "y", context);
            IValue z = this.get(object, "z", context);
            return new Translate(x, y, z);
        }

        private IValue get(JsonObject object, String key, JsonDeserializationContext context)
        {
            if(!object.has(key)) return Static.ZERO;
            JsonElement e = object.get(key);
            if(e.isJsonObject())
            {
                return context.deserialize(e, Dynamic.class);
            }
            else if(e.isJsonPrimitive())
            {
                return context.deserialize(e, Static.class);
            }
            throw new JsonParseException("Rotate values can only be a number or object");
        }
    }
}
