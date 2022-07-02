package com.mrcrayfish.vehicle.client.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;

public class MathUtil
{
    /**
     * Spherically interpolates between two quaternions with a weight
     * Source taken from Bones framework for JPCT, see <a href="https://github.com/raftAtGit/Bones">...</a>
     * Code has been adapted to work with Quaternion from Minecraft's math package.
     *
     * @param start the starting quaternion
     * @param end the destination quaternion
     * @param t the weight of the interpolation in the range of [0, 1]
     */
    public static Quaternion slerp(Quaternion start, Quaternion end, float t)
    {
        // Skip operation if equal
        if(start.equals(end))
        {
            return start;
        }

        float dot = start.i() * end.i() + start.j() * end.j() + start.k() * end.k() + start.r() * end.r();
        if(dot < 0.0F)
        {
            end = new Quaternion(-end.i(), -end.j(), -end.k(), -end.r());
            dot = -dot;
        }

        float scale0 = 1 - t;
        float scale1 = t;

        // Only run calculations if angle between two quaternions is big enough.
        if((1.0F - dot) > 0.1F)
        {
            float theta = (float) Math.acos(dot);
            float invSinTheta = 1.0F / MathHelper.sin(theta);
            scale0 = MathHelper.sin((1.0F - t) * theta) * invSinTheta;
            scale1 = MathHelper.sin((t * theta)) * invSinTheta;
        }

        // Calculate new quaternion. Interpolation is linear unless above calculations are run.
        float i = (scale0 * start.i()) + (scale1 * end.i());
        float j = (scale0 * start.j()) + (scale1 * end.j());
        float k = (scale0 * start.k()) + (scale1 * end.k());
        float r = (scale0 * start.r()) + (scale1 * end.r());
        return new Quaternion(i, j, k, r);
    }
}
