package com.mrcrayfish.vehicle.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class CollisionHelper
{
    public static AxisAlignedBB getBlockBounds(EnumFacing facing, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double[] bounds = fixRotation(facing, x1, z1, x2, z2);
        return new AxisAlignedBB(bounds[0], y1, bounds[1], bounds[2], y2, bounds[3]);
    }

    public static AxisAlignedBB getBlockBounds(EnumFacing facing, Bounds bounds)
    {
        double[] fixedBounds = fixRotation(facing, bounds.x1, bounds.z1, bounds.x2, bounds.z2);
        return new AxisAlignedBB(fixedBounds[0], bounds.y1, fixedBounds[1], fixedBounds[2], bounds.y2, fixedBounds[3]);
    }

    public static double[] fixRotation(EnumFacing facing, double x1, double z1, double x2, double z2)
    {
        switch(facing)
        {
            case WEST:
            {
                double origX1 = x1;
                x1 = 1.0F - x2;
                double origZ1 = z1;
                z1 = 1.0F - z2;
                x2 = 1.0F - origX1;
                z2 = 1.0F - origZ1;
                break;
            }
            case NORTH:
            {
                double origX1 = x1;
                x1 = z1;
                z1 = 1.0F - x2;
                x2 = z2;
                z2 = 1.0F - origX1;
                break;
            }
            case SOUTH:
            {
                double origX1 = x1;
                x1 = 1.0F - z2;
                double origZ1 = z1;
                z1 = origX1;
                double origX2 = x2;
                x2 = 1.0F - origZ1;
                z2 = origX2;
                break;
            }
            default:
                break;
        }
        return new double[] { x1, z1, x2, z2 };
    }
}
