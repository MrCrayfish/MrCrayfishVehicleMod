package com.mrcrayfish.vehicle.client.model;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
public interface ISpecialModel
{
    @OnlyIn(Dist.CLIENT)
    IBakedModel getModel();
}
