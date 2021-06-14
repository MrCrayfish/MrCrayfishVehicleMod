package com.mrcrayfish.vehicle.entity;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class VehicleProperties
{
    private static final Map<EntityType<?>, VehicleProperties> PROPERTIES_MAP = new HashMap<>();

    public static void setProperties(EntityType<? extends VehicleEntity> entityType, VehicleProperties properties)
    {
        if(!PROPERTIES_MAP.containsKey(entityType) || Config.CLIENT.reloadVehiclePropertiesEachTick.get())
        {
            PROPERTIES_MAP.put(entityType, properties);
        }
    }

    public static VehicleProperties getProperties(EntityType<?> entityType)
    {
        return PROPERTIES_MAP.get(entityType);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if(event.phase != TickEvent.Phase.END || player == null)
            return;

        if(!Config.CLIENT.reloadVehiclePropertiesEachTick.get())
            return;

        VehicleProperties.register();
    }

    public static void register()
    {
        VehicleProperties.setProperties(ModEntities.ALUMINUM_BOAT.get(), new VehicleProperties()
                .setBodyPosition(new PartPosition(0.0, 0.0, 0.2, 1.1))
                .setFuelPortPosition(new PartPosition(-16.0, 3, -18, 0.0, -90.0, 0.0, 0.25))
                .setHeldOffset(new Vector3d(0.0, 0.0, 0.0))
                .setDisplayPosition(new PartPosition(1.0F))
                .addSeat(new Seat(new Vector3d(-7, 8, -15), true))
                .addSeat(new Seat(new Vector3d(7, 6, -15)))
                .addSeat(new Seat(new Vector3d(-7, 6, 3)))
                .addSeat(new Seat(new Vector3d(7, 6, 3)))
                .setEngineType(EngineType.SMALL_MOTOR));
        
        VehicleProperties.setProperties(ModEntities.ATV.get(), new VehicleProperties()
                .setAxleOffset(-1.5F)
                .setWheelOffset(4.375F)
                .setBodyPosition(new PartPosition(1.25))
                .setFuelPortPosition(new PartPosition(0, 6.55, 5.0, -90, 0, 0, 0.35))
                .setKeyPortPosition(new PartPosition(-5, 4.5, 6.5, -45, 0, 0, 0.5))
                .setHeldOffset(new Vector3d(4.0, 3.5, 0.0))
                .setTowBarPosition(new Vector3d(0.0, 0.0, -20.8))
                .setTrailerOffset(new Vector3d(0.0, 0.0, -0.55))
                .setDisplayPosition(new PartPosition(1.5F))
                .addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 4.0F, 10.5F, 1.85F, true, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 4.0F, 10.5F, 1.85F, true, true)
                .addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 4.0F, -10.5F, 1.85F, true, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 4.0F, -10.5F, 1.85F, true, true)
                .setFrontAxelVec(0, 10.5)
                .setRearAxelVec(0, -10.5)
                .addSeat(new Seat(new Vector3d(0, 5, -3), true))
                .addSeat(new Seat(new Vector3d(0, 5.5, -12)))
                .setEngineType(EngineType.SMALL_MOTOR));
        
        VehicleProperties.setProperties(ModEntities.BUMPER_CAR.get(), new VehicleProperties()
                .setAxleOffset(-1.5F)
                .setWheelOffset(1.5F)
                .setBodyPosition(new PartPosition(1.2))
                .setFuelPortPosition(new PartPosition(-8.0, 6, -8.0, 0, -90, 0, 0.25))
                .setHeldOffset(new Vector3d(6.0, 0.0, 0.0))
                .setTrailerOffset(new Vector3d(0.0, -0.03125, -0.5625))
                .setDisplayPosition(new PartPosition(1.5F))
                .addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 7.0F, 8.5F, 0.75F, false, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 7.0F, 8.5F, 0.75F, false, true)
                .addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 7.0F, -8.5F, 0.75F, false, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 7.0F, -8.5F, 0.75F, false, true)
                .setFrontAxelVec(0, 8.5)
                .setRearAxelVec(0, -8.5)
                .addSeat(new Seat(new Vector3d(0, 1, -6), true))
                .setEngineType(EngineType.ELECTRIC_MOTOR));

        VehicleProperties.setProperties(ModEntities.DIRT_BIKE.get(), new VehicleProperties()
                .setAxleOffset(0.0F)
                .setWheelOffset(5.625F)
                .setBodyPosition(new PartPosition(1.0))
                .setEnginePosition(new PartPosition(0, 1, 0, 0, 180, 0, 0.85))
                .setFuelPortPosition(new PartPosition(0, 12.1, 3, 67.5, 180, 0, 0.35))
                .setHeldOffset(new Vector3d(0.0, 3.5, 0.0))
                .setDisplayPosition(new PartPosition(1.4F))
                .setTrailerOffset(new Vector3d(0, -0.0625, -0.3125))
                .addWheel(Wheel.Side.NONE, Wheel.Position.FRONT, 0.0F, 0.0F, 14.08F, 1.5F, 2.25F, 2.25F, false, false)
                .addWheel(Wheel.Side.NONE, Wheel.Position.REAR, 0.0F, 0.0F, -11.61F, 1.5F, 2.25F, 2.25F, true, true)
                .setFrontAxelVec(0, 14.08)
                .setRearAxelVec(0, -11.61)
                .addSeat(new Seat(new Vector3d(0, 8, -2), true))
                .addSeat(new Seat(new Vector3d(0, 9, -9)))
                .setEngineType(EngineType.SMALL_MOTOR));

        VehicleProperties.setProperties(ModEntities.DUNE_BUGGY.get(), new VehicleProperties()
                .setAxleOffset(-2.3F)
                .setWheelOffset(2.5F)
                .setBodyPosition(new PartPosition(1.3))
                .setFuelPortPosition(new PartPosition(0, 3, -7.0, 0, 180, 0, 0.25))
                .setHeldOffset(new Vector3d(2.0, 0.0, 0.0))
                .setTrailerOffset(new Vector3d(0.0, -0.025, -0.25))
                .setDisplayPosition(new PartPosition(1.75F))
                .addWheel(Wheel.Side.NONE, Wheel.Position.FRONT, 2.4F, 5.3F, 1.0F, false, false)
                .addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 2.4F, -5.7F, 1.0F, true, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 2.4F, -5.7F, 1.0F, true, true)
                .setFrontAxelVec(0, 5.3)
                .setRearAxelVec(0, -5.7)
                .addSeat(new Seat(new Vector3d(0, 2, -3), true))
                .setEngineType(EngineType.SMALL_MOTOR));

        VehicleProperties.setProperties(ModEntities.GO_KART.get(), new VehicleProperties()
                .setAxleOffset(-2.5F)
                .setWheelOffset(3.45F)
                .setBodyPosition(new PartPosition(1.0))
                .setEnginePosition(new PartPosition(0, 2, -9, 0, 180, 0, 1.2))
                .setHeldOffset(new Vector3d(3.0D, 0.5D, 0.0D))
                .setTrailerOffset(new Vector3d(0D, -0.03125D, -0.375D))
                .setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.5F))
                .addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 9.0F, 13.5F, 1.4F, false, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 9.0F, 13.5F, 1.4F, false, true)
                .addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 9.0F, -8.5F, 1.4F, true, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 9.0F, -8.5F, 1.4F, true, true)
                .setFrontAxelVec(0, 13.5)
                .setRearAxelVec(0, -8.5)
                .addSeat(new Seat(new Vector3d(0, -2, -1), true))
                .setEngineType(EngineType.SMALL_MOTOR));

        VehicleProperties.setProperties(ModEntities.GOLF_CART.get(), new VehicleProperties()
                .setAxleOffset(-0.5F)
                .setWheelOffset(4.45F)
                .setBodyPosition(new PartPosition(1.15))
                .setFuelPortPosition(new PartPosition(-13, 3.5, -6, 0, -90, 0, 0.25))
                .setKeyPortPosition(new PartPosition(-8.5, 2.75, 8.5, -67.5, 0, 0, 0.5))
                .setHeldOffset(new Vector3d(1.5D, 2.5D, 0.0D))
                .setDisplayPosition(new PartPosition(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.25F))
                .addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 9.0F, 16.0F, 1.75F, false, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 9.0F, 16.0F, 1.75F, false, true)
                .addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 9.0F, -12.5F, 1.75F, true, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 9.0F, -12.5F, 1.75F, true, true)
                .setFrontAxelVec(0, 16.0)
                .setRearAxelVec(0, -12.5)
                .addSeat(new Seat(new Vector3d(5.5, 5, -6), true))
                .addSeat(new Seat(new Vector3d(-5.5, 5, -6)))
                .addSeat(new Seat(new Vector3d(5.5, 5, -15), 180F))
                .addSeat(new Seat(new Vector3d(-5.5, 5, -15), 180F))
                .setEngineType(EngineType.ELECTRIC_MOTOR));

        VehicleProperties.setProperties(ModEntities.JET_SKI.get(), new VehicleProperties()
                .setWheelOffset(2.75F)
                .setBodyPosition(new PartPosition(0.0, 0.0, 0.25, 0.0, 0.0, 0.0, 1.25))
                .setFuelPortPosition(new PartPosition(0.0, 9.25, 8.5, -90, 0, 0, 0.35))
                .setHeldOffset(new Vector3d(6.0, 0.0, 0.0))
                .setTrailerOffset(new Vector3d(0.0, -0.09375, -0.65))
                .setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.45F, 0.0F, 0.0F, 0.0F, 1.5F))
                .addSeat(new Seat(new Vector3d(0, 5, 0), true))
                .addSeat(new Seat(new Vector3d(0, 5, -7)))
                .setEngineType(EngineType.SMALL_MOTOR));

        VehicleProperties.setProperties(ModEntities.LAWN_MOWER.get(), new VehicleProperties()
                .setAxleOffset(-2.0F)
                .setWheelOffset(2.85F)
                .setBodyPosition(new PartPosition(1.25))
                .setFuelPortPosition(new PartPosition(-4.50, 9.5, 4.0, 0, -90, 0, 0.2))
                .setKeyPortPosition(new PartPosition(-5, 4.5, 6.5, -45, 0, 0, 0.5))
                .setHeldOffset(new Vector3d(12.0, -1.5, 0.0))
                .setTowBarPosition(new Vector3d(0.0, 0.0, -20.0))
                .setTrailerOffset(new Vector3d(0.0, -0.01, -1.0))
                .setDisplayPosition(new PartPosition(1.5F))
                .addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 6.0F, 0.0F, 13.5F, 1.15F, false, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 6.0F, 0.0F, 13.5F, 1.15F, false, true)
                .addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 5.0F, 0.8F, -10.7F, 1.55F, true, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 5.0F, 0.8F, -10.7F, 1.55F, true, true)
                .setFrontAxelVec(0, 13.5)
                .setRearAxelVec(0, -10.7)
                .addSeat(new Seat(new Vector3d(0, 7, -9), true))
                .setEngineType(EngineType.SMALL_MOTOR));

        VehicleProperties.setProperties(ModEntities.MINI_BIKE.get(), new VehicleProperties()
                .setAxleOffset(-1.7F)
                .setWheelOffset(4.0F)
                .setBodyPosition(new PartPosition(1.05))
                .setEnginePosition(new PartPosition(0, 1, 2.5, 0, 180F, 0, 1.0))
                .setHeldOffset(new Vector3d(6.0, 0.0, 0.0))
                .setTrailerOffset(new Vector3d(0.0, -0.0625, -0.5))
                .setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 1.5F))
                .addWheel(Wheel.Side.NONE, Wheel.Position.REAR, 0.0F, -6.7F, 1.65F, true, true)
                .addWheel(Wheel.Side.NONE, Wheel.Position.FRONT, 0.0F, -0.5F + 1.7F * 0.0625F, 13.0F, 1.65F, true, false)
                .setFrontAxelVec(0, 13)
                .setRearAxelVec(0, -6.7)
                .addSeat(new Seat(new Vector3d(0, 7, -2), true))
                .setEngineType(EngineType.SMALL_MOTOR));

        VehicleProperties.setProperties(ModEntities.MINI_BUS.get(), new VehicleProperties()
                .setAxleOffset(1.0F)
                .setWheelOffset(4.5F)
                .setBodyPosition(new PartPosition(1.3))
                .setFuelPortPosition(new PartPosition(-12.0, 8.0, -8.75, 0, -90, 0, 0.25))
                .setKeyPortPosition(new PartPosition(0, 6.75, 19.5, -67.5, 0, 0, 0.5))
                .setHeldOffset(new Vector3d(0.0, 3.5, 0.0))
                .setTowBarPosition(new Vector3d(0.0, 0.0, -33.0))
                .setDisplayPosition(new PartPosition(1.0F))
                .addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 9.0F, 0.0F, 13.5F, 1.5F, 1.9F, 1.9F, true, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 9.0F, 0.0F, 13.5F, 1.5F, 1.9F, 1.9F, true, true)
                .addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 9.0F, 0.0F, -13.5F, 1.5F, 1.9F, 1.9F, true, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 9.0F, 0.0F, -13.5F, 1.5F, 1.9F, 1.9F, true, true)
                .setFrontAxelVec(0, 14.5)
                .setRearAxelVec(0, -14.5)
                .addSeat(new Seat(new Vector3d(4.5, 2, 11), true))
                .addSeat(new Seat(new Vector3d(-4.5, 2, 11)))
                .addSeat(new Seat(new Vector3d(4.5, 2, -3)))
                .addSeat(new Seat(new Vector3d(-4.5, 2, -3)))
                .addSeat(new Seat(new Vector3d(4.5, 2, -15)))
                .setEngineType(EngineType.LARGE_MOTOR));

        VehicleProperties.setProperties(ModEntities.MOPED.get(), new VehicleProperties()
                .setAxleOffset(-1.0F)
                .setWheelOffset(3.5F)
                .setBodyPosition(new PartPosition(1.2))
                .setFuelPortPosition(new PartPosition(-2.5, 4.2, -2.5, 0, -90, 0, 0.2))
                .setHeldOffset(new Vector3d(7.0, 2.0, 0.0))
                .setTrailerOffset(new Vector3d(0.0, -0.03125, -0.65))
                .setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F))
                .addWheel(Wheel.Side.NONE, Wheel.Position.REAR, 0.0F, -6.7F, 1.5F, true, true)
                .addWheel(Wheel.Side.NONE, Wheel.Position.FRONT, 0.0F, -0.4F, 14.5F, 1.3F, true, false)
                .setFrontAxelVec(0, 14.5)
                .setRearAxelVec(0, -6.7)
                .addSeat(new Seat(new Vector3d(0, 4, -2), true))
                .setEngineType(EngineType.SMALL_MOTOR));

        VehicleProperties.setProperties(ModEntities.OFF_ROADER.get(), new VehicleProperties()
                .setAxleOffset(-1.0F)
                .setWheelOffset(5.4F)
                .setBodyPosition(new PartPosition(1.4))
                .setFuelPortPosition(new PartPosition(-12.0, 8.5, -6.5, 0, -90, 0, 0.25))
                .setKeyPortPosition(new PartPosition(0, 7, 6.2, -67.5, 0, 0, 0.5))
                .setHeldOffset(new Vector3d(0.0, 3.5, 0.0))
                .setDisplayPosition(new PartPosition(0.0F, 0.0F, 0.1F, 0.0F, 0.0F, 0.0F, 1.0F))
                .addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 10.0F, 14.5F, 2.25F, true, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 10.0F, 14.5F, 2.25F, true, true)
                .addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 10.0F, -14.5F, 2.25F, true, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 10.0F, -14.5F, 2.25F, true, true)
                .setFrontAxelVec(0, 14.5)
                .setRearAxelVec(0, -14.5)
                .addSeat(new Seat(new Vector3d(5, 4, -3), true))
                .addSeat(new Seat(new Vector3d(-5, 4, -3)))
                .addSeat(new Seat(new Vector3d(5, 11.5, -14.5)))
                .addSeat(new Seat(new Vector3d(-5, 3.5, -18.9)))
                .setEngineType(EngineType.LARGE_MOTOR));

        VehicleProperties.setProperties(ModEntities.SHOPPING_CART.get(), new VehicleProperties()
                .setAxleOffset(-1.0F)
                .setWheelOffset(2.0F)
                .setBodyPosition(new PartPosition(1.05))
                .setHeldOffset(new Vector3d(4.0, 9.25, 0.0))
                .setTrailerOffset(new Vector3d(0.0, -0.03125, -0.25))
                .setDisplayPosition(new PartPosition(1.45F))
                .addWheel(Wheel.Side.LEFT, Wheel.Position.NONE, 5.75F, -10.5F, 0.75F, false, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.NONE, 5.75F, -10.5F, 0.75F, false, true)
                .addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 4.0F, 9.5F, 0.75F, false, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 4.0F, 9.5F, 0.75F, false, true)
                .setFrontAxelVec(0, 9.5)
                .setRearAxelVec(0, -10.5)
                .addSeat(new Seat(new Vector3d(0, 7, -4), true))
                .setEngineType(EngineType.NONE));

        VehicleProperties.setProperties(ModEntities.SMART_CAR.get(), new VehicleProperties()
                .setAxleOffset(-1.7F)
                .setWheelOffset(3.5F)
                .setBodyPosition(new PartPosition(1.25))
                .setFuelPortPosition(new PartPosition(-9.0, 8.7, -12.3, 0, -90, 0, 0.25))
                .setHeldOffset(new Vector3d(3.0, 1.0, 0.0))
                .setTowBarPosition(new Vector3d(0.0, 0.0, -24.5))
                .setDisplayPosition(new PartPosition(1.35F))
                .addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 7F, 12F, 1.5F, true, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 7F, 12F, 1.5F, true, true)
                .addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 7F, -12F, 1.5F, false, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 7F, -12F, 1.5F, false, true)
                .setFrontAxelVec(0, 12)
                .setRearAxelVec(0, -12)
                .addSeat(new Seat(new Vector3d(0, 0.5, -2), true))
                .setEngineType(EngineType.ELECTRIC_MOTOR));

        VehicleProperties.setProperties(ModEntities.SPEED_BOAT.get(), new VehicleProperties()
                .setWheelOffset(2.5F)
                .setBodyPosition(new PartPosition(0.0, -0.03125, 0.6875, 1.0))
                .setFuelPortPosition(new PartPosition(0.0, 5.25, -20.5, -90.0, 0.0, 0.0, 0.65))
                .setHeldOffset(new Vector3d(6.0, -0.5, 0.0))
                .setTrailerOffset(new Vector3d(0.0, -0.09375, -0.75))
                .setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.65F, 0.0F, 0.0F, 0.0F, 1.25F))
                .addSeat(new Seat(new Vector3d(0, 0, 0), true))
                .setEngineType(EngineType.LARGE_MOTOR));

        VehicleProperties.setProperties(ModEntities.SPORTS_PLANE.get(), new VehicleProperties()
                .setBodyPosition(new PartPosition(0, 11 * 0.0625, -8 * 0.0625, 0, 0, 0, 1.8))
                .setFuelPortPosition(new PartPosition(-4.35, 4, -6, 0, -112.5, 0, 0.25))
                .setKeyPortPosition(new PartPosition(0, 3.75, 12.5, -67.5, 0, 0, 0.5))
                .setDisplayPosition(new PartPosition(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.85F))
                .addSeat(new Seat(new Vector3d(0, 6, 0), true))
                .setEngineType(EngineType.LARGE_MOTOR));

        VehicleProperties.setProperties(ModEntities.TRACTOR.get(), new VehicleProperties()
                .setAxleOffset(-3.0F)
                .setWheelOffset(5.5F)
                .setBodyPosition(new PartPosition(1.0))
                .setEnginePosition(new PartPosition(0, 6, 8.775, 0, 0, 0, 0.85))
                .setFuelPortPosition(new PartPosition(-6.0, 9.5, -0.5, 0, -90, 0, 0.3))
                .setKeyPortPosition(new PartPosition(-2.75, 12, -1.75, -45.0, 0, 0, 0.5))
                .setHeldOffset(new Vector3d(0.0, 3.5, 0.0))
                .setTowBarPosition(new Vector3d(0.0, 0.0, -24.5))
                .setDisplayPosition(new PartPosition(1.25F))
                .addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 8.0F, 0.0F, 14.0F, 1.5F, 2.25F, 2.25F, true, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 8.0F, 0.0F, 14.0F, 1.5F, 2.25F, 2.25F, true, true)
                .addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 8.0F, 5.5F, -14.5F, 3.0F, 4.5F, 4.5F, true, true)
                .addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 8.0F, 5.5F, -14.5F, 3.0F, 4.5F, 4.5F, true, true)
                .setFrontAxelVec(0, 14.0)
                .setRearAxelVec(0, -14.5)
                .addSeat(new Seat(new Vector3d(0, 9, -14), true))
                .setEngineType(EngineType.LARGE_MOTOR));

        VehicleProperties.setProperties(ModEntities.FERTILIZER.get(), new VehicleProperties()
                .setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1))
                .setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F)));

        VehicleProperties.setProperties(ModEntities.FLUID_TRAILER.get(),  new VehicleProperties()
                .setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1))
                .setHeldOffset(new Vector3d(0D, 3D, 0D))
                .setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F)));

        VehicleProperties.setProperties(ModEntities.SEEDER.get(), new VehicleProperties()
                .setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1))
                .setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F)));

        VehicleProperties.setProperties(ModEntities.STORAGE_TRAILER.get(), new VehicleProperties()
                .setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1))
                .setTowBarPosition(new Vector3d(0.0, 0.0, -12.0))
                .setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F)));

        VehicleProperties.setProperties(ModEntities.VEHICLE_TRAILER.get(), new VehicleProperties()
                .setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1))
                .setHeldOffset(new Vector3d(0D, 3D, 0D))
                .setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F)));

        if(ModList.get().isLoaded("cfm"))
        {
            VehicleProperties.setProperties(ModEntities.BATH.get(), new VehicleProperties()
                    .setBodyPosition(new PartPosition(1.0))
                    .setHeldOffset(new Vector3d(4.0, 3.5, 0.0))
                    .setTrailerOffset(new Vector3d(0.0, 0.0, -0.4375))
                    .setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F))
                    .addSeat(new Seat(new Vector3d(0, 0, 0), true))
                    .setEngineType(EngineType.NONE));

            VehicleProperties.setProperties(ModEntities.SOFA.get(), new VehicleProperties()
                    .setAxleOffset(-1.5F)
                    .setWheelOffset(5.0F)
                    .setBodyPosition(new PartPosition(0, -0.0625, 0, 0, 0, 0, 1.0))
                    .setFuelPortPosition(new PartPosition(0, 2, 8, 0, 0, 0, 0.5))
                    .setHeldOffset(new Vector3d(2.0, 2.0, 0.0))
                    .setTrailerOffset(new Vector3d(0.0, 0.0, -0.25))
                    .setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F))
                    .addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 8.0F, 0.0625F, 7.0F, 1.75F, false, true)
                    .addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 8.0F, 0.0625F, 7.0F, 1.75F, false, true)
                    .addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 8.0F, 0.0625F, -7.0F, 1.75F, true, true)
                    .addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 8.0F, 0.0625F, -7.0F, 1.75F, true, true)
                    .setFrontAxelVec(0, 7.0)
                    .setRearAxelVec(0, -7.0)
                    .addSeat(new Seat(new Vector3d(0, 5, 0), true))
                    .setEngineType(EngineType.SMALL_MOTOR));

            VehicleProperties.setProperties(ModEntities.SOFACOPTER.get(), new VehicleProperties()
                    .setBodyPosition(new PartPosition(0, 0, 0.0625, 0, 0, 0, 1))
                    .setFuelPortPosition(new PartPosition(0.0, 1.5, 8.0, 0, 0, 0, 0.45))
                    .setKeyPortPosition(new PartPosition(-9.25, 8, 5, 0, 0, 0, 0.8))
                    .setDisplayPosition(new PartPosition(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.25F))
                    .addSeat(new Seat(new Vector3d(0, 0, 0), true))
                    .setEngineType(EngineType.SMALL_MOTOR));
        }
    }

    private float axleOffset;
    private float wheelOffset;
    private Vector3d heldOffset = Vector3d.ZERO;
    private Vector3d towBarVec = Vector3d.ZERO;
    private Vector3d trailerOffset = Vector3d.ZERO;
    private List<Wheel> wheels = new ArrayList<>();
    private PartPosition bodyPosition = PartPosition.DEFAULT;
    private PartPosition enginePosition;
    private PartPosition fuelPortPosition;
    private PartPosition fuelPortLidPosition;
    private PartPosition keyPortPosition;
    private PartPosition keyPosition;
    private PartPosition displayPosition = PartPosition.DEFAULT;
    private Vector3d frontAxelVec = null;
    private Vector3d rearAxelVec = null;
    private List<Seat> seats = new ArrayList<>();
    private EngineType engineType = EngineType.NONE;

    public VehicleProperties setAxleOffset(float axleOffset)
    {
        this.axleOffset = axleOffset;
        return this;
    }

    public float getAxleOffset()
    {
        return this.axleOffset;
    }

    public VehicleProperties setWheelOffset(float wheelOffset)
    {
        this.wheelOffset = wheelOffset;
        return this;
    }

    public float getWheelOffset()
    {
        return this.wheelOffset;
    }

    public VehicleProperties setHeldOffset(Vector3d heldOffset)
    {
        this.heldOffset = heldOffset;
        return this;
    }

    public Vector3d getHeldOffset()
    {
        return this.heldOffset;
    }

    public VehicleProperties setTowBarPosition(Vector3d towBarVec)
    {
        this.towBarVec = towBarVec;
        return this;
    }

    public Vector3d getTowBarPosition()
    {
        return this.towBarVec;
    }

    public VehicleProperties setTrailerOffset(Vector3d trailerOffset)
    {
        this.trailerOffset = trailerOffset;
        return this;
    }

    public Vector3d getTrailerOffset()
    {
        return this.trailerOffset;
    }

    public VehicleProperties addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetZ, float scale, boolean particles, boolean render)
    {
        this.wheels.add(new Wheel(side, position, 2.0F, scale, scale, scale, offsetX, 0F, offsetZ, particles, render));
        return this;
    }

    public VehicleProperties addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetY, float offsetZ, float scale, boolean particles, boolean render)
    {
        this.wheels.add(new Wheel(side, position, 2.0F, scale, scale, scale, offsetX, offsetY, offsetZ, particles, render));
        return this;
    }

    public VehicleProperties addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetY, float offsetZ, float scaleX, float scaleY, float scaleZ, boolean particles, boolean render)
    {
        this.wheels.add(new Wheel(side, position, 2.0F, scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, particles, render));
        return this;
    }

    public List<Wheel> getWheels()
    {
        return this.wheels;
    }

    @Nullable
    public Wheel getFirstFrontWheel()
    {
        return this.wheels.stream().filter(wheel -> wheel.getPosition() == Wheel.Position.FRONT).findFirst().orElse(null);
    }

    @Nullable
    public Wheel getFirstRearWheel()
    {
        return this.wheels.stream().filter(wheel -> wheel.getPosition() == Wheel.Position.REAR).findFirst().orElse(null);
    }

    public VehicleProperties setBodyPosition(PartPosition bodyPosition)
    {
        this.bodyPosition = bodyPosition;
        return this;
    }

    public PartPosition getBodyPosition()
    {
        return this.bodyPosition;
    }

    public VehicleProperties setEnginePosition(PartPosition enginePosition)
    {
        this.enginePosition = enginePosition;
        return this;
    }

    public PartPosition getEnginePosition()
    {
        return this.enginePosition;
    }

    public VehicleProperties setFuelPortPosition(PartPosition fuelPortPosition)
    {
        this.fuelPortPosition = fuelPortPosition;
        this.fuelPortLidPosition = new PartPosition(
                fuelPortPosition.getX() - 6 * fuelPortPosition.getScale(),
                fuelPortPosition.getY(),
                fuelPortPosition.getZ() - 5 * fuelPortPosition.getScale(),
                fuelPortPosition.getRotX(),
                fuelPortPosition.getRotY() - 90,
                fuelPortPosition.getRotZ(),
                fuelPortPosition.getScale());
        return this;
    }

    public PartPosition getFuelPortPosition()
    {
        return this.fuelPortPosition;
    }

    public VehicleProperties setFuelPortLidPosition(PartPosition fuelPortLidPosition)
    {
        this.fuelPortLidPosition = fuelPortLidPosition;
        return this;
    }

    public PartPosition getFuelPortLidPosition()
    {
        return this.fuelPortLidPosition;
    }

    public VehicleProperties setKeyPortPosition(PartPosition keyPortPosition)
    {
        this.keyPortPosition = keyPortPosition;
        this.keyPosition = new PartPosition(keyPortPosition.getX(), keyPortPosition.getY(), keyPortPosition.getZ(), keyPortPosition.getRotX() + 90, 0, 0, 0.15);
        return this;
    }

    public PartPosition getKeyPortPosition()
    {
        return this.keyPortPosition;
    }

    public VehicleProperties setKeyPosition(PartPosition keyPosition)
    {
        this.keyPosition = keyPosition;
        return this;
    }

    public PartPosition getKeyPosition()
    {
        return this.keyPosition;
    }

    public VehicleProperties setDisplayPosition(PartPosition displayPosition)
    {
        this.displayPosition = displayPosition;
        return this;
    }

    public PartPosition getDisplayPosition()
    {
        return this.displayPosition;
    }

    public VehicleProperties setFrontAxelVec(double x, double z)
    {
        this.frontAxelVec = new Vector3d(x, 0, z);
        return this;
    }

    @Nullable
    public Vector3d getFrontAxelVec()
    {
        return this.frontAxelVec;
    }

    public VehicleProperties setRearAxelVec(double x, double z)
    {
        this.rearAxelVec = new Vector3d(x, 0, z);
        return this;
    }

    @Nullable
    public Vector3d getRearAxelVec()
    {
        return this.rearAxelVec;
    }

    public VehicleProperties addSeat(Seat seat)
    {
        this.seats.add(seat);
        return this;
    }

    public List<Seat> getSeats()
    {
        return ImmutableList.copyOf(this.seats);
    }

    public VehicleProperties setEngineType(EngineType type)
    {
        this.engineType = type;
        return this;
    }

    public EngineType getEngineType()
    {
        return this.engineType;
    }
}
