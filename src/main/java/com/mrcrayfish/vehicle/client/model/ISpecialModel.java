package com.mrcrayfish.vehicle.client.model;

import com.mrcrayfish.vehicle.client.render.complex.ComplexRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;

/**
 * Author: MrCrayfish
 */
public interface ISpecialModel
{
    @OnlyIn(Dist.CLIENT)
    IBakedModel getModel();

    static void registerCosmeticModel(ResourceLocation model)
    {
        ModelLoader.addSpecialModel(model);
        ComplexRenderer.loadCustomRendering(model);
    }
}
