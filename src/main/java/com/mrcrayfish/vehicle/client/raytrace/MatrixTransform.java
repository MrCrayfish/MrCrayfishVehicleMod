package com.mrcrayfish.vehicle.client.raytrace;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;

/**
 * Matrix transformation that corresponds to one of the three supported GL operations that might be performed on a rendered item part
 */
public abstract class MatrixTransform
{
    public static MatrixTransform translate(float x, float y, float z)
    {
        return new Translate(x, y, z);
    }

    public static MatrixTransform rotate(Quaternion quaternion)
    {
        return new Rotation(quaternion);
    }

    public static MatrixTransform scale(float x, float y, float z)
    {
        return new Scale(x, y, z);
    }

    public static MatrixTransform scale(float scale)
    {
        return new Scale(scale);
    }

    /**
     * Applies the matrix transformation that this class represents to the passed matrix
     *
     * @param matrix matrix to construct this transformation to
     */
    public abstract void transform(Matrix4f matrix);

    public static class Translate extends MatrixTransform
    {
        private final float x, y, z;

        public Translate(float x, float y, float z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void transform(Matrix4f matrix)
        {
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.translate(this.x, this.y, this.z);
            matrix.multiply(matrixStack.last().pose());
        }
    }

    public static class Rotation extends MatrixTransform
    {
        private final Quaternion quaternion;

        public Rotation(Quaternion quaternion)
        {
            this.quaternion = quaternion;
        }

        @Override
        public void transform(Matrix4f matrix)
        {
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.mulPose(this.quaternion);
            matrix.multiply(matrixStack.last().pose());
        }
    }

    public static class Scale extends MatrixTransform
    {
        private final float x, y, z;

        public Scale(float x, float y, float z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Scale(float s)
        {
            this.x = s;
            this.y = s;
            this.z = s;
        }

        @Override
        public void transform(Matrix4f matrix)
        {
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.scale(this.x, this.y, this.z);
            matrix.multiply(matrixStack.last().pose());
        }
    }
}
