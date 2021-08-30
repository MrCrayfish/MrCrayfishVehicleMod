package com.mrcrayfish.vehicle.entity.properties;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public abstract class ExtendedProperties
{
    private static final Map<Class<? extends ExtendedProperties>, ResourceLocation> CLASS_TO_ID = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, Function<JsonObject, ? extends ExtendedProperties>> FACTORY = new ConcurrentHashMap<>();

    public static <T extends ExtendedProperties> void register(ResourceLocation id, Class<T> clazz, Function<JsonObject, T> supplier)
    {
        CLASS_TO_ID.putIfAbsent(clazz, id);
        FACTORY.putIfAbsent(id, supplier);
    }

    public static Optional<ResourceLocation> getId(Class<? extends ExtendedProperties> clazz)
    {
        return Optional.ofNullable(CLASS_TO_ID.get(clazz));
    }

    public static Optional<? extends ExtendedProperties> create(ResourceLocation id, JsonObject object)
    {
        Function<JsonObject, ? extends ExtendedProperties> factory = FACTORY.get(id);
        if(factory != null)
        {
            return Optional.of(factory.apply(object));
        }
        return Optional.empty();
    }

    public ResourceLocation getId()
    {
        return CLASS_TO_ID.get(this.getClass());
    }

    public abstract void serialize(JsonObject object);
}
