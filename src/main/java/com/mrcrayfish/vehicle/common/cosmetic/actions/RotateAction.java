package com.mrcrayfish.vehicle.common.cosmetic.actions;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.entity.PlaneEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.util.Axis;
import net.minecraft.util.JSONUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class RotateAction extends Action
{
    private final Source source;
    private final Axis axis;
    private final float scale;

    public RotateAction(Source source, Axis axis, float scale)
    {
        this.source = source;
        this.axis = axis;
        this.scale = scale;
    }

    @Override
    public void beforeRender(MatrixStack stack, VehicleEntity vehicle, float partialTicks)
    {
        stack.mulPose(this.axis.getAxis().rotationDegrees(this.source.valueFunction.apply(vehicle, partialTicks)));
    }

    @Override
    public void serialize(JsonObject object)
    {
        object.addProperty("source", this.source.getKey());
        object.addProperty("axis", this.axis.getKey());
        object.addProperty("scale", this.scale);
    }

    public static Supplier<RotateAction> createSupplier(JsonObject object)
    {
        Source source = Source.fromKey(JSONUtils.getAsString(object, "source"));
        Axis axis = Axis.fromKey(JSONUtils.getAsString(object, "axis"));
        float scale = JSONUtils.getAsFloat(object, "scale", 1.0F);
        return () -> new RotateAction(source, axis, scale);
    }

    public enum Source
    {
        PROPELLER("propeller", (vehicle, partialTicks) -> {
            return vehicle instanceof PlaneEntity ? ((PlaneEntity) vehicle).getPropellerRotation(partialTicks) : 0F;
        });
        //TODO add more eventually

        private final String key;
        private final BiFunction<VehicleEntity, Float, Float> valueFunction;

        Source(String key, BiFunction<VehicleEntity, Float, Float> valueFunction)
        {
            this.key = key;
            this.valueFunction = valueFunction;
        }

        public String getKey()
        {
            return this.key;
        }

        @Nullable
        public static Source fromKey(@Nullable String key)
        {
            return Arrays.stream(values()).filter(axis -> axis.key.equals(key)).findFirst().orElse(null);
        }
    }
}
