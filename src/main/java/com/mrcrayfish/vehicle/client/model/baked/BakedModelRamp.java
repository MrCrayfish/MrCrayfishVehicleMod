package com.mrcrayfish.vehicle.client.model.baked;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.block.BlockBoostRamp;
import com.mrcrayfish.vehicle.block.BlockRotatedObject;
import com.mrcrayfish.vehicle.client.util.BakedQuadBuilder;
import com.mrcrayfish.vehicle.client.util.QuadHelper;
import com.mrcrayfish.vehicle.client.util.TransformationBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class BakedModelRamp implements IBakedModel
{
    private static final ImmutableMap<ItemCameraTransforms.TransformType, Matrix4f> CAMERA_TRANSFORMATIONS;

    static
    {
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, Matrix4f> builder = ImmutableMap.builder();
        builder.put(ItemCameraTransforms.TransformType.FIXED, new TransformationBuilder().setScale(0.5F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.GUI, new TransformationBuilder().setTranslation(0.1F, 2, 0).setRotation(20, 110.5F, 0).setScale(0.7F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.GROUND, new TransformationBuilder().setTranslation(0, 1, 0).setScale(0.25F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, new TransformationBuilder().setTranslation(0, 4, 0).setRotation(0, -45, 0).setScale(0.4F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, new TransformationBuilder().setTranslation(0, 4, 0).setRotation(0, 135, 0).setScale(0.4F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, new TransformationBuilder().setTranslation(0, 2.5F, 3.5F).setRotation(75, 315, 0).setScale(0.375F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, new TransformationBuilder().setTranslation(0, 2.5F, 3.5F).setRotation(75, 135, 0).setScale(0.375F).build().getMatrix());
        CAMERA_TRANSFORMATIONS = builder.build();
    }

    private VertexFormat format;
    private TextureAtlasSprite anvilTexture;
    private TextureAtlasSprite rampTexture;

    public BakedModelRamp(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        this.format = format;
        this.anvilTexture = bakedTextureGetter.apply(new ResourceLocation("minecraft", "blocks/anvil_base"));
        this.rampTexture = bakedTextureGetter.apply(new ResourceLocation("vehicle", "blocks/boost_pad"));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        List<BakedQuad> quads = Lists.newArrayList();
        if(side == null)
        {
            BakedQuadBuilder builder = new BakedQuadBuilder(format);
            builder.setFacing(EnumFacing.NORTH);

            if(state != null && state.getPropertyKeys().contains(BlockRotatedObject.FACING))
            {
                EnumFacing facing = state.getValue(BlockRotatedObject.FACING);
                builder.setFacing(facing);
            }

            builder.setTexture(rampTexture);

            float startHeight = 0.0F;
            float endHeight = 0.5F;

            if(state != null && state.getPropertyKeys().contains(BlockBoostRamp.STACKED))
            {
                if(state.getValue(BlockBoostRamp.STACKED))
                {
                    startHeight = 0.5F;
                    endHeight = 1.0F;
                }
            }

            //Boost Pad
            builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 1 * 0.0625F, 1, 0), EnumFacing.UP);
            builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 15 * 0.0625F, 15, 0), EnumFacing.UP);
            builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 15 * 0.0625F, 15, 16), EnumFacing.UP);
            builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 1 * 0.0625F, 1, 16), EnumFacing.UP);
            quads.add(builder.build());

            builder.setTexture(anvilTexture);

            //Left Trim
            builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 0, 0, 0), EnumFacing.UP);
            builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 1 * 0.0625F, 1, 0), EnumFacing.UP);
            builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 1 * 0.0625F, 1, 16), EnumFacing.UP);
            builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 0, 0, 16), EnumFacing.UP);
            quads.add(builder.build());

            //Right Trim
            builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 15 * 0.0625F, 15, 0), EnumFacing.UP);
            builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 1, 16, 0), EnumFacing.UP);
            builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 1, 16, 16), EnumFacing.UP);
            builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 15 * 0.0625F, 15, 16), EnumFacing.UP);
            quads.add(builder.build());

            EnumFacing facing = builder.getFacing();

            //Left Side
            builder.put(new BakedQuadBuilder.VertexData(1, 0, 0, 0, 0), facing.rotateYCCW());
            builder.put(new BakedQuadBuilder.VertexData(0, 0, 0, 16, 0), facing.rotateYCCW());
            builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 0, 16, 16 * startHeight), facing.rotateYCCW());
            builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 0, 0, 16 * endHeight), facing.rotateYCCW());
            quads.add(builder.build());

            //Back Side
            builder.put(new BakedQuadBuilder.VertexData(1, 0, 1, 0, 0), facing);
            builder.put(new BakedQuadBuilder.VertexData(1, 0, 0, 16, 0), facing);
            builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 0, 16, 16 * endHeight), facing);
            builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 1, 0, 16 * endHeight), facing);
            quads.add(builder.build());

            if(startHeight > 0.0F)
            {
                builder.put(new BakedQuadBuilder.VertexData(0, 0, 0, 0, 0), facing);
                builder.put(new BakedQuadBuilder.VertexData(0, 0, 1, 16, 0), facing);
                builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 1, 16, 16 * startHeight), facing);
                builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 0, 0, 16 * startHeight), facing);
                quads.add(builder.build());
            }

            //Right Side
            builder.put(new BakedQuadBuilder.VertexData(0, 0, 1, 0, 0), facing.rotateY());
            builder.put(new BakedQuadBuilder.VertexData(1, 0, 1, 16, 0), facing.rotateY());
            builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 1, 16, 16 * endHeight), facing.rotateY());
            builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 1, 0, 16 * startHeight), facing.rotateY());
            quads.add(builder.build());

            //Bottom
            builder.put(new BakedQuadBuilder.VertexData(0, 0, 1, 0, 0), facing);
            builder.put(new BakedQuadBuilder.VertexData(0, 0, 0, 16, 0), facing);
            builder.put(new BakedQuadBuilder.VertexData(1, 0, 0, 16, 16), facing);
            builder.put(new BakedQuadBuilder.VertexData(1, 0, 1, 0, 16), facing);
            quads.add(builder.build());
        }
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return true;
    }

    @Override
    public boolean isGui3d()
    {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return anvilTexture;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return new ItemOverrideList(Lists.newArrayList());
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, Matrix4f> builder = ImmutableMap.builder();
        builder.put(ItemCameraTransforms.TransformType.FIXED, new TransformationBuilder().setScale(0.5F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.GUI, new TransformationBuilder().setTranslation(0.1F, 2, 0).setRotation(30.0F, 45.0F, 0).setScale(0.625F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.GROUND, new TransformationBuilder().setTranslation(0, 1, 0).setScale(0.25F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, new TransformationBuilder().setTranslation(0, 4, 0).setRotation(0, -45, 0).setScale(0.4F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, new TransformationBuilder().setTranslation(0, 4, 0).setRotation(0, 135, 0).setScale(0.4F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, new TransformationBuilder().setTranslation(0, 2.5F, 3.5F).setRotation(75, 315, 0).setScale(0.375F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, new TransformationBuilder().setTranslation(0, 2.5F, 3.5F).setRotation(75, 135, 0).setScale(0.375F).build().getMatrix());
        ImmutableMap<ItemCameraTransforms.TransformType, Matrix4f> CAMERA_TRANSFORMATIONS = builder.build();
        return Pair.of(this, CAMERA_TRANSFORMATIONS.get(cameraTransformType));
    }
}
