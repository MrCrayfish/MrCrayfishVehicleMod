package com.mrcrayfish.vehicle.util;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

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
}
