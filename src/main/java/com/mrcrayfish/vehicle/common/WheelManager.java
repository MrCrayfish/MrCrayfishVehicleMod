package com.mrcrayfish.vehicle.common;

/**
 * Author: MrCrayfish
 */
public class WheelManager
{
    private static WheelManager instance;

    public static WheelManager instance()
    {
        if(instance == null)
        {
            instance = new WheelManager();
        }
        return instance;
    }

    private WheelManager() {}


}
