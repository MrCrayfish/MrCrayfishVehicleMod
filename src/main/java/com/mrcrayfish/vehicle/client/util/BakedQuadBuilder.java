package com.mrcrayfish.vehicle.client.util;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

/**
 * Author: MrCrayfish
 */
public class BakedQuadBuilder
{
    private UnpackedBakedQuad.Builder builder;
    private EnumFacing facing = null;
    private TextureAtlasSprite texture;

    public BakedQuadBuilder(VertexFormat format)
    {
        this.builder = new UnpackedBakedQuad.Builder(format);
    }

    public void setFacing(EnumFacing facing)
    {
        this.facing = facing;
        this.builder.setQuadOrientation(facing);
    }

    public EnumFacing getFacing()
    {
        return facing;
    }

    public void setTexture(TextureAtlasSprite texture)
    {
        this.texture = texture;
        this.builder.setTexture(texture);
    }

    public BakedQuad build()
    {
        BakedQuad quad = builder.build();
        builder = new UnpackedBakedQuad.Builder(builder.getVertexFormat());
        builder.setQuadOrientation(facing);
        builder.setTexture(texture);
        return quad;
    }

    public BakedQuadBuilder put(VertexData data, EnumFacing side)
    {
        data = rotate(data);
        VertexFormat format = builder.getVertexFormat();
        for(int e = 0; e < builder.getVertexFormat().getElementCount(); e++)
        {
            switch(format.getElement(e).getUsage())
            {
                case POSITION:
                    builder.put(e, data.x, data.y, data.z, 1.0F);
                    break;
                case COLOR:
                    builder.put(e, 1f, 1f, 1f, 1f);
                    break;
                case UV: if (format.getElement(e).getIndex() == 0)
                {
                    float u = texture.getInterpolatedU(data.u);
                    float v = texture.getInterpolatedV(data.v);
                    builder.put(e, u, v, 0.0F, 1.0F);
                    break;
                }
                case NORMAL:
                    builder.put(e, (float) side.getFrontOffsetX(), (float) side.getFrontOffsetY(), (float) side.getFrontOffsetZ(), 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
        return this;
    }

    public VertexData rotate(VertexData data)
    {
        if(facing != null)
        {
            switch(facing)
            {
                case WEST:
                    data.x = 1.0F - data.x;
                    data.z = 1.0F - data.z;
                    break;
                case NORTH:
                    data.x = 1.0F - data.x;
                    float oldX = data.x;
                    data.x = data.z;
                    data.z = oldX;
                    break;
                case SOUTH:
                    data.z = 1.0F - data.z;
                    float oldZ = data.z;
                    data.z = data.x;
                    data.x = oldZ;
                    break;
                default:
                    break;
            }
        }
        return data;
    }

    public static class VertexData
    {
        private float x, y, z;
        private float u, v;

        public VertexData(float x, float y, float z, float u, float v)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.u = u;
            this.v = v;
        }
    }
}
