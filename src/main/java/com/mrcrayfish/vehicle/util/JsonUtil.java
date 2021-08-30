package com.mrcrayfish.vehicle.util;

import com.google.gson.JsonObject;

/**
 * Author: MrCrayfish
 */
public class JsonUtil
{
    public static void write(JsonObject object, String key, Number number, Number defaultValue)
    {
        if(!number.equals(defaultValue))
        {
            object.addProperty(key, number);
        }
    }
}
