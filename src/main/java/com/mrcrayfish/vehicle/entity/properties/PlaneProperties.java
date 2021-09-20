package com.mrcrayfish.vehicle.entity.properties;

import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
import net.minecraft.util.JSONUtils;

/**
 * Author: MrCrayfish
 */
public final class PlaneProperties extends ExtendedProperties
{
    public static final float DEFAULT_MINIMUM_SPEED_TO_TAKE_OFF = 16F;
    public static final float DEFAULT_MAX_FLAP_ANGLE = 35F;
    public static final float DEFAULT_FLAP_STRENGTH = 0.25F;
    public static final float DEFAULT_FLAP_SENSITIVITY = 0.05F;
    public static final float DEFAULT_MAX_ELEVATOR_ANGLE = 45F;
    public static final float DEFAULT_ELEVATOR_STRENGTH = 0.15F;
    public static final float DEFAULT_ELEVATOR_SENSITIVITY = 0.025F;
    public static final float DEFAULT_MAX_TURN_ANGLE = 0.5F;

    private final float minimumSpeedToTakeOff;
    private final float maxFlapAngle;
    private final float flapStrength;
    private final float flapSensitivity;
    private final float maxElevatorAngle;
    private final float elevatorStrength;
    private final float elevatorSensitivity;
    private final float maxTurnAngle;

    public PlaneProperties(JsonObject object)
    {
        this.minimumSpeedToTakeOff = JSONUtils.getAsFloat(object, "minimumSpeedToTakeOff", DEFAULT_MINIMUM_SPEED_TO_TAKE_OFF); //TODO specify the defaults somewhere
        this.maxFlapAngle = JSONUtils.getAsFloat(object, "maxFlapAngle", DEFAULT_MAX_FLAP_ANGLE);
        this.flapStrength = JSONUtils.getAsFloat(object, "flapStrength", DEFAULT_FLAP_STRENGTH);
        this.flapSensitivity = JSONUtils.getAsFloat(object, "flapSensitivity", DEFAULT_FLAP_SENSITIVITY);
        this.maxElevatorAngle = JSONUtils.getAsFloat(object, "maxElevatorAngle", DEFAULT_MAX_ELEVATOR_ANGLE);
        this.elevatorStrength = JSONUtils.getAsFloat(object, "elevatorStrength", DEFAULT_ELEVATOR_STRENGTH);
        this.elevatorSensitivity = JSONUtils.getAsFloat(object, "elevatorSensitivity", DEFAULT_ELEVATOR_SENSITIVITY);
        this.maxTurnAngle = JSONUtils.getAsFloat(object, "maxTurnAngle", DEFAULT_MAX_TURN_ANGLE);
    }

    public PlaneProperties(float minimumSpeedToTakeOff, float maxFlapAngle, float flapStrength, float flapSensitivity, float maxElevatorAngle, float elevatorStrength, float elevatorSensitivity, float maxTurnAngle)
    {
        this.minimumSpeedToTakeOff = minimumSpeedToTakeOff;
        this.maxFlapAngle = maxFlapAngle;
        this.flapStrength = flapStrength;
        this.flapSensitivity = flapSensitivity;
        this.maxElevatorAngle = maxElevatorAngle;
        this.elevatorStrength = elevatorStrength;
        this.elevatorSensitivity = elevatorSensitivity;
        this.maxTurnAngle = maxTurnAngle;
    }

    @Override
    public void serialize(JsonObject object)
    {
        ExtraJSONUtils.write(object, "minimumSpeedToTakeOff", this.minimumSpeedToTakeOff, DEFAULT_MINIMUM_SPEED_TO_TAKE_OFF);
        ExtraJSONUtils.write(object, "maxFlapAngle", this.maxFlapAngle, DEFAULT_MAX_FLAP_ANGLE);
        ExtraJSONUtils.write(object, "flapStrength", this.flapStrength, DEFAULT_FLAP_STRENGTH);
        ExtraJSONUtils.write(object, "flapSensitivity", this.flapSensitivity, DEFAULT_FLAP_SENSITIVITY);
        ExtraJSONUtils.write(object, "maxElevatorAngle", this.maxElevatorAngle, DEFAULT_MAX_ELEVATOR_ANGLE);
        ExtraJSONUtils.write(object, "elevatorStrength", this.elevatorStrength, DEFAULT_ELEVATOR_STRENGTH);
        ExtraJSONUtils.write(object, "elevatorSensitivity", this.elevatorSensitivity, DEFAULT_ELEVATOR_SENSITIVITY);
        ExtraJSONUtils.write(object, "maxTurnAngle", this.maxTurnAngle, DEFAULT_MAX_TURN_ANGLE);
    }

    public float getMinimumSpeedToTakeOff()
    {
        return this.minimumSpeedToTakeOff;
    }

    public float getMaxFlapAngle()
    {
        return this.maxFlapAngle;
    }

    public float getFlapStrength()
    {
        return this.flapStrength;
    }

    public float getFlapSensitivity()
    {
        return this.flapSensitivity;
    }

    public float getMaxElevatorAngle()
    {
        return this.maxElevatorAngle;
    }

    public float getElevatorStrength()
    {
        return this.elevatorStrength;
    }

    public float getElevatorSensitivity()
    {
        return this.elevatorSensitivity;
    }

    public float getMaxTurnAngle()
    {
        return this.maxTurnAngle;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public final static class Builder
    {
        private float minimumSpeedToTakeOff = DEFAULT_MINIMUM_SPEED_TO_TAKE_OFF;
        private float maxFlapAngle = DEFAULT_MAX_FLAP_ANGLE;
        private float flapStrength = DEFAULT_FLAP_STRENGTH;
        private float flapSensitivity = DEFAULT_FLAP_SENSITIVITY;
        private float maxElevatorAngle = DEFAULT_MAX_ELEVATOR_ANGLE;
        private float elevatorStrength = DEFAULT_ELEVATOR_STRENGTH;
        private float elevatorSensitivity = DEFAULT_ELEVATOR_SENSITIVITY;
        private float maxTurnAngle = DEFAULT_MAX_TURN_ANGLE;

        private Builder() {}

        public Builder setMinimumSpeedToTakeOff(float minimumSpeedToTakeOff)
        {
            this.minimumSpeedToTakeOff = minimumSpeedToTakeOff;
            return this;
        }

        public Builder setMaxFlapAngle(float maxFlapAngle)
        {
            this.maxFlapAngle = maxFlapAngle;
            return this;
        }

        public Builder setFlapStrength(float flapStrength)
        {
            this.flapStrength = flapStrength;
            return this;
        }

        public Builder setFlapSensitivity(float flapSensitivity)
        {
            this.flapSensitivity = flapSensitivity;
            return this;
        }

        public Builder setMaxElevatorAngle(float maxElevatorAngle)
        {
            this.maxElevatorAngle = maxElevatorAngle;
            return this;
        }

        public Builder setElevatorStrength(float elevatorStrength)
        {
            this.elevatorStrength = elevatorStrength;
            return this;
        }

        public Builder setElevatorSensitivity(float elevatorSensitivity)
        {
            this.elevatorSensitivity = elevatorSensitivity;
            return this;
        }

        public Builder setMaxTurnAngle(float maxTurnAngle)
        {
            this.maxTurnAngle = maxTurnAngle;
            return this;
        }

        public PlaneProperties build()
        {
            return new PlaneProperties(this.minimumSpeedToTakeOff, this.maxFlapAngle, this.flapStrength, this.flapSensitivity, this.maxElevatorAngle, this.elevatorStrength, this.elevatorSensitivity, this.maxTurnAngle);
        }
    }
}
