package com.mrcrayfish.vehicle.entity;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public abstract class ExtendedProperties
{
    private static final Map<Class<? extends ExtendedProperties>, ResourceLocation> CLASS_TO_ID = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, Supplier<? extends ExtendedProperties>> SUPPLIER_MAP = new ConcurrentHashMap<>();

    public static <T extends ExtendedProperties> void register(ResourceLocation id, Class<T> clazz, Supplier<T> supplier)
    {
        CLASS_TO_ID.putIfAbsent(clazz, id);
        SUPPLIER_MAP.putIfAbsent(id, supplier);
    }

    public static Optional<ResourceLocation> getId(Class<? extends ExtendedProperties> clazz)
    {
        return Optional.ofNullable(CLASS_TO_ID.get(clazz));
    }

    public static Optional<? extends ExtendedProperties> fromId(ResourceLocation id)
    {
        Supplier<? extends ExtendedProperties> supplier = SUPPLIER_MAP.get(id);
        if(supplier != null)
        {
            return Optional.of(supplier.get());
        }
        return Optional.empty();
    }

    public ResourceLocation getId()
    {
        return CLASS_TO_ID.get(this.getClass());
    }

    public abstract void serialize(JsonObject object);

    public abstract void deserialize(JsonObject object);
}
