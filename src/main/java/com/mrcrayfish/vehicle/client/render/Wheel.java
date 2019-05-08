package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.EntityLandVehicle;

/**
 * Author: MrCrayfish
 */
public class Wheel
{
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private float width;
    private float scaleX;
    private float scaleY;
    private float scaleZ;
    private Side side;
    private Position position;

    private boolean particles;
    private boolean render;

    public Wheel(Side side, Position position, float width, float scaleX, float scaleY, float scaleZ, float offsetX, float offsetY, float offsetZ, boolean particles, boolean render)
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.width = width;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.side = side;
        this.position = position;
        this.particles = particles;
        this.render = render;
    }

    public float getWheelRotation(EntityLandVehicle vehicle, float partialTicks)
    {
        if(position == Position.REAR)
        {
            return vehicle.prevRearWheelRotation + (vehicle.rearWheelRotation - vehicle.prevRearWheelRotation) * partialTicks;
        }
        return vehicle.prevFrontWheelRotation + (vehicle.frontWheelRotation - vehicle.prevFrontWheelRotation) * partialTicks;
    }

    public float getOffsetX()
    {
        return offsetX;
    }

    public float getOffsetY()
    {
        return offsetY;
    }

    public float getOffsetZ()
    {
        return offsetZ;
    }

    public float getWidth()
    {
        return width;
    }

    public float getScaleX()
    {
        return scaleX;
    }

    public float getScaleY()
    {
        return scaleY;
    }

    public float getScaleZ()
    {
        return scaleZ;
    }

    public Side getSide()
    {
        return side;
    }

    public Position getPosition()
    {
        return position;
    }

    /**
     * Determines if this wheels should spawn particles. Depending on the drivetrain of a vehicle,
     * the spawning of particles can be disabled. For instance, a rear wheel drive vehicle will only
     * spawn particles for the rear wheels as that's where the force to push the vehicle comes from.
     * It should be noted that there is no system in place that determines the drivetrain of a vehicle
     * and the spawning of particles is specified when adding wheels.
     *
     * @return if the wheel should spawn particles
     */
    public boolean shouldSpawnParticles()
    {
        return particles;
    }

    /**
     * Determines if this wheel should render. Some vehicles have wheels that are manually rendered
     * due the fact they need extra tranformations and rotations, and therefore shouldn't use the
     * wheel system and rather just be a placeholder.
     *
     * @return if the wheel should be rendered
     */
    public boolean shouldRender()
    {
        return render;
    }

    public enum Side
    {
        LEFT(-1), RIGHT(1), NONE(0);

        int offset;

        Side(int offset)
        {
            this.offset = offset;
        }

        public int getOffset()
        {
            return offset;
        }
    }

    public enum Position
    {
        FRONT, REAR, NONE
    }
}
