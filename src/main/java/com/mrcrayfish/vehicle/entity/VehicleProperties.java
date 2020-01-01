package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class VehicleProperties
{
    private static final Map<EntityType<?>, VehicleProperties> PROPERTIES_MAP = new HashMap<>();

    public static void setProperties(EntityType<? extends VehicleEntity> entityType, VehicleProperties properties)
    {
        if(!PROPERTIES_MAP.containsKey(entityType))
        {
            PROPERTIES_MAP.put(entityType, properties);
        }
    }

    public static VehicleProperties getProperties(EntityType<?> entityType)
    {
        return PROPERTIES_MAP.get(entityType);
    }

    public static void register()
    {
        VehicleProperties properties;

        /* Aluminum Boat */
        properties = new VehicleProperties();
        properties.setBodyPosition(new PartPosition(0.0, 0.0, 0.2, 1.1));
        properties.setFuelPortPosition(new PartPosition(-16.25, 3, -18.5, 0.0, -90.0, 0.0, 0.25));
        properties.setHeldOffset(new Vec3d(36.0, 0.0, 0.0));
        VehicleProperties.setProperties(ModEntities.ALUMINUM_BOAT, properties);

        /* ATV */
        properties = new VehicleProperties();
        properties.setAxleOffset(-1.5F);
        properties.setWheelOffset(4.375F);
        properties.setBodyPosition(new PartPosition(0, 0, 0.25, 0, 0, 0, 1.25));
        properties.setFuelPortPosition(new PartPosition(-1.57, 6.55, 5.3, -90, 0, 0, 0.35));
        properties.setKeyPortPosition(new PartPosition(-5, 4.5, 6.5, -45, 0, 0, 0.5));
        properties.setHeldOffset(new Vec3d(4.0, 3.5, 0.0));
        properties.setTowBarPosition(new Vec3d(0.0, 0.0, -20.8));
        properties.setTrailerOffset(new Vec3d(0.0, 0.0, -0.55));
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 4.0F, 10.5F, 1.85F, true, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 4.0F, 10.5F, 1.85F, true, true);
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 4.0F, -10.5F, 1.85F, true, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 4.0F, -10.5F, 1.85F, true, true);
        VehicleProperties.setProperties(ModEntities.ATV, properties);

        /* Bath */
        properties = new VehicleProperties();
        properties.setBodyPosition(new PartPosition(0, 0, 0, 0, 0, 0, 1.0));
        properties.setHeldOffset(new Vec3d(4.0, 3.5, 0.0));
        properties.setTrailerOffset(new Vec3d(0.0, 0.0, -0.4375));
        VehicleProperties.setProperties(ModEntities.BATH, properties);

        /* Bumper Car */
        properties = new VehicleProperties();
        properties.setAxleOffset(-1.5F);
        properties.setWheelOffset(1.5F);
        properties.setBodyPosition(new PartPosition(0, 0, 0.4, 0, 0, 0, 1.2));
        properties.setFuelPortPosition(new PartPosition(-8.25, 6, -9.3, 0, -90, 0, 0.25));
        properties.setHeldOffset(new Vec3d(6.0, 0.0, 0.0));
        properties.setTrailerOffset(new Vec3d(0.0, -0.03125, -0.5625));
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 7.0F, 8.5F, 0.75F, false, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 7.0F, 8.5F, 0.75F, false, true);
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 7.0F, -8.5F, 0.75F, false, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 7.0F, -8.5F, 0.75F, false, true);
        VehicleProperties.setProperties(ModEntities.BUMPER_CAR, properties);

        /* Couch */
        properties = new VehicleProperties();
        properties.setAxleOffset(-1.5F);
        properties.setWheelOffset(5.0F);
        properties.setBodyPosition(new PartPosition(0, -0.0625, 0.1, 0, 0, 0, 1.0));
        properties.setHeldOffset(new Vec3d(2.0, 2.0, 0.0));
        properties.setTrailerOffset(new Vec3d(0.0, 0.0, -0.25));
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 8.0F, 0.0625F, 7.0F, 1.75F, false, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 8.0F, 0.0625F, 7.0F, 1.75F, false, true);
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 8.0F, 0.0625F, -7.0F, 1.75F, true, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 8.0F, 0.0625F, -7.0F, 1.75F, true, true);
        VehicleProperties.setProperties(ModEntities.SOFA, properties);

        /* Dune Buggy */
        properties = new VehicleProperties();
        properties.setAxleOffset(-2.3F);
        properties.setWheelOffset(2.5F);
        properties.setBodyPosition(new PartPosition(0, 0, 0.225, 0, 0, 0, 1.3));
        properties.setFuelPortPosition(new PartPosition(1.15, 3, -7.25, 0, 180, 0, 0.25));
        properties.setHeldOffset(new Vec3d(2.0, 0.0, 0.0));
        properties.setTrailerOffset(new Vec3d(0.0, -0.025, -0.25));
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 2.4F, -5.7F, 1.0F, true, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 2.4F, -5.7F, 1.0F, true, true);
        VehicleProperties.setProperties(ModEntities.DUNE_BUGGY, properties);

        /* Go Kart */
        properties = new VehicleProperties();
        properties.setAxleOffset(-2.5F);
        properties.setWheelOffset(3.45F);
        properties.setBodyPosition(new PartPosition(0, 0, 0, 0, 0, 0, 1.0));
        properties.setEnginePosition(new PartPosition(0, 2, -9, 0, 180, 0, 1.2));
        properties.setHeldOffset(new Vec3d(3.0D, 0.5D, 0.0D));
        properties.setTrailerOffset(new Vec3d(0D, -0.03125D, -0.375D));
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 9.0F, 13.5F, 1.4F, false, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 9.0F, 13.5F, 1.4F, false, true);
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 9.0F, -8.5F, 1.4F, true, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 9.0F, -8.5F, 1.4F, true, true);
        VehicleProperties.setProperties(ModEntities.GO_KART, properties);

        /* Golf Cart */
        properties = new VehicleProperties();
        properties.setAxleOffset(-0.5F);
        properties.setWheelOffset(4.45F);
        properties.setBodyPosition(new PartPosition(0, 0, 0, 0, 0, 0, 1.15));
        properties.setFuelPortPosition(new PartPosition(-13.25, 3.5, -7.3, 0, -90, 0, 0.25));
        properties.setKeyPortPosition(new PartPosition(-8.5, 2.75, 8.5, -67.5, 0, 0, 0.5));
        properties.setHeldOffset(new Vec3d(1.5D, 2.5D, 0.0D));
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 9.0F, 16.0F, 1.75F, false, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 9.0F, 16.0F, 1.75F, false, true);
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 9.0F, -12.5F, 1.75F, true, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 9.0F, -12.5F, 1.75F, true, true);
        VehicleProperties.setProperties(ModEntities.GOLF_CART, properties);

        /* Jet Ski */
        properties = new VehicleProperties();
        properties.setWheelOffset(2.75F);
        properties.setBodyPosition(new PartPosition(0.0, 0.0, 0.25, 0.0, 0.0, 0.0, 1.25));
        properties.setFuelPortPosition(new PartPosition(-1.57, 7.25, 4.87, -135, 0, 0, 0.35));
        properties.setHeldOffset(new Vec3d(6.0, 0.0, 0.0));
        properties.setTrailerOffset(new Vec3d(0.0, -0.09375, -0.65));
        VehicleProperties.setProperties(ModEntities.JET_SKI, properties);

        /* Lawn Mower */
        properties = new VehicleProperties();
        properties.setAxleOffset(-2.0F);
        properties.setWheelOffset(2.85F);
        properties.setBodyPosition(new PartPosition(0, 0, 0.65, 0, 0, 0, 1.25));
        properties.setFuelPortPosition(new PartPosition(-4.75, 9.5, 3.5, 0, -90, 0, 0.2));
        properties.setKeyPortPosition(new PartPosition(-5, 4.5, 6.5, -45, 0, 0, 0.5));
        properties.setHeldOffset(new Vec3d(12.0, -1.5, 0.0));
        properties.setTowBarPosition(new Vec3d(0.0, 0.0, -20.0));
        properties.setTrailerOffset(new Vec3d(0.0, -0.01, -1.0));
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 6.0F, 0.0F, 13.5F, 1.15F, false, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 6.0F, 0.0F, 13.5F, 1.15F, false, true);
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 5.0F, 0.8F, -10.7F, 1.55F, true, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 5.0F, 0.8F, -10.7F, 1.55F, true, true);
        VehicleProperties.setProperties(ModEntities.LAWN_MOWER, properties);

        /* Mini Bike */
        properties = new VehicleProperties();
        properties.setAxleOffset(-1.7F);
        properties.setWheelOffset(4.0F);
        properties.setBodyPosition(new PartPosition(0, 0, 0.1, 0, 0, 0, 1.05));
        properties.setEnginePosition(new PartPosition(0, 1, 2.5, 0, 180F, 0, 1.0));
        properties.setHeldOffset(new Vec3d(6.0, 0.0, 0.0));
        properties.setTrailerOffset(new Vec3d(0.0, -0.0625, -0.5));
        properties.addWheel(Wheel.Side.NONE, Wheel.Position.REAR, 0.0F, -6.7F, 1.65F, true, true);
        properties.addWheel(Wheel.Side.NONE, Wheel.Position.FRONT, 0.0F, -0.5F + 1.7F * 0.0625F, 13.0F, 1.65F, true, false);
        VehicleProperties.setProperties(ModEntities.MINI_BIKE, properties);

        /* Moped */
        properties = new VehicleProperties();
        properties.setAxleOffset(-1.0F);
        properties.setWheelOffset(3.5F);
        properties.setBodyPosition(new PartPosition(0, 0, 0.15, 0, 0, 0, 1.2));
        properties.setFuelPortPosition(new PartPosition(-2.75, 4.2, -3.4, 0, -90, 0, 0.2));
        properties.setHeldOffset(new Vec3d(7.0, 2.0, 0.0));
        properties.setTrailerOffset(new Vec3d(0.0, -0.03125, -0.65));
        properties.addWheel(Wheel.Side.NONE, Wheel.Position.REAR, 0.0F, -6.7F, 1.5F, true, true);
        properties.addWheel(Wheel.Side.NONE, Wheel.Position.FRONT, 0.0F, -0.4F, 14.5F, 1.3F, true, false);
        VehicleProperties.setProperties(ModEntities.MOPED, properties);

        /* Off Roader */
        properties = new VehicleProperties();
        properties.setAxleOffset(-1.0F);
        properties.setWheelOffset(5.4F);
        properties.setBodyPosition(new PartPosition(0, 0, -0.125, 0, 0, 0, 1.4));
        properties.setFuelPortPosition(new PartPosition(-12.25, 8.5, -7.3, 0, -90, 0, 0.25));
        properties.setKeyPortPosition(new PartPosition(0, 7, 6.2, -67.5, 0, 0, 0.5));
        properties.setHeldOffset(new Vec3d(0.0, 3.5, 0.0));
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 10.0F, 14.5F, 2.25F, true, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 10.0F, 14.5F, 2.25F, true, true);
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 10.0F, -14.5F, 2.25F, true, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 10.0F, -14.5F, 2.25F, true, true);
        VehicleProperties.setProperties(ModEntities.OFF_ROADER, properties);

        /* Shopping Cart */
        properties = new VehicleProperties();
        properties.setAxleOffset(-1.0F);
        properties.setWheelOffset(2.0F);
        properties.setBodyPosition(new PartPosition(0, 0, 0.165, 0, 0, 0, 1.05));
        properties.setHeldOffset(new Vec3d(4.0, 9.25, 0.0));
        properties.setTrailerOffset(new Vec3d(0.0, -0.03125, -0.25));
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.NONE, 5.75F, -10.5F, 0.75F, false, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.NONE, 5.75F, -10.5F, 0.75F, false, true);
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 4.0F, 9.5F, 0.75F, false, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 4.0F, 9.5F, 0.75F, false, true);
        VehicleProperties.setProperties(ModEntities.SHOPPING_CART, properties);

        /* Smart Car */
        properties = new VehicleProperties();
        properties.setAxleOffset(-1.7F);
        properties.setWheelOffset(3.5F);
        properties.setBodyPosition(new PartPosition(0, 0, 0.2, 0, 0, 0, 1.25));
        properties.setFuelPortPosition(new PartPosition(-9.25, 8.7, -12.3, 0, -90, 0, 0.25));
        properties.setHeldOffset(new Vec3d(3.0, 1.0, 0.0));
        properties.setTowBarPosition(new Vec3d(0.0, 0.0, -24.5));
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 7F, 12F, 1.5F, true, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 7F, 12F, 1.5F, true, true);
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 7F, -12F, 1.5F, false, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 7F, -12F, 1.5F, false, true);
        VehicleProperties.setProperties(ModEntities.SMART_CAR, properties);

        /* Sofacopter */
        properties = new VehicleProperties();
        properties.setBodyPosition(new PartPosition(0, 0, 0.0625, 0, 0, 0, 1));
        properties.setFuelPortPosition(new PartPosition(-2, 1.75, 8.25, 0, 0, 0, 0.45));
        properties.setKeyPortPosition(new PartPosition(-9.25, 8, 5, 0, 0, 0, 0.8));
        VehicleProperties.setProperties(ModEntities.SOFACOPTER, properties);

        /* Speed Boat */
        properties = new VehicleProperties();
        properties.setWheelOffset(2.5F);
        properties.setBodyPosition(new PartPosition(0.0, -0.03125, 0.6875, 1.0));
        properties.setFuelPortPosition(new PartPosition(-12.25, 6.0, -19.5, 0.0, -90.0, 0.0, 0.25));
        properties.setHeldOffset(new Vec3d(6.0, -0.5, 0.0));
        properties.setTrailerOffset(new Vec3d(0.0, -0.09375, -0.75));
        VehicleProperties.setProperties(ModEntities.SPEED_BOAT, properties);

        /* Sports Plane */
        properties = new VehicleProperties();
        properties.setBodyPosition(new PartPosition(0, 11 * 0.0625, -8 * 0.0625, 0, 0, 0, 1.8));
        properties.setFuelPortPosition(new PartPosition(-6.25, 4, -1, 0, -90, 0, 0.25));
        properties.setKeyPortPosition(new PartPosition(0, 3.75, 12.5, -67.5, 0, 0, 0.5));
        VehicleProperties.setProperties(ModEntities.SPORTS_PLANE, properties);

        /* Tractor */
        properties = new VehicleProperties();
        properties.setAxleOffset(-3.0F);
        properties.setWheelOffset(5.5F);
        properties.setBodyPosition(new PartPosition(0, 0, 0.25, 0, 0, 0, 1.0));
        properties.setEnginePosition(new PartPosition(0, 6, 8.775, 0, 0, 0, 0.85));
        properties.setFuelPortPosition(new PartPosition(-6.25, 9.5, -1.75, 0, -90, 0, 0.3));
        properties.setKeyPortPosition(new PartPosition(0, 7, 6.2, -67.5, 0, 0, 0.5));
        properties.setHeldOffset(new Vec3d(0.0, 3.5, 0.0));
        properties.setTowBarPosition(new Vec3d(0.0, 0.0, -24.5));
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 8.0F, 0.0F, 14.0F, 1.5F, 2.25F, 2.25F, true, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 8.0F, 0.0F, 14.0F, 1.5F, 2.25F, 2.25F, true, true);
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 8.0F, 5.5F, -14.5F, 3.0F, 4.5F, 4.5F, true, true);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 8.0F, 5.5F, -14.5F, 3.0F, 4.5F, 4.5F, true, true);
        VehicleProperties.setProperties(ModEntities.TRACTOR, properties);

        /* Fertilizer Trailer */
        properties = new VehicleProperties();
        properties.setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1));
        VehicleProperties.setProperties(ModEntities.FERTILIZER, properties);

        /* Fluid Trailer */
        properties = new VehicleProperties();
        properties.setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1));
        properties.setHeldOffset(new Vec3d(0D, 3D, 0D));
        VehicleProperties.setProperties(ModEntities.FLUID_TRAILER, properties);

        /* Seeder Trailer */
        properties = new VehicleProperties();
        properties.setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1));
        VehicleProperties.setProperties(ModEntities.SEEDER, properties);

        /* Storage Trailer */
        properties = new VehicleProperties();
        properties.setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1));
        properties.setTowBarPosition(new Vec3d(0.0, 0.0, -12.0));
        VehicleProperties.setProperties(ModEntities.STORAGE_TRAILER, properties);

        /* Vehicle Trailer */
        properties = new VehicleProperties();
        properties.setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1));
        properties.setHeldOffset(new Vec3d(0D, 3D, 0D));
        VehicleProperties.setProperties(ModEntities.VEHICLE_TRAILER, properties);
    }

    private float axleOffset;
    private float wheelOffset;
    private Vec3d heldOffset = Vec3d.ZERO;
    private Vec3d towBarVec = Vec3d.ZERO;
    private Vec3d trailerOffset = Vec3d.ZERO;
    private List<Wheel> wheels = new ArrayList<>();
    private PartPosition bodyPosition = PartPosition.DEFAULT;
    private PartPosition enginePosition;
    private PartPosition fuelPortPosition;
    private PartPosition fuelPortLidPosition;
    private PartPosition keyPortPosition;
    private PartPosition keyPosition;

    public void setAxleOffset(float axleOffset)
    {
        this.axleOffset = axleOffset;
    }

    public float getAxleOffset()
    {
        return axleOffset;
    }

    public void setWheelOffset(float wheelOffset)
    {
        this.wheelOffset = wheelOffset;
    }

    public float getWheelOffset()
    {
        return wheelOffset;
    }

    public void setHeldOffset(Vec3d heldOffset)
    {
        this.heldOffset = heldOffset;
    }

    public Vec3d getHeldOffset()
    {
        return heldOffset;
    }

    public void setTowBarPosition(Vec3d towBarVec)
    {
        this.towBarVec = towBarVec;
    }

    public Vec3d getTowBarPosition()
    {
        return towBarVec;
    }

    public void setTrailerOffset(Vec3d trailerOffset)
    {
        this.trailerOffset = trailerOffset;
    }

    public Vec3d getTrailerOffset()
    {
        return trailerOffset;
    }

    public void addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetZ, float scale, boolean particles, boolean render)
    {
        wheels.add(new Wheel(side, position, 2.0F, scale, scale, scale, offsetX, 0F, offsetZ, particles, render));
    }

    public void addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetY, float offsetZ, float scale, boolean particles, boolean render)
    {
        wheels.add(new Wheel(side, position, 2.0F, scale, scale, scale, offsetX, offsetY, offsetZ, particles, render));
    }

    public void addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetY, float offsetZ, float scaleX, float scaleY, float scaleZ, boolean particles, boolean render)
    {
        wheels.add(new Wheel(side, position, 2.0F, scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, particles, render));
    }

    public List<Wheel> getWheels()
    {
        return wheels;
    }

    @Nullable
    public Wheel getFirstFrontWheel()
    {
        return wheels.stream().filter(wheel -> wheel.getPosition() == Wheel.Position.FRONT).findFirst().orElse(null);
    }

    @Nullable
    public Wheel getFirstRearWheel()
    {
        return wheels.stream().filter(wheel -> wheel.getPosition() == Wheel.Position.REAR).findFirst().orElse(null);
    }

    public void setBodyPosition(PartPosition bodyPosition)
    {
        this.bodyPosition = bodyPosition;
    }

    public PartPosition getBodyPosition()
    {
        return bodyPosition;
    }

    public void setEnginePosition(PartPosition enginePosition)
    {
        this.enginePosition = enginePosition;
    }

    public PartPosition getEnginePosition()
    {
        return enginePosition;
    }

    public void setFuelPortPosition(PartPosition fuelPortPosition)
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
    }

    public PartPosition getFuelPortPosition()
    {
        return fuelPortPosition;
    }

    public void setFuelPortLidPosition(PartPosition fuelPortLidPosition)
    {
        this.fuelPortLidPosition = fuelPortLidPosition;
    }

    public PartPosition getFuelPortLidPosition()
    {
        return fuelPortLidPosition;
    }

    public void setKeyPortPosition(PartPosition keyPortPosition)
    {
        this.keyPortPosition = keyPortPosition;
        this.keyPosition = new PartPosition(keyPortPosition.getX(), keyPortPosition.getY(), keyPortPosition.getZ(), keyPortPosition.getRotX() + 90, 0, 0, 0.15);
    }

    public PartPosition getKeyPortPosition()
    {
        return keyPortPosition;
    }

    public void setKeyPosition(PartPosition keyPosition)
    {
        this.keyPosition = keyPosition;
    }

    public PartPosition getKeyPosition()
    {
        return keyPosition;
    }
}
