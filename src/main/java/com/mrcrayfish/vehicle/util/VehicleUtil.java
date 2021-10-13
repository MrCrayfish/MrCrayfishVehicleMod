package com.mrcrayfish.vehicle.util;

import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.client.CosmeticCache;
import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.client.raytrace.data.RayTraceData;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.EntityVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class VehicleUtil
{
    public static <T extends VehicleEntity> RegistryObject<EntityType<T>> createEntityType(DeferredRegister<EntityType<?>> deferredRegister, String name, BiFunction<EntityType<T>, World, T> function, float width, float height)
    {
        return createEntityType(deferredRegister, name, function, width, height, true);
    }

    public static <T extends VehicleEntity> RegistryObject<EntityType<T>> createEntityType(DeferredRegister<EntityType<?>> deferredRegister, String name, BiFunction<EntityType<T>, World, T> function, float width, float height, boolean includeCrate)
    {
        String modId = ObfuscationReflectionHelper.getPrivateValue(DeferredRegister.class, deferredRegister, "modid");
        ResourceLocation id = new ResourceLocation(modId, name);
        EntityType<T> type = VehicleUtil.buildVehicleType(id, function, width, height);
        VehicleRegistry.registerVehicleType(type);
        if(includeCrate) VehicleCrateBlock.registerVehicle(id);
        return deferredRegister.register(name, () -> type);
    }

    @Nullable
    public static <T extends VehicleEntity> RegistryObject<EntityType<T>> createModDependentEntityType(DeferredRegister<EntityType<?>> deferredRegister, String modId, String id, BiFunction<EntityType<T>, World, T> function, float width, float height, boolean registerCrate)
    {
        if(ModList.get().isLoaded(modId))
        {
            return createEntityType(deferredRegister, id, function, width, height, registerCrate);
        }
        return null;
    }

    private static <T extends Entity> EntityType<T> buildVehicleType(ResourceLocation id, BiFunction<EntityType<T>, World, T> function, float width, float height)
    {
        return EntityType.Builder.of(function::apply, EntityClassification.MISC).sized(width, height).setTrackingRange(256).setUpdateInterval(1).fireImmune().setShouldReceiveVelocityUpdates(true).build(id.toString());
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends VehicleEntity> void registerVehicleRenderer(EntityType<T> type, BiFunction<EntityType<T>, VehicleProperties, AbstractVehicleRenderer<T>> rendererFunction)
    {
        VehicleProperties properties = VehicleProperties.get(type);
        AbstractVehicleRenderer<T> renderer = rendererFunction.apply(type, properties);
        RenderingRegistry.registerEntityRenderingHandler(type, manager -> new EntityVehicleRenderer<>(manager, renderer));
        VehicleRenderRegistry.registerVehicleRendererFunction(type, rendererFunction, renderer);
        EntityRayTracer.instance().registerTransforms(type, renderer::getRayTraceTransforms);
        EntityRayTracer.instance().registerDynamicRayTraceData(type, VehicleUtil::getCosmeticsRayTraceData);
    }

    private static <T extends VehicleEntity> List<RayTraceData> getCosmeticsRayTraceData(T vehicle)
    {
        return CosmeticCache.instance().getDataForVehicle(vehicle);
    }
}
