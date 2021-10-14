package com.mrcrayfish.vehicle.util;

/**
 * Author: MrCrayfish
 */
public class EasingHelper
{
    public static double easeInOutBack(double x)
    {
        double c1 = 1.70158;
        double c2 = c1 * 1.525;
        return x < 0.5 ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2 : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    }

    public static double easeOutBack(double x)
    {
        double c1 = 1.70158;
        double c3 = c1 + 1;
        return 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2);
    }
}
