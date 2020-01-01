package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.trailer.*;
import com.mrcrayfish.vehicle.entity.vehicle.*;
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
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
@ObjectHolder(Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities
{
    /* Motor Vehicles */
    public static final EntityType<ATVEntity> ATV = null;
    public static final EntityType<DuneBuggyEntity> DUNE_BUGGY = null;
    public static final EntityType<GoKartEntity> GO_KART = null;
    public static final EntityType<ShoppingCartEntity> SHOPPING_CART = null;
    public static final EntityType<MiniBikeEntity> MINI_BIKE = null;
    public static final EntityType<BumperCarEntity> BUMPER_CAR = null;
    public static final EntityType<JetSkiEntity> JET_SKI = null;
    public static final EntityType<SpeedBoatEntity> SPEED_BOAT = null;
    public static final EntityType<AluminumBoatEntity> ALUMINUM_BOAT = null;
    public static final EntityType<SmartCarEntity> SMART_CAR = null;
    public static final EntityType<LawnMowerEntity> LAWN_MOWER = null;
    public static final EntityType<MopedEntity> MOPED = null;
    public static final EntityType<SportsPlaneEntity> SPORTS_PLANE = null;
    public static final EntityType<GolfCartEntity> GOLF_CART = null;
    public static final EntityType<OffRoaderEntity> OFF_ROADER = null;
    public static final EntityType<TractorEntity> TRACTOR = null;

    /* Trailers */
    public static final EntityType<VehicleEntityTrailer> VEHICLE_TRAILER = null;
    public static final EntityType<StorageTrailerEntity> STORAGE_TRAILER = null;
    public static final EntityType<FluidTrailerEntity> FLUID_TRAILER = null;
    public static final EntityType<SeederTrailerEntity> SEEDER = null;
    public static final EntityType<FertilizerTrailerEntity> FERTILIZER = null;

    /* Special Vehicles */
    public static final EntityType<CouchEntity> SOFA = null;
    public static final EntityType<BathEntity> BATH = null;
    public static final EntityType<SofacopterEntity> SOFACOPTER = null;

    /* Other */
    public static final EntityType<EntityJack> JACK = null;

    private static <T extends Entity> EntityType<T> buildVehicleType(String id, Function<World, T> function, float width, float height)
    {
        EntityType<T> type = EntityType.Builder.<T>create((entityType, world) -> function.apply(world), EntityClassification.MISC).size(width, height).setTrackingRange(256).setUpdateInterval(1).disableSummoning().immuneToFire().setShouldReceiveVelocityUpdates(true).setCustomClientFactory((spawnEntity, world) -> function.apply(world)).build(id);
        type.setRegistryName(id);
        BlockVehicleCrate.registerVehicle(id);
        return type;
    }

    private static <T extends Entity> EntityType<T> buildType(String id, Function<World, T> function, float width, float height)
    {
        EntityType<T> type = EntityType.Builder.<T>create((entityType, world) -> function.apply(world), EntityClassification.MISC).size(width, height).setTrackingRange(256).setUpdateInterval(1).disableSummoning().immuneToFire().setShouldReceiveVelocityUpdates(true).setCustomClientFactory((spawnEntity, world) -> function.apply(world)).build(id);
        type.setRegistryName(id);
        return type;
    }
    
    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void registerTypes(final RegistryEvent.Register<EntityType<?>> event)
    {
        IForgeRegistry<EntityType<?>> registry = event.getRegistry();

        registry.register(buildVehicleType(Names.Entity.ATV, ATVEntity::new, 1.5F, 1.0F));
        registry.register(buildVehicleType(Names.Entity.DUNE_BUGGY, DuneBuggyEntity::new, 0.75F, 0.75F));
        registry.register(buildVehicleType(Names.Entity.GO_KART, GoKartEntity::new, 1.5F, 0.5F));
        registry.register(buildVehicleType(Names.Entity.SHOPPING_CART, ShoppingCartEntity::new, 1.0F, 1.0F));
        registry.register(buildVehicleType(Names.Entity.MINI_BIKE, MiniBikeEntity::new, 1.0F, 1.0F));
        registry.register(buildVehicleType(Names.Entity.BUMPER_CAR, BumperCarEntity::new, 1.5F, 1.0F));
        registry.register(buildVehicleType(Names.Entity.JET_SKI, JetSkiEntity::new, 1.5F, 1.0F));
        registry.register(buildVehicleType(Names.Entity.SPEED_BOAT, SpeedBoatEntity::new, 1.5F, 1.0F));
        registry.register(buildVehicleType(Names.Entity.ALUMINUM_BOAT, AluminumBoatEntity::new, 2.25F, 0.875F));
        registry.register(buildVehicleType(Names.Entity.SMART_CAR, SmartCarEntity::new, 1.85F, 1.15F));
        registry.register(buildVehicleType(Names.Entity.LAWN_MOWER, LawnMowerEntity::new, 1.2F, 1.0F));
        registry.register(buildVehicleType(Names.Entity.MOPED, MopedEntity::new, 1.0F, 1.0F));
        registry.register(buildVehicleType(Names.Entity.SPORTS_PLANE, SportsPlaneEntity::new, 3.0F, 1.6875F));
        registry.register(buildVehicleType(Names.Entity.GO_KART, GolfCartEntity::new, 2.0F, 1.0F));
        registry.register(buildVehicleType(Names.Entity.OFF_ROADER, OffRoaderEntity::new, 2.0F, 1.0F));
        registry.register(buildVehicleType(Names.Entity.TRACTOR, TractorEntity::new, 1.5F, 1.5F));

        registry.register(buildVehicleType(Names.Entity.VEHICLE_TRAILER, VehicleEntityTrailer::new, 1.5F, 1.5F));
        registry.register(buildVehicleType(Names.Entity.STORAGE_TRAILER, StorageTrailerEntity::new, 1.0F, 1.0F));
        registry.register(buildVehicleType(Names.Entity.FLUID_TRAILER, FluidTrailerEntity::new, 1.5F, 1.5F));
        registry.register(buildVehicleType(Names.Entity.SEEDER, SeederTrailerEntity::new, 1.5F, 1.5F));
        registry.register(buildVehicleType(Names.Entity.FERTILIZER, FertilizerTrailerEntity::new, 1.5F, 1.5F));

        if(ModList.get().isLoaded("cfm"))
        {
            registry.register(buildVehicleType(Names.Entity.COUCH, CouchEntity::new, 1.0F, 1.0F));
            registry.register(buildVehicleType(Names.Entity.BATH, BathEntity::new, 1.0F, 1.0F));
            registry.register(buildVehicleType(Names.Entity.SOFACOPTER, SofacopterEntity::new, 1.0F, 1.0F));
        }

        registry.register(buildType(Names.Entity.JACK, EntityJack::new, 0.0F, 0.0F));
    }
}
