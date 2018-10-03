package com.mrcrayfish.vehicle.client.model.baked;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.block.BlockBoostRamp;
import com.mrcrayfish.vehicle.block.BlockRotatedObject;
import com.mrcrayfish.vehicle.block.BlockSteepBoostRamp;
import com.mrcrayfish.vehicle.client.util.BakedQuadBuilder;
import com.mrcrayfish.vehicle.client.util.TransformationBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
public class BakedModelSteepRamp implements IBakedModel
{
    private static final ImmutableMap<ItemCameraTransforms.TransformType, Matrix4f> CAMERA_TRANSFORMATIONS;

    static
    {
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, Matrix4f> builder = ImmutableMap.builder();
        builder.put(ItemCameraTransforms.TransformType.FIXED, new TransformationBuilder().setScale(0.5F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.GUI, new TransformationBuilder().setTranslation(0.1F, 0, 0).setRotation(30.0F, 45.0F, 0).setScale(0.625F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.GROUND, new TransformationBuilder().setTranslation(0, 1, 0).setScale(0.25F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, new TransformationBuilder().setTranslation(0, 4, 0).setRotation(0, -45, 0).setScale(0.4F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, new TransformationBuilder().setTranslation(0, 4, 0).setRotation(0, 135, 0).setScale(0.4F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, new TransformationBuilder().setTranslation(0, 2.5F, 3.5F).setRotation(75, 315, 0).setScale(0.375F).build().getMatrix());
        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, new TransformationBuilder().setTranslation(0, 2.5F, 3.5F).setRotation(75, 135, 0).setScale(0.375F).build().getMatrix());
        CAMERA_TRANSFORMATIONS = builder.build();
    }

    private VertexFormat format;
    private TextureAtlasSprite mainTexture;
    private TextureAtlasSprite rampTexture;

    public BakedModelSteepRamp(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        this.format = format;
        this.mainTexture = bakedTextureGetter.apply(new ResourceLocation("minecraft", "blocks/concrete_gray"));
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

            EnumFacing facing = EnumFacing.NORTH;
            float startHeight = 0.0F;
            float endHeight = 1.0F;
            boolean left = false;
            boolean right = false;

            if(state != null)
            {
                if(state.getPropertyKeys().contains(BlockRotatedObject.FACING))
                {
                    facing = state.getValue(BlockRotatedObject.FACING);
                    builder.setFacing(facing);
                }
                left = state.getPropertyKeys().contains(BlockSteepBoostRamp.LEFT) && state.getValue(BlockSteepBoostRamp.LEFT);
                right = state.getPropertyKeys().contains(BlockSteepBoostRamp.RIGHT) && state.getValue(BlockSteepBoostRamp.RIGHT);
            }

            builder.setTexture(rampTexture);

            int offsetStart = left ? 0 : 1;
            int offsetEnd = right ? 16 : 15;

            //Boost Pad
            builder.put(new BakedQuadBuilder.VertexData(0, startHeight, offsetStart * 0.0625F, offsetStart, 0), EnumFacing.UP);
            builder.put(new BakedQuadBuilder.VertexData(0, startHeight, offsetEnd * 0.0625F, offsetEnd, 0), EnumFacing.UP);
            builder.put(new BakedQuadBuilder.VertexData(1, endHeight, offsetEnd * 0.0625F, offsetEnd, 16), EnumFacing.UP);
            builder.put(new BakedQuadBuilder.VertexData(1, endHeight, offsetStart * 0.0625F, offsetStart, 16), EnumFacing.UP);
            quads.add(builder.build());

            builder.setTexture(mainTexture);

            if(!left)
            {
                //Left Trim
                builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 0, 0, 0), EnumFacing.UP);
                builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 1 * 0.0625F, 1, 0), EnumFacing.UP);
                builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 1 * 0.0625F, 1, 16), EnumFacing.UP);
                builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 0, 0, 16), EnumFacing.UP);
                quads.add(builder.build());
            }

            if(!right)
            {
                //Right Trim
                builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 15 * 0.0625F, 15, 0), EnumFacing.UP);
                builder.put(new BakedQuadBuilder.VertexData(0, startHeight, 1, 16, 0), EnumFacing.UP);
                builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 1, 16, 16), EnumFacing.UP);
                builder.put(new BakedQuadBuilder.VertexData(1, endHeight, 15 * 0.0625F, 15, 16), EnumFacing.UP);
                quads.add(builder.build());
            }

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
        return mainTexture;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return new ItemOverrideList(Lists.newArrayList());
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        return Pair.of(this, CAMERA_TRANSFORMATIONS.get(cameraTransformType));
    }
}
