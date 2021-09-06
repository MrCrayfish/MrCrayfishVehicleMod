package com.mrcrayfish.vehicle.client.raytrace;

import com.mrcrayfish.vehicle.client.raytrace.data.RayTraceData;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
class InterceptResult
{
    private static final float EPSILON = 0.000001F;
    private final Vector3d hitPos;
    private final RayTraceData part;
    private final double distance;

    public InterceptResult(RayTraceData part, float x, float y, float z, Vector3d eyePos)
    {
        this.part = part;
        this.hitPos = new Vector3d(x, y, z);
        this.distance = eyePos.distanceTo(this.hitPos);
    }

    public Vector3d getHitPos()
    {
        return this.hitPos;
    }

    public RayTraceData getPart()
    {
        return this.part;
    }

    public double getDistance()
    {
        return this.distance;
    }

    /**
     * Raytrace a triangle using a Möller/Trumbore intersection algorithm
     *
     * @param entityPos position of the entity being raytraced
     * @param eyePos      position of the eyes of the player
     * @param direction normalized direction vector scaled by reach distance that represents the player's looking direction
     * @param data      triangle data of a part of the entity being raytraced
     * @param part      raytrace part
     * @return new instance of this class, if the ray intersect the triangle - null if the ray does not
     */
    @Nullable
    public static InterceptResult calculate(Vector3d entityPos, Vector3d eyePos, float[] direction, float[] data, RayTraceData part)
    {
        float[] eyes = new float[]{(float) eyePos.x, (float) eyePos.y, (float) eyePos.z};
        float[] vec0 = {data[0] + (float) entityPos.x, data[1] + (float) entityPos.y, data[2] + (float) entityPos.z};
        float[] vec1 = {data[3] + (float) entityPos.x, data[4] + (float) entityPos.y, data[5] + (float) entityPos.z};
        float[] vec2 = {data[6] + (float) entityPos.x, data[7] + (float) entityPos.y, data[8] + (float) entityPos.z};
        float[] edge1 = new float[3];
        float[] edge2 = new float[3];
        float[] tvec = new float[3];
        float[] pvec = new float[3];
        float[] qvec = new float[3];
        float det;
        float inv_det;
        subtract(edge1, vec1, vec0);
        subtract(edge2, vec2, vec0);
        crossProduct(pvec, direction, edge2);
        det = dotProduct(edge1, pvec);
        if(det <= -EPSILON || det >= EPSILON)
        {
            inv_det = 1f / det;
            subtract(tvec, eyes, vec0);
            float u = dotProduct(tvec, pvec) * inv_det;
            if(u >= 0 && u <= 1)
            {
                crossProduct(qvec, tvec, edge1);
                float v = dotProduct(direction, qvec) * inv_det;
                if(v >= 0 && u + v <= 1 && inv_det * dotProduct(edge2, qvec) > EPSILON)
                {
                    float x = edge1[0] * u + edge2[0] * v + vec0[0];
                    float y = edge1[1] * u + edge2[1] * v + vec0[1];
                    float z = edge1[2] * u + edge2[2] * v + vec0[2];
                    return new InterceptResult(part, x, y, z, eyePos);
                }
            }
        }
        return null;
    }

    private static void crossProduct(float[] result, float[] v1, float[] v2)
    {
        result[0] = v1[1] * v2[2] - v1[2] * v2[1];
        result[1] = v1[2] * v2[0] - v1[0] * v2[2];
        result[2] = v1[0] * v2[1] - v1[1] * v2[0];
    }

    private static float dotProduct(float[] v1, float[] v2)
    {
        return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
    }

    private static void subtract(float[] result, float[] v1, float[] v2)
    {
        result[0] = v1[0] - v2[0];
        result[1] = v1[1] - v2[1];
        result[2] = v1[2] - v2[2];
    }
}
