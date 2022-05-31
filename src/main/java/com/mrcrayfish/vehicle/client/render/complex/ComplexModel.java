package com.mrcrayfish.vehicle.client.render.complex;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.render.complex.transforms.Rotate;
import com.mrcrayfish.vehicle.client.render.complex.transforms.Transform;
import com.mrcrayfish.vehicle.client.render.complex.transforms.Translate;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A special type of model that is fully data driven. Complex models can be made up of multiple models
 * with each model having it's own transforms and children. Transforms can take in data from vehicles,
 * such has steering angle, and apply it before rendering the model and it's subsequent children.
 *
 * Author: MrCrayfish
 */
public class ComplexModel
{
    private final ResourceLocation modelLocation;
    private final List<Transform> transforms;
    private final List<ComplexModel> children;
    private IBakedModel cachedModel;

    public ComplexModel(ResourceLocation modelLocation, List<Transform> transforms, List<ComplexModel> children)
    {
        this.modelLocation = modelLocation;
        this.transforms = transforms;
        this.children = children;
    }

    public void render(VehicleEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int color, int light)
    {
        this.transforms.forEach(transform -> transform.apply(entity, matrixStack, partialTicks));
        RenderUtil.renderColoredModel(this.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, color, light, OverlayTexture.NO_OVERLAY);
        this.children.forEach(model -> {
            matrixStack.pushPose();
            model.render(entity, matrixStack, renderTypeBuffer, partialTicks, color, light);
            matrixStack.popPose();
        });
    }

    public final IBakedModel getModel()
    {
        if(this.cachedModel == null)
        {
            this.cachedModel = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
        }
        return this.cachedModel;
    }

    public static class Deserializer implements JsonDeserializer<ComplexModel>
    {
        @Override
        public ComplexModel deserialize(JsonElement root, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject object = root.getAsJsonObject();
            ResourceLocation location = new ResourceLocation(JSONUtils.getAsString(object, "model"));
            List<Transform> transforms = Collections.emptyList();
            if(object.has("transforms") && object.get("transforms").isJsonArray())
            {
                transforms = new ArrayList<>();
                JsonArray transformArray = JSONUtils.getAsJsonArray(object, "transforms");
                for(JsonElement e : transformArray)
                {
                    if(!e.isJsonObject()) throw new JsonParseException("Transforms array can only contain objects");
                    JsonObject transformObj = e.getAsJsonObject();
                    String transformType = JSONUtils.getAsString(transformObj, "type");
                    switch(transformType)
                    {
                        case "translate":
                            transforms.add(context.deserialize(transformObj, Translate.class));
                            break;
                        case "rotate":
                            transforms.add(context.deserialize(transformObj, Rotate.class));
                            break;
                    }
                }
            }
            List<ComplexModel> children = Collections.emptyList();
            if(object.has("children") && object.get("children").isJsonArray())
            {
                children = new ArrayList<>();
                JsonArray childrenArray = JSONUtils.getAsJsonArray(object, "children");
                for(JsonElement e : childrenArray)
                {
                    if(!e.isJsonObject()) throw new JsonParseException("Children array can only contain objects");
                    JsonObject childrenObj = e.getAsJsonObject();
                    children.add(context.deserialize(childrenObj, ComplexModel.class));
                }
            }
            return new ComplexModel(location, transforms, children);
        }
    }
}
