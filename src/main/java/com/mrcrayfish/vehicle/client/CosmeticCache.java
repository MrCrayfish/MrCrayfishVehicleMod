package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.client.raytrace.data.CosmeticRayTraceData;
import com.mrcrayfish.vehicle.client.raytrace.data.RayTraceData;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: MrCrayfish
 */
public class CosmeticCache
{
    private static CosmeticCache instance;

    public static CosmeticCache instance()
    {
        if(instance == null)
        {
            instance = new CosmeticCache();
        }
        return instance;
    }

    private final Map<EntityType<?>, Map<ResourceLocation, RayTraceData>> cacheMap = new ConcurrentHashMap<>();

    private CosmeticCache() {}

    public <T extends VehicleEntity> List<RayTraceData> getDataForVehicle(T vehicle)
    {
        if(vehicle.getProperties().getCosmetics().isEmpty())
            return Collections.emptyList();

        this.cacheCosmetics(vehicle);
        List<RayTraceData> list = new ArrayList<>();
        //TODO get current cosmetics instead of this
        vehicle.getProperties().getCosmetics().forEach((cosmeticId, cosmeticProperties) ->
        {
            ResourceLocation modelLocation = cosmeticProperties.getModelLocations().get(0);
            RayTraceData data = this.getModelRayTraceDataMap(vehicle).get(modelLocation);
            if(data != null)
            {
                list.add(data);
            }
        });
        return list;
    }

    private <T extends VehicleEntity> Map<ResourceLocation, RayTraceData> getModelRayTraceDataMap(T vehicle)
    {
        return this.cacheMap.computeIfAbsent(vehicle.getType(), k -> new HashMap<>());
    }

    private <T extends VehicleEntity> void cacheCosmetics(T vehicle)
    {
        Map<ResourceLocation, RayTraceData> cosmeticData = this.getModelRayTraceDataMap(vehicle);
        vehicle.getProperties().getCosmetics().forEach((cosmeticId, cosmeticProperties) ->
        {
            cosmeticProperties.getModelLocations().forEach(modelLocation ->
            {
                cosmeticData.computeIfAbsent(modelLocation, location ->
                        new CosmeticRayTraceData(cosmeticId, location, cosmeticProperties.getOffset().scale(0.0625)));
            });
        });
    }

    @SubscribeEvent
    public void onClientTick(InputEvent.KeyInputEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null)
            return;

        if(!FMLLoader.isProduction() && event.getKey() == GLFW.GLFW_KEY_MINUS && (event.getModifiers() & GLFW.GLFW_MOD_CONTROL) != GLFW.GLFW_FALSE)
            this.cacheMap.clear();
    }
}
