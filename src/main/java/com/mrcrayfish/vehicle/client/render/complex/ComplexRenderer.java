package com.mrcrayfish.vehicle.client.render.complex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.render.complex.transforms.Rotate;
import com.mrcrayfish.vehicle.client.render.complex.transforms.Translate;
import com.mrcrayfish.vehicle.client.render.complex.value.Dynamic;
import com.mrcrayfish.vehicle.client.render.complex.value.Static;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.IResource;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ComplexRenderer
{
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ComplexModel.class, new ComplexModel.Deserializer())
            .registerTypeAdapter(Translate.class, new Translate.Deserializer())
            .registerTypeAdapter(Rotate.class, new Rotate.Deserializer())
            .registerTypeAdapter(Static.class, new Static.Deserializer())
            .registerTypeAdapter(Dynamic.class, new Dynamic.Deserializer())
            .create();

    private static final Map<ResourceLocation, ComplexModel> CUSTOM_MODELS = new HashMap<>();

    public static void loadCustomRendering(ResourceLocation location)
    {
        try
        {
            Minecraft minecraft = Minecraft.getInstance();
            ResourceLocation cosmeticLocation = new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".complex");
            if(minecraft.getResourceManager().hasResource(cosmeticLocation))
            {
                IResource resource = minecraft.getResourceManager().getResource(cosmeticLocation);
                Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                ComplexModel model = JSONUtils.fromJson(GSON, reader, ComplexModel.class);
                CUSTOM_MODELS.put(location, model);
            }
        }
        catch(JsonParseException | ResourceLocationException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void renderModel(ResourceLocation id, IBakedModel model, VehicleEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int color, int light, float partialTicks)
    {
        if(CUSTOM_MODELS.containsKey(id))
        {
            CUSTOM_MODELS.get(id).render(entity, matrixStack, renderTypeBuffer, partialTicks, color, light);
        }
        else
        {
            RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, color, light, OverlayTexture.NO_OVERLAY);
        }
    }
}
