package com.mrcrayfish.vehicle.client.model;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.client.render.complex.ComplexModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ComponentManager
{
    private static final Map<String, ComponentLoader> LOADERS = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, ComponentModel> ALL_MODELS = new HashMap<>();

    public static void registerLoader(ComponentLoader loader)
    {
        if(!LOADERS.containsKey(loader.getModId()))
        {
            LOADERS.put(loader.getModId(), loader);
            loader.getModels().forEach(model -> ALL_MODELS.put(model.getModelLocation(), model));
        }
    }

    @SubscribeEvent
    public static void setupModels(ModelRegistryEvent event)
    {
        LOADERS.forEach((modId, loader) ->
        {
            loader.getModels().forEach(model ->
            {
                ModelLoader.addSpecialModel(model.getModelLocation());
            });
        });
    }

    @SubscribeEvent
    public static void onBakeEvent(ModelBakeEvent event)
    {
        LOADERS.forEach((modId, loader) ->
        {
            loader.getModels().forEach(model ->
            {
                model.setComplexModel(ComplexModel.load(model));
            });
        });
    }

    public static void clearCache()
    {
        LOADERS.forEach((modId, loader) -> loader.getModels().forEach(ComponentModel::clearCache));
    }

    @Nullable
    public static ComponentModel lookupModel(ResourceLocation location)
    {
        return ALL_MODELS.get(location);
    }
}
