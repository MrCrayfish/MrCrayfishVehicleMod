package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.trailer.*;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Author: MrCrayfish
 */
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, Reference.MOD_ID);

    public static final RegistryObject<EntityType<ATVEntity>> ATV = registerVehicle("atv", ATVEntity::new, 1.5F, 1.0F);
    public static final RegistryObject<EntityType<DuneBuggyEntity>> DUNE_BUGGY = registerVehicle("dune_buggy", DuneBuggyEntity::new, 0.75F, 0.75F);
    public static final RegistryObject<EntityType<GoKartEntity>> GO_KART = registerVehicle("go_kart", GoKartEntity::new, 1.5F, 0.5F);
    public static final RegistryObject<EntityType<ShoppingCartEntity>> SHOPPING_CART = registerVehicle("shopping_cart", ShoppingCartEntity::new, 1.0F, 1.0F);
    public static final RegistryObject<EntityType<MiniBikeEntity>> MINI_BIKE = registerVehicle("mini_bike", MiniBikeEntity::new, 1.0F, 1.0F);
    public static final RegistryObject<EntityType<BumperCarEntity>> BUMPER_CAR = registerVehicle("bumper_car", BumperCarEntity::new, 1.5F, 1.0F);
    public static final RegistryObject<EntityType<JetSkiEntity>> JET_SKI = registerVehicle("jet_ski", JetSkiEntity::new, 1.5F, 1.0F);
    public static final RegistryObject<EntityType<SpeedBoatEntity>> SPEED_BOAT = registerVehicle("speed_boat", SpeedBoatEntity::new, 1.5F, 1.0F);
    public static final RegistryObject<EntityType<AluminumBoatEntity>> ALUMINUM_BOAT = registerVehicle("aluminum_boat", AluminumBoatEntity::new, 2.25F, 0.875F);
    public static final RegistryObject<EntityType<SmartCarEntity>> SMART_CAR = registerVehicle("smart_car", SmartCarEntity::new, 1.85F, 1.15F);
    public static final RegistryObject<EntityType<LawnMowerEntity>> LAWN_MOWER = registerVehicle("lawn_mower", LawnMowerEntity::new, 1.2F, 1.0F);
    public static final RegistryObject<EntityType<MopedEntity>> MOPED = registerVehicle("moped", MopedEntity::new, 1.0F, 1.0F);
    public static final RegistryObject<EntityType<SportsPlaneEntity>> SPORTS_PLANE = registerVehicle("sports_plane", SportsPlaneEntity::new, 3.0F, 1.6875F);
    public static final RegistryObject<EntityType<GolfCartEntity>> GOLF_CART = registerVehicle("golf_cart", GolfCartEntity::new, 2.0F, 1.0F);
    public static final RegistryObject<EntityType<OffRoaderEntity>> OFF_ROADER = registerVehicle("off_roader", OffRoaderEntity::new, 2.0F, 1.0F);
    public static final RegistryObject<EntityType<TractorEntity>> TRACTOR = registerVehicle("tractor", TractorEntity::new, 1.5F, 1.5F);
    public static final RegistryObject<EntityType<MiniBusEntity>> MINI_BUS = registerVehicle("mini_bus", MiniBusEntity::new, 2.0F, 2.0F);

    /* Trailers */
    public static final RegistryObject<EntityType<VehicleEntityTrailer>> VEHICLE_TRAILER = registerVehicle("vehicle_trailer", VehicleEntityTrailer::new, 1.5F, 1.5F);
    public static final RegistryObject<EntityType<StorageTrailerEntity>> STORAGE_TRAILER = registerVehicle("storage_trailer", StorageTrailerEntity::new, 1.0F, 1.0F);
    public static final RegistryObject<EntityType<FluidTrailerEntity>> FLUID_TRAILER = registerVehicle("fluid_trailer", FluidTrailerEntity::new, 1.5F, 1.5F);
    public static final RegistryObject<EntityType<SeederTrailerEntity>> SEEDER = registerVehicle("seeder", SeederTrailerEntity::new, 1.5F, 1.5F);
    public static final RegistryObject<EntityType<FertilizerTrailerEntity>> FERTILIZER = registerVehicle("fertilizer", FertilizerTrailerEntity::new, 1.5F, 1.5F);

    /* Special Vehicles */
    public static final RegistryObject<EntityType<CouchEntity>> SOFA = registerDependent("cfm", "couch", CouchEntity::new, 1.0F, 1.0F);
    public static final RegistryObject<EntityType<BathEntity>> BATH = registerDependent("cfm", "bath", BathEntity::new, 1.0F, 1.0F);
    public static final RegistryObject<EntityType<SofacopterEntity>> SOFACOPTER = registerDependent("cfm", "sofacopter", SofacopterEntity::new, 1.0F, 1.0F);

    /* Other */
    public static final RegistryObject<EntityType<EntityJack>> JACK = registerEntity("jack", EntityJack::new, 0.0F, 0.0F);

    private static <T extends Entity> RegistryObject<EntityType<T>> registerVehicle(String id, BiFunction<EntityType<T>, World, T> function, float width, float height)
    {
        EntityType<T> type = EntityUtil.buildVehicleType(new ResourceLocation(Reference.MOD_ID, id), function, width, height);
        return ModEntities.ENTITY_TYPES.register(id, () -> type);
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerDependent(String modId, String id, BiFunction<EntityType<T>, World, T> function, float width, float height)
    {
        if(ModList.get().isLoaded(modId))
        {
            EntityType<T> type = EntityUtil.buildVehicleType(new ResourceLocation(Reference.MOD_ID, id), function, width, height);
            return ModEntities.ENTITY_TYPES.register(id, () -> type);
        }
        return null;
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String id, BiFunction<EntityType<T>, World, T> function, float width, float height)
    {
        EntityType<T> type = EntityType.Builder.create(function::apply, EntityClassification.MISC).size(width, height).setTrackingRange(256).setUpdateInterval(1).disableSummoning().immuneToFire().setShouldReceiveVelocityUpdates(true).build(id);
        return ModEntities.ENTITY_TYPES.register(id, () -> type);
    }
}
