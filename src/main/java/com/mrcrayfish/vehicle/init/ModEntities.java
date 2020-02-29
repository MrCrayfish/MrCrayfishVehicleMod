package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.trailer.*;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.util.EntityUtil;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities
{
    private static final List<EntityType<?>> ENTITY_TYPES = new ArrayList<>();

    /* Motor Vehicles */
    public static final EntityType<ATVEntity> ATV = buildVehicleType(Names.Entity.ATV, ATVEntity::new, 1.5F, 1.0F);
    public static final EntityType<DuneBuggyEntity> DUNE_BUGGY = buildVehicleType(Names.Entity.DUNE_BUGGY, DuneBuggyEntity::new, 0.75F, 0.75F);
    public static final EntityType<GoKartEntity> GO_KART = buildVehicleType(Names.Entity.GO_KART, GoKartEntity::new, 1.5F, 0.5F);
    public static final EntityType<ShoppingCartEntity> SHOPPING_CART = buildVehicleType(Names.Entity.SHOPPING_CART, ShoppingCartEntity::new, 1.0F, 1.0F);
    public static final EntityType<MiniBikeEntity> MINI_BIKE = buildVehicleType(Names.Entity.MINI_BIKE, MiniBikeEntity::new, 1.0F, 1.0F);
    public static final EntityType<BumperCarEntity> BUMPER_CAR = buildVehicleType(Names.Entity.BUMPER_CAR, BumperCarEntity::new, 1.5F, 1.0F);
    public static final EntityType<JetSkiEntity> JET_SKI = buildVehicleType(Names.Entity.JET_SKI, JetSkiEntity::new, 1.5F, 1.0F);
    public static final EntityType<SpeedBoatEntity> SPEED_BOAT = buildVehicleType(Names.Entity.SPEED_BOAT, SpeedBoatEntity::new, 1.5F, 1.0F);
    public static final EntityType<AluminumBoatEntity> ALUMINUM_BOAT = buildVehicleType(Names.Entity.ALUMINUM_BOAT, AluminumBoatEntity::new, 2.25F, 0.875F);
    public static final EntityType<SmartCarEntity> SMART_CAR = buildVehicleType(Names.Entity.SMART_CAR, SmartCarEntity::new, 1.85F, 1.15F);
    public static final EntityType<LawnMowerEntity> LAWN_MOWER = buildVehicleType(Names.Entity.LAWN_MOWER, LawnMowerEntity::new, 1.2F, 1.0F);
    public static final EntityType<MopedEntity> MOPED = buildVehicleType(Names.Entity.MOPED, MopedEntity::new, 1.0F, 1.0F);
    public static final EntityType<SportsPlaneEntity> SPORTS_PLANE = buildVehicleType(Names.Entity.SPORTS_PLANE, SportsPlaneEntity::new, 3.0F, 1.6875F);
    public static final EntityType<GolfCartEntity> GOLF_CART = buildVehicleType(Names.Entity.GOLF_CART, GolfCartEntity::new, 2.0F, 1.0F);
    public static final EntityType<OffRoaderEntity> OFF_ROADER = buildVehicleType(Names.Entity.OFF_ROADER, OffRoaderEntity::new, 2.0F, 1.0F);
    public static final EntityType<TractorEntity> TRACTOR = buildVehicleType(Names.Entity.TRACTOR, TractorEntity::new, 1.5F, 1.5F);
    public static final EntityType<MiniBusEntity> MINI_BUS = buildVehicleType(Names.Entity.MINI_BUS, MiniBusEntity::new, 2.0F, 2.0F);

    /* Trailers */
    public static final EntityType<VehicleEntityTrailer> VEHICLE_TRAILER = buildVehicleType(Names.Entity.VEHICLE_TRAILER, VehicleEntityTrailer::new, 1.5F, 1.5F);
    public static final EntityType<StorageTrailerEntity> STORAGE_TRAILER = buildVehicleType(Names.Entity.STORAGE_TRAILER, StorageTrailerEntity::new, 1.0F, 1.0F);
    public static final EntityType<FluidTrailerEntity> FLUID_TRAILER = buildVehicleType(Names.Entity.FLUID_TRAILER, FluidTrailerEntity::new, 1.5F, 1.5F);
    public static final EntityType<SeederTrailerEntity> SEEDER = buildVehicleType(Names.Entity.SEEDER, SeederTrailerEntity::new, 1.5F, 1.5F);
    public static final EntityType<FertilizerTrailerEntity> FERTILIZER = buildVehicleType(Names.Entity.FERTILIZER, FertilizerTrailerEntity::new, 1.5F, 1.5F);

    /* Special Vehicles */
    public static final EntityType<CouchEntity> SOFA = buildDependentVehicleType("cfm", Names.Entity.SOFA, CouchEntity::new, 1.0F, 1.0F);
    public static final EntityType<BathEntity> BATH = buildDependentVehicleType("cfm", Names.Entity.BATH, BathEntity::new, 1.0F, 1.0F);
    public static final EntityType<SofacopterEntity> SOFACOPTER = buildDependentVehicleType("cfm", Names.Entity.SOFACOPTER, SofacopterEntity::new, 1.0F, 1.0F);

    /* Other */
    public static final EntityType<EntityJack> JACK = buildType(Names.Entity.JACK, EntityJack::new, 0.0F, 0.0F);

    private static <T extends Entity> EntityType<T> buildVehicleType(String id, Function<World, T> function, float width, float height)
    {
        EntityType<T> type = EntityUtil.buildVehicleType(id, function, width, height);
        ENTITY_TYPES.add(type);
        return type;
    }

    private static <T extends Entity> EntityType<T> buildDependentVehicleType(String modId, String id, Function<World, T> function, float width, float height)
    {
        if(ModList.get().isLoaded(modId))
        {
            EntityType<T> type = EntityType.Builder.<T>create((entityType, world) -> function.apply(world), EntityClassification.MISC).size(width, height).setTrackingRange(256).setUpdateInterval(1).immuneToFire().setShouldReceiveVelocityUpdates(true).setCustomClientFactory((spawnEntity, world) -> function.apply(world)).build(id);
            type.setRegistryName(id);
            ENTITY_TYPES.add(type);
            BlockVehicleCrate.registerVehicle(id);
            return type;
        }
        return null;
    }

    private static <T extends Entity> EntityType<T> buildType(String id, Function<World, T> function, float width, float height)
    {
        EntityType<T> type = EntityType.Builder.<T>create((entityType, world) -> function.apply(world), EntityClassification.MISC).size(width, height).setTrackingRange(256).setUpdateInterval(1).disableSummoning().immuneToFire().setShouldReceiveVelocityUpdates(true).setCustomClientFactory((spawnEntity, world) -> function.apply(world)).build(id);
        type.setRegistryName(id);
        ENTITY_TYPES.add(type);
        return type;
    }
    
    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void registerTypes(final RegistryEvent.Register<EntityType<?>> event)
    {
        IForgeRegistry<EntityType<?>> registry = event.getRegistry();
        ENTITY_TYPES.forEach(registry::register);
    }

    public static List<EntityType<?>> getEntityTypes()
    {
        return ENTITY_TYPES;
    }
}
