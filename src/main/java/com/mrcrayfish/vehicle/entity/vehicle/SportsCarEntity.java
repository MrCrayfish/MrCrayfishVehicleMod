package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.CameraProperties;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.entity.Transform;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.entity.properties.PoweredProperties;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class SportsCarEntity extends LandVehicleEntity
{
    public SportsCarEntity(EntityType<? extends LandVehicleEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public VehicleProperties getProperties()
    {
        return VehicleProperties.builder()
                .setBodyTransform(Transform.create(1.6))
                .setCanChangeWheels(true)
                .setCanBePainted(true)
                .addWheel(Wheel.builder()
                        .setPosition(Wheel.Position.FRONT)
                        .setSide(Wheel.Side.LEFT)
                        .setOffset(6.5, 0.25, 12.5)
                        .setScale(0.9))
                .addWheel(Wheel.builder()
                        .setPosition(Wheel.Position.FRONT)
                        .setSide(Wheel.Side.RIGHT)
                        .setOffset(6.5, 0.25, 12.5)
                        .setScale(0.9))
                .addWheel(Wheel.builder()
                        .setPosition(Wheel.Position.REAR)
                        .setSide(Wheel.Side.LEFT)
                        .setOffset(6.5, 0.25, -11.5)
                        .setScale(0.9))
                .addWheel(Wheel.builder()
                        .setPosition(Wheel.Position.REAR)
                        .setSide(Wheel.Side.RIGHT)
                        .setOffset(6.5, 0.25, -11.5)
                        .setScale(0.9))
                .addSeat(Seat.of(4, -2, -4, true))
                .setCamera(CameraProperties.builder()
                    .setDistance(5.0))
                .addExtended(PoweredProperties.builder()
                        .setEngineType(EngineType.LARGE_MOTOR)
                        .setEnginePower(20F)
                        .setFuelFillerTransform(Transform.create(-10.0, 6.5, -14.0, 0.0, -90.0, 0.0, 0.4))
                        .setIgnitionTransform(Transform.create(-5.0, 4.5, 6.5, -45.0, 0.0, 0.0, 0.5))
                        .setFrontAxleOffset(12.5)
                        .setRearAxleOffset(-11.5)
                        .setEnergyCapacity(20000F)
                        .setEngineSound(ModSounds.ENTITY_MINI_BUS_ENGINE.getId())
                        .setMaxSteeringAngle(35F)
                        .build()).build(true);
    }
}
