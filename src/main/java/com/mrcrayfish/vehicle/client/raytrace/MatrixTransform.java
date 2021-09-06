package com.mrcrayfish.vehicle.client.raytrace;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

/**
 * Matrix transformation that corresponds to one of the three supported GL operations that might be performed on a rendered item part
 */
public class MatrixTransform
{
    private final Type type;
    private final float x, y, z;
    private final float angle;

    private MatrixTransform(Type type, float x, float y, float z)
    {
        this(type, x, y, z, 0F);
    }

    private MatrixTransform(Type type, float x, float y, float z, float angle)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = angle;
    }

    public static MatrixTransform translate(float x, float y, float z)
    {
        return new MatrixTransform(Type.TRANSLATION, x, y, z);
    }

    public static MatrixTransform rotate(Vector3f axis, float angle)
    {
        return new MatrixTransform(Type.ROTATION, axis.x(), axis.y(), axis.z(), angle);
    }

    public static MatrixTransform scale(float x, float y, float z)
    {
        return new MatrixTransform(Type.SCALE, x, y, z);
    }

    public static MatrixTransform scale(float scale)
    {
        return new MatrixTransform(Type.SCALE, scale, scale, scale);
    }

    /**
     * Applies the matrix transformation that this class represents to the passed matrix
     *
     * @param matrix matrix to construct this transformation to
     */
    public void transform(Matrix4f matrix)
    {
        MatrixStack matrixStack = new MatrixStack();
        switch(this.type)
        {
            case ROTATION:
                matrixStack.mulPose(new Vector3f(this.x, this.y, this.z).rotationDegrees(this.angle));
                break;
            case TRANSLATION:
                matrixStack.translate(this.x, this.y, this.z);
                break;
            case SCALE:
                matrixStack.scale(this.x, this.y, this.z);
                break;
        }
        matrix.multiply(matrixStack.last().pose());
    }

    private enum Type
    {
        TRANSLATION,
        ROTATION,
        SCALE
    }
}
