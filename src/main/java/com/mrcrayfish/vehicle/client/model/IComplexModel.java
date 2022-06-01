package com.mrcrayfish.vehicle.client.model;

import com.mrcrayfish.vehicle.client.render.complex.ComplexModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public interface IComplexModel
{
    ResourceLocation getModelLocation();

    IBakedModel getBaseModel();

    @Nullable
    ComplexModel getComplexModel();
}
