package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.trailer.FertilizerTrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.FluidTrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.SeederTrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.StorageTrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.VehicleTrailerEntity;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.util.VehicleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiFunction;

/**
 * Author: MrCrayfish
 */
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MOD_ID);

    public static final RegistryObject<EntityType<QuadBikeEntity>> QUAD_BIKE = VehicleUtil.createEntityType(REGISTER, "quad_bike", QuadBikeEntity::new, 1.5F, 1.0F);
    public static final RegistryObject<EntityType<SportsCarEntity>> SPORTS_CAR = VehicleUtil.createEntityType(REGISTER, "sports_car", SportsCarEntity::new, 1.5F, 1.0F);
    public static final RegistryObject<EntityType<GoKartEntity>> GO_KART = VehicleUtil.createEntityType(REGISTER, "go_kart", GoKartEntity::new, 1.5F, 0.5F);
    public static final RegistryObject<EntityType<JetSkiEntity>> JET_SKI = VehicleUtil.createEntityType(REGISTER, "jet_ski", JetSkiEntity::new, 1.5F, 1.0F);
    public static final RegistryObject<EntityType<LawnMowerEntity>> LAWN_MOWER = VehicleUtil.createEntityType(REGISTER, "lawn_mower", LawnMowerEntity::new, 1.2F, 1.0F);
    public static final RegistryObject<EntityType<MopedEntity>> MOPED = VehicleUtil.createEntityType(REGISTER, "moped", MopedEntity::new, 1.0F, 1.0F);
    public static final RegistryObject<EntityType<SportsPlaneEntity>> SPORTS_PLANE = VehicleUtil.createEntityType(REGISTER, "sports_plane", SportsPlaneEntity::new, 3.0F, 1.6875F);
    public static final RegistryObject<EntityType<GolfCartEntity>> GOLF_CART = VehicleUtil.createEntityType(REGISTER, "golf_cart", GolfCartEntity::new, 2.0F, 1.0F);
    public static final RegistryObject<EntityType<OffRoaderEntity>> OFF_ROADER = VehicleUtil.createEntityType(REGISTER, "off_roader", OffRoaderEntity::new, 2.0F, 1.0F);
    public static final RegistryObject<EntityType<TractorEntity>> TRACTOR = VehicleUtil.createEntityType(REGISTER, "tractor", TractorEntity::new, 1.5F, 1.5F);
    public static final RegistryObject<EntityType<MiniBusEntity>> MINI_BUS = VehicleUtil.createEntityType(REGISTER, "mini_bus", MiniBusEntity::new, 2.0F, 2.0F);
    public static final RegistryObject<EntityType<DirtBikeEntity>> DIRT_BIKE = VehicleUtil.createEntityType(REGISTER, "dirt_bike", DirtBikeEntity::new, 1.0F, 1.5F);

    /* Trailers */
    public static final RegistryObject<EntityType<VehicleTrailerEntity>> VEHICLE_TRAILER = VehicleUtil.createEntityType(REGISTER, "vehicle_trailer", VehicleTrailerEntity::new, 1.5F, 0.75F);
    public static final RegistryObject<EntityType<StorageTrailerEntity>> STORAGE_TRAILER = VehicleUtil.createEntityType(REGISTER, "storage_trailer", StorageTrailerEntity::new, 1.0F, 1.0F);
    public static final RegistryObject<EntityType<FluidTrailerEntity>> FLUID_TRAILER = VehicleUtil.createEntityType(REGISTER, "fluid_trailer", FluidTrailerEntity::new, 1.5F, 1.5F);
    public static final RegistryObject<EntityType<SeederTrailerEntity>> SEEDER = VehicleUtil.createEntityType(REGISTER, "seeder", SeederTrailerEntity::new, 1.5F, 1.0F);
    public static final RegistryObject<EntityType<FertilizerTrailerEntity>> FERTILIZER = VehicleUtil.createEntityType(REGISTER, "fertilizer", FertilizerTrailerEntity::new, 1.5F, 1.0F);

    /* Special Vehicles */
    public static final RegistryObject<EntityType<SofacopterEntity>> SOFACOPTER = VehicleUtil.createModDependentEntityType(REGISTER, "cfm", "sofacopter", SofacopterEntity::new, 1.0F, 1.0F, false);

    /* Other */
    public static final RegistryObject<EntityType<EntityJack>> JACK = registerEntity("jack", EntityJack::new, 0.0F, 0.0F);

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String id, BiFunction<EntityType<T>, World, T> function, float width, float height)
    {
        EntityType<T> type = EntityType.Builder.of(function::apply, EntityClassification.MISC).sized(width, height).setTrackingRange(256).setUpdateInterval(1).noSummon().fireImmune().setShouldReceiveVelocityUpdates(true).build(id);
        return ModEntities.REGISTER.register(id, () -> type);
    }
}