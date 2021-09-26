package com.mrcrayfish.vehicle.client.raytrace;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix4f;

/**
 * Author: MrCrayfish
 */
public class Triangle
{
    private final float[] data;

    public Triangle(float[] data)
    {
        this.data = data;
    }

    public float[] getVertices()
    {
        return this.data;
    }

    public void draw(MatrixStack matrixStack, IVertexBuilder builder, float red, float green, float blue, float alpha)
    {
        Matrix4f matrix = matrixStack.last().pose();
        builder.vertex(matrix, this.data[6], this.data[7], this.data[8]).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, this.data[0], this.data[1], this.data[2]).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, this.data[0], this.data[1], this.data[2]).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, this.data[3], this.data[4], this.data[5]).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, this.data[3], this.data[4], this.data[5]).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, this.data[6], this.data[7], this.data[8]).color(red, green, blue, alpha).endVertex();
    }
}
