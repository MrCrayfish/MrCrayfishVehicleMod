package com.mrcrayfish.vehicle.entity;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
public class Wheel
{
    private Vector3d offset;
    private Vector3d scale;
    private float width;
    private Side side;
    private Position position;

    private boolean autoScale;
    private boolean particles;
    private boolean render;

    public Wheel(Side side, Position position, float width, float scaleX, float scaleY, float scaleZ, float offsetX, float offsetY, float offsetZ, boolean autoScale, boolean particles, boolean render)
    {
        this.offset = new Vector3d(offsetX, offsetY, offsetZ);
        this.scale = new Vector3d(scaleX, scaleY, scaleZ);
        this.width = width;
        this.side = side;
        this.position = position;
        this.autoScale = autoScale;
        this.particles = particles;
        this.render = render;
    }

    @OnlyIn(Dist.CLIENT)
    public float getWheelRotation(LandVehicleEntity vehicle, float partialTicks)
    {
        return this.position == Position.REAR ? vehicle.getRearWheelRotation(partialTicks) : vehicle.getFrontWheelRotation(partialTicks);
    }

    public Vector3d getOffset()
    {
        return this.offset;
    }

    public Vector3d getScale()
    {
        return this.scale;
    }

    public float getOffsetX()
    {
        return (float) this.offset.x;
    }

    public float getOffsetY()
    {
        return (float) this.offset.y;
    }

    public float getOffsetZ()
    {
        return (float) this.offset.z;
    }

    public float getWidth()
    {
        return width;
    }

    public float getScaleX()
    {
        return (float) this.scale.x;
    }

    public float getScaleY()
    {
        return (float) this.scale.y;
    }

    public float getScaleZ()
    {
        return (float) this.scale.z;
    }

    public Side getSide()
    {
        return side;
    }

    public Position getPosition()
    {
        return position;
    }

    void updateScale(double scale)
    {
        double xScale = this.scale.x != 0.0 ? this.scale.x : scale;
        double yScale = this.scale.y != 0.0 ? this.scale.y : scale;
        double zScale = this.scale.z != 0.0 ? this.scale.z : scale;
        this.scale = new Vector3d(xScale, yScale, zScale);
    }

    /**
     * Indicates that the wheel scale is to be generated. This is only used when loading vehicle
     * properties and has no other significant use.
     *
     * @return true if the wheel auto scaled
     */
    public boolean isAutoScale()
    {
        return this.autoScale;
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
