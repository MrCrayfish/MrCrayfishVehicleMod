package com.mrcrayfish.vehicle.client.render.complex.transforms;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.render.complex.value.Dynamic;
import com.mrcrayfish.vehicle.client.render.complex.value.IValue;
import com.mrcrayfish.vehicle.client.render.complex.value.Static;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.util.math.vector.Vector3f;

import java.lang.reflect.Type;

/**
 * Author: MrCrayfish
 */
public class Rotate implements Transform
{
    private final IValue x, y, z;

    public Rotate(IValue x, IValue y, IValue z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void apply(VehicleEntity entity, MatrixStack stack, float partialTicks)
    {
        stack.mulPose(Vector3f.XP.rotationDegrees((float) this.x.getValue(entity, partialTicks)));
        stack.mulPose(Vector3f.YP.rotationDegrees((float) this.y.getValue(entity, partialTicks)));
        stack.mulPose(Vector3f.ZP.rotationDegrees((float) this.z.getValue(entity, partialTicks)));
    }

    public static class Deserializer implements JsonDeserializer<Rotate>
    {
        @Override
        public Rotate deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject object = json.getAsJsonObject();
            IValue x = this.get(object, "x", context);
            IValue y = this.get(object, "y", context);
            IValue z = this.get(object, "z", context);
            return new Rotate(x, y, z);
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
