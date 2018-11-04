package com.mrcrayfish.vehicle.client.model;

import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.vehicle.client.model.baked.BakedModelSteepRamp;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class ModelSteepRamp implements IModel
{
    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return ImmutableSet.of(new ResourceLocation("minecraft", "blocks/concrete_gray"), new ResourceLocation("vehicle", "blocks/boost_pad"));
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        return new BakedModelSteepRamp(format, bakedTextureGetter);
    }
}
