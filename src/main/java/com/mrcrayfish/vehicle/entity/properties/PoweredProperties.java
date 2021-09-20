package com.mrcrayfish.vehicle.entity.properties;

import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.common.entity.Transform;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.FuelFillerType;
import com.mrcrayfish.vehicle.entity.IEngineType;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public final class PoweredProperties extends ExtendedProperties
{
    public static final boolean DEFAULT_CAN_DRIVE_UP_BLOCKS = true;
    public static final float DEFAULT_MAX_STEERING_ANGLE = 35F;
    public static final boolean DEFAULT_REQUIRES_ENERGY = true;
    public static final float DEFAULT_ENERGY_CAPACITY = 15000F;
    public static final float DEFAULT_ENERGY_CONSUMPTION_PER_TICK = 0.25F;
    public static final IEngineType DEFAULT_ENGINE_TYPE = EngineType.NONE;
    public static final float DEFAULT_ENGINE_POWER = 15F;
    public static final float DEFAULT_MIN_ENGINE_PITCH = 0.5F;
    public static final float DEFAULT_MAX_ENGINE_PITCH = 1.25F;
    public static final boolean DEFAULT_RENDER_ENGINE = false;
    public static final Transform DEFAULT_ENGINE_TRANSFORM = Transform.DEFAULT;
    public static final Transform DEFAULT_EXHAUST_TRANSFORM = Transform.DEFAULT;
    public static final boolean DEFAULT_SHOW_EXHAUST_FUMES = false;
    public static final Vector3d DEFAULT_EXHAUST_FUMES_POSITION = Vector3d.ZERO;
    public static final Transform DEFAULT_FUEL_FILLER_TRANSFORM = Transform.DEFAULT;
    public static final FuelFillerType DEFAULT_FUEL_FILLER_TYPE = FuelFillerType.DEFAULT;
    public static final Transform DEFAULT_IGNITION_TRANSFORM = Transform.DEFAULT;
    public static final boolean DEFAULT_HAS_HORN = true;
    public static final boolean DEFAULT_CAN_LOCK_WITH_KEY = true;
    public static final Vector3d DEFAULT_FRONT_AXLE_POSITION = Vector3d.ZERO;
    public static final Vector3d DEFAULT_REAR_AXLE_POSITION = Vector3d.ZERO;
    public static final ResourceLocation DEFAULT_ENGINE_SOUND = null;
    public static final ResourceLocation DEFAULT_HORN_SOUND = new ResourceLocation(Reference.MOD_ID, "entity.vehicle.horn");

    private final boolean canDriveUpBlocks;
    private final float maxSteeringAngle;
    private final boolean requiresEnergy;
    private final float energyCapacity;
    private final float energyConsumptionPerTick;
    private final IEngineType engineType;
    private final float enginePower;
    private final float minEnginePitch;
    private final float maxEnginePitch;
    private final boolean renderEngine;
    private final Transform engineTransform;
    private final Transform exhaustTransform;
    private final boolean showExhaustFumes;
    private final Vector3d exhaustFumesPosition;
    private final Transform fuelFillerTransform;
    private final FuelFillerType fuelFillerType;
    private final Transform ignitionTransform;
    private final boolean hasHorn;
    private final boolean canLockWithKey;
    private final Vector3d frontAxleOffset;
    private final Vector3d rearAxleOffset;
    private final ResourceLocation engineSound;
    private final ResourceLocation hornSound;

    public PoweredProperties(JsonObject object)
    {
        this.canDriveUpBlocks = JSONUtils.getAsBoolean(object, "canDriveUpBlocks", DEFAULT_CAN_DRIVE_UP_BLOCKS);
        this.maxSteeringAngle = JSONUtils.getAsFloat(object, "maxSteeringAngle", DEFAULT_MAX_STEERING_ANGLE);
        this.requiresEnergy = JSONUtils.getAsBoolean(object, "requiresEnergy", DEFAULT_REQUIRES_ENERGY);
        this.energyCapacity = JSONUtils.getAsFloat(object, "energyCapacity", DEFAULT_ENERGY_CAPACITY);
        this.energyConsumptionPerTick = JSONUtils.getAsFloat(object, "energyConsumptionPerTick", DEFAULT_ENERGY_CONSUMPTION_PER_TICK);
        this.engineType = ExtraJSONUtils.getAsEngineType(object, "engineType", DEFAULT_ENGINE_TYPE);
        this.enginePower = JSONUtils.getAsFloat(object, "enginePower", DEFAULT_ENGINE_POWER);
        this.minEnginePitch = JSONUtils.getAsFloat(object, "minEnginePitch", DEFAULT_MIN_ENGINE_PITCH);
        this.maxEnginePitch = JSONUtils.getAsFloat(object, "maxEnginePitch", DEFAULT_MAX_ENGINE_PITCH);
        this.renderEngine = JSONUtils.getAsBoolean(object, "renderEngine", DEFAULT_RENDER_ENGINE);
        this.engineTransform = ExtraJSONUtils.getAsTransform(object, "engineTransform", DEFAULT_ENGINE_TRANSFORM);
        this.exhaustTransform = ExtraJSONUtils.getAsTransform(object, "exhaustTransform", DEFAULT_EXHAUST_TRANSFORM);
        this.showExhaustFumes = JSONUtils.getAsBoolean(object, "showExhaustFumes", DEFAULT_SHOW_EXHAUST_FUMES);
        this.exhaustFumesPosition = ExtraJSONUtils.getAsVector3d(object, "exhaustFumesPosition", DEFAULT_EXHAUST_FUMES_POSITION);
        this.fuelFillerTransform = ExtraJSONUtils.getAsTransform(object, "fuelFillerTransform", DEFAULT_FUEL_FILLER_TRANSFORM);
        this.fuelFillerType = ExtraJSONUtils.getAsEnum(object, "fuelFillerType", FuelFillerType.class, FuelFillerType.DEFAULT);
        this.ignitionTransform = ExtraJSONUtils.getAsTransform(object, "ignitionTransform", DEFAULT_IGNITION_TRANSFORM);
        this.hasHorn = JSONUtils.getAsBoolean(object, "hasHorn", DEFAULT_HAS_HORN);
        this.canLockWithKey = JSONUtils.getAsBoolean(object, "canLockWithKey", DEFAULT_CAN_LOCK_WITH_KEY);
        this.frontAxleOffset = new Vector3d(0, 0, JSONUtils.getAsFloat(object, "frontAxleOffset", 0F));
        this.rearAxleOffset = new Vector3d(0, 0, JSONUtils.getAsFloat(object, "rearAxleOffset", 0F));
        this.engineSound = ExtraJSONUtils.getAsResourceLocation(object, "engineSound", DEFAULT_ENGINE_SOUND);
        this.hornSound = ExtraJSONUtils.getAsResourceLocation(object, "hornSound", DEFAULT_HORN_SOUND);
    }

    public PoweredProperties(boolean canDriveUpBlocks, float maxSteeringAngle, boolean requiresEnergy, float energyCapacity, float energyConsumptionPerTick, IEngineType engineType, float enginePower, float minEnginePitch, float maxEnginePitch, boolean renderEngine, Transform engineTransform, Transform exhaustTransform, boolean showExhaustFumes, Vector3d exhaustFumesPosition, Transform fuelFillerTransform, FuelFillerType fuelFillerType, Transform ignitionTransform, boolean hasHorn, boolean canLockWithKey, Vector3d frontAxleOffset, Vector3d rearAxleOffset, ResourceLocation engineSound, ResourceLocation hornSound)
    {
        this.canDriveUpBlocks = canDriveUpBlocks;
        this.maxSteeringAngle = maxSteeringAngle;
        this.requiresEnergy = requiresEnergy;
        this.energyCapacity = energyCapacity;
        this.energyConsumptionPerTick = energyConsumptionPerTick;
        this.engineType = engineType;
        this.enginePower = enginePower;
        this.minEnginePitch = minEnginePitch;
        this.maxEnginePitch = maxEnginePitch;
        this.renderEngine = renderEngine;
        this.engineTransform = engineTransform;
        this.exhaustTransform = exhaustTransform;
        this.showExhaustFumes = showExhaustFumes;
        this.exhaustFumesPosition = exhaustFumesPosition;
        this.fuelFillerTransform = fuelFillerTransform;
        this.fuelFillerType = fuelFillerType;
        this.ignitionTransform = ignitionTransform;
        this.hasHorn = hasHorn;
        this.canLockWithKey = canLockWithKey;
        this.frontAxleOffset = frontAxleOffset;
        this.rearAxleOffset = rearAxleOffset;
        this.engineSound = engineSound;
        this.hornSound = hornSound;
    }

    public boolean canDriveUpBlocks()
    {
        return this.canDriveUpBlocks;
    }

    public float getMaxSteeringAngle()
    {
        return this.maxSteeringAngle;
    }

    public boolean requiresEnergy()
    {
        return this.requiresEnergy;
    }

    public float getEnergyCapacity()
    {
        return this.energyCapacity;
    }

    public float getEnergyConsumptionPerTick()
    {
        return this.energyConsumptionPerTick;
    }

    public IEngineType getEngineType()
    {
        return this.engineType;
    }

    public float getEnginePower()
    {
        return this.enginePower;
    }

    public float getMinEnginePitch()
    {
        return this.minEnginePitch;
    }

    public float getMaxEnginePitch()
    {
        return this.maxEnginePitch;
    }

    public boolean isRenderEngine()
    {
        return this.renderEngine;
    }

    public Transform getEngineTransform()
    {
        return this.engineTransform;
    }

    public Transform getExhaustTransform()
    {
        return this.exhaustTransform;
    }

    public boolean showExhaustFumes()
    {
        return this.showExhaustFumes;
    }

    public Vector3d getExhaustFumesPosition()
    {
        return this.exhaustFumesPosition;
    }

    public Transform getFuelFillerTransform()
    {
        return this.fuelFillerTransform;
    }

    public FuelFillerType getFuelFillerType()
    {
        return this.fuelFillerType;
    }

    public Transform getIgnitionTransform()
    {
        return this.ignitionTransform;
    }

    public boolean hasHorn()
    {
        return this.hasHorn;
    }

    public boolean canLockWithKey()
    {
        return this.canLockWithKey;
    }

    public Vector3d getFrontAxleOffset()
    {
        return this.frontAxleOffset;
    }

    public Vector3d getRearAxleOffset()
    {
        return this.rearAxleOffset;
    }

    @Nullable
    public ResourceLocation getEngineSound()
    {
        return this.engineSound;
    }

    public ResourceLocation getHornSound()
    {
        return this.hornSound;
    }

    @Override
    public void serialize(JsonObject object)
    {
        ExtraJSONUtils.write(object, "canDriveUpBlocks", this.canDriveUpBlocks, DEFAULT_CAN_DRIVE_UP_BLOCKS);
        ExtraJSONUtils.write(object, "maxSteeringAngle", this.maxSteeringAngle, DEFAULT_MAX_STEERING_ANGLE);
        ExtraJSONUtils.write(object, "requiresEnergy", this.requiresEnergy, DEFAULT_REQUIRES_ENERGY);
        ExtraJSONUtils.write(object, "energyCapacity", this.energyCapacity, DEFAULT_ENERGY_CAPACITY);
        ExtraJSONUtils.write(object, "energyConsumptionPerTick", this.energyConsumptionPerTick, DEFAULT_ENERGY_CONSUMPTION_PER_TICK);
        ExtraJSONUtils.write(object, "engineType", this.engineType, DEFAULT_ENGINE_TYPE);
        ExtraJSONUtils.write(object, "enginePower", this.enginePower, DEFAULT_ENGINE_POWER);
        ExtraJSONUtils.write(object, "minEnginePitch", this.minEnginePitch, DEFAULT_MIN_ENGINE_PITCH);
        ExtraJSONUtils.write(object, "maxEnginePitch", this.maxEnginePitch, DEFAULT_MAX_ENGINE_PITCH);
        ExtraJSONUtils.write(object, "renderEngine", this.renderEngine, DEFAULT_RENDER_ENGINE);
        ExtraJSONUtils.write(object, "engineTransform", this.engineTransform, DEFAULT_ENGINE_TRANSFORM);
        ExtraJSONUtils.write(object, "exhaustTransform", this.exhaustTransform, DEFAULT_EXHAUST_TRANSFORM);
        ExtraJSONUtils.write(object, "showExhaustFumes", this.showExhaustFumes, DEFAULT_SHOW_EXHAUST_FUMES);
        ExtraJSONUtils.write(object, "exhaustFumesPosition", this.exhaustFumesPosition, DEFAULT_EXHAUST_FUMES_POSITION);
        ExtraJSONUtils.write(object, "fuelFillerTransform", this.fuelFillerTransform, DEFAULT_FUEL_FILLER_TRANSFORM);
        ExtraJSONUtils.write(object, "fuelFillerType", this.fuelFillerType, DEFAULT_FUEL_FILLER_TYPE);
        ExtraJSONUtils.write(object, "ignitionTransform", this.ignitionTransform, DEFAULT_IGNITION_TRANSFORM);
        ExtraJSONUtils.write(object, "hasHorn", this.hasHorn, DEFAULT_HAS_HORN);
        ExtraJSONUtils.write(object, "canLockWithKey", this.canLockWithKey, DEFAULT_CAN_LOCK_WITH_KEY);
        ExtraJSONUtils.write(object, "frontAxleOffset", this.frontAxleOffset.z, DEFAULT_FRONT_AXLE_POSITION.z);
        ExtraJSONUtils.write(object, "rearAxleOffset", this.rearAxleOffset.z, DEFAULT_REAR_AXLE_POSITION.z);
        ExtraJSONUtils.write(object, "engineSound", this.engineSound, DEFAULT_ENGINE_SOUND);
        ExtraJSONUtils.write(object, "hornSound", this.hornSound, DEFAULT_HORN_SOUND);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private boolean canDriveUpBlocks = DEFAULT_CAN_DRIVE_UP_BLOCKS;
        private float maxSteeringAngle = DEFAULT_MAX_STEERING_ANGLE;
        private boolean requiresEnergy = DEFAULT_REQUIRES_ENERGY;
        private float energyCapacity = DEFAULT_ENERGY_CAPACITY;
        private float energyConsumptionPerTick = DEFAULT_ENERGY_CONSUMPTION_PER_TICK;
        private IEngineType engineType = DEFAULT_ENGINE_TYPE;
        private float enginePower = DEFAULT_ENGINE_POWER;
        private float minEnginePitch = DEFAULT_MIN_ENGINE_PITCH;
        private float maxEnginePitch = DEFAULT_MAX_ENGINE_PITCH;
        private boolean renderEngine = DEFAULT_RENDER_ENGINE;
        private Transform engineTransform = DEFAULT_ENGINE_TRANSFORM;
        private Transform exhaustTransform = DEFAULT_EXHAUST_TRANSFORM;
        private boolean showExhaustFumes = DEFAULT_SHOW_EXHAUST_FUMES;
        private Vector3d exhaustFumesPosition = DEFAULT_EXHAUST_FUMES_POSITION;
        private Transform fuelFillerTransform = DEFAULT_FUEL_FILLER_TRANSFORM;
        private FuelFillerType fuelFillerType = DEFAULT_FUEL_FILLER_TYPE;
        private Transform ignitionTransform = DEFAULT_IGNITION_TRANSFORM;
        private boolean hasHorn = DEFAULT_HAS_HORN;
        private boolean canLockWithKey = DEFAULT_CAN_LOCK_WITH_KEY;
        private Vector3d frontAxleOffset = DEFAULT_FRONT_AXLE_POSITION;
        private Vector3d rearAxleOffset = DEFAULT_REAR_AXLE_POSITION;
        private ResourceLocation engineSound = DEFAULT_ENGINE_SOUND;
        private ResourceLocation hornSound = DEFAULT_HORN_SOUND;

        private Builder() {}

        public Builder setCanDriveUpBlocks(boolean canDriveUpBlocks)
        {
            this.canDriveUpBlocks = canDriveUpBlocks;
            return this;
        }

        public Builder setMaxSteeringAngle(float maxSteeringAngle)
        {
            this.maxSteeringAngle = maxSteeringAngle;
            return this;
        }

        public Builder setRequiresEnergy(boolean requiresEnergy)
        {
            this.requiresEnergy = requiresEnergy;
            return this;
        }

        public Builder setEnergyCapacity(float energyCapacity)
        {
            this.energyCapacity = energyCapacity;
            return this;
        }

        public Builder setEnergyConsumptionPerTick(float energyConsumptionPerTick)
        {
            this.energyConsumptionPerTick = energyConsumptionPerTick;
            return this;
        }

        public Builder setEngineType(IEngineType engineType)
        {
            this.engineType = engineType;
            return this;
        }

        public Builder setEnginePower(float enginePower)
        {
            this.enginePower = enginePower;
            return this;
        }

        public Builder setMinEnginePitch(float minEnginePitch)
        {
            this.minEnginePitch = minEnginePitch;
            return this;
        }

        public Builder setMaxEnginePitch(float maxEnginePitch)
        {
            this.maxEnginePitch = maxEnginePitch;
            return this;
        }

        public Builder setRenderEngine(boolean renderEngine)
        {
            this.renderEngine = renderEngine;
            return this;
        }

        public Builder setEngineTransform(Transform engineTransform)
        {
            this.engineTransform = engineTransform;
            return this;
        }

        public Builder setExhaustTransform(Transform exhaustTransform)
        {
            this.exhaustTransform = exhaustTransform;
            return this;
        }

        public Builder setShowExhaustFumes(boolean showExhaustFumes)
        {
            this.showExhaustFumes = showExhaustFumes;
            return this;
        }

        public Builder setExhaustFumesPosition(double x, double y, double z)
        {
            this.exhaustFumesPosition = new Vector3d(x, y, z);
            return this;
        }

        public Builder setFuelFillerTransform(Transform fuelFillerTransform)
        {
            this.fuelFillerTransform = fuelFillerTransform;
            return this;
        }

        public Builder setFuelFillerType(FuelFillerType fuelFillerType)
        {
            this.fuelFillerType = fuelFillerType;
            return this;
        }

        public Builder setIgnitionTransform(Transform ignitionTransform)
        {
            this.ignitionTransform = ignitionTransform;
            return this;
        }

        public Builder setHasHorn(boolean hasHorn)
        {
            this.hasHorn = hasHorn;
            return this;
        }

        public Builder setCanLockWithKey(boolean canLockWithKey)
        {
            this.canLockWithKey = canLockWithKey;
            return this;
        }

        public Builder setFrontAxleOffset(double frontAxleOffset)
        {
            this.frontAxleOffset = new Vector3d(0, 0, frontAxleOffset);
            return this;
        }

        public Builder setRearAxleOffset(double rearAxleOffset)
        {
            this.rearAxleOffset = new Vector3d(0, 0, rearAxleOffset);
            return this;
        }

        public Builder setEngineSound(ResourceLocation engineSound)
        {
            this.engineSound = engineSound;
            return this;
        }

        public Builder setHornSound(ResourceLocation hornSound)
        {
            this.hornSound = hornSound;
            return this;
        }

        public PoweredProperties build()
        {
            return new PoweredProperties(this.canDriveUpBlocks, this.maxSteeringAngle, this.requiresEnergy, this.energyCapacity, this.energyConsumptionPerTick, this.engineType, this.enginePower, this.minEnginePitch, this.maxEnginePitch, this.renderEngine, this.engineTransform, this.exhaustTransform, this.showExhaustFumes, this.exhaustFumesPosition, this.fuelFillerTransform, this.fuelFillerType, this.ignitionTransform, this.hasHorn, this.canLockWithKey, this.frontAxleOffset, this.rearAxleOffset, this.engineSound, this.hornSound);
        }
    }
}
