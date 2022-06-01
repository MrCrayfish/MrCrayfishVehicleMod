package com.mrcrayfish.vehicle.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.render.complex.ComplexModel;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ComponentModel implements IComplexModel
{
    private final ResourceLocation modelLocation;
    @Nullable
    private IBakedModel cachedModel;
    @Nullable
    private ComplexModel complexModel;

    public ComponentModel(ResourceLocation modelLocation)
    {
        this.modelLocation = modelLocation;
    }

    @Override
    public ResourceLocation getModelLocation()
    {
        return this.modelLocation;
    }

    @Override
    public IBakedModel getBaseModel()
    {
        if(this.cachedModel == null)
        {
            this.cachedModel = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
        }
        return this.cachedModel;
    }

    public void clearCache()
    {
        this.cachedModel = null;
    }

    void setComplexModel(@Nullable ComplexModel complexModel)
    {
        this.complexModel = complexModel;
    }

    @Override
    @Nullable
    public ComplexModel getComplexModel()
    {
        return this.complexModel;
    }

    public void render(VehicleEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int color, int light, float partialTicks)
    {
        if(this.complexModel != null)
        {
            this.complexModel.render(entity, matrixStack, renderTypeBuffer, partialTicks, color, light);
        }
        else
        {
            RenderUtil.renderColoredModel(this.getBaseModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, color, light, OverlayTexture.NO_OVERLAY);
        }
    }
}
