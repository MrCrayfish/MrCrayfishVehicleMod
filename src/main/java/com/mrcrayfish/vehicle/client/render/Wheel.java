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
    private float scale;
    private Side side;
    private Position position;

    public Wheel(Side side, Position position, float width, float scale, float offsetX, float offsetY, float offsetZ)
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.width = width;
        this.scale = scale;
        this.side = side;
        this.position = position;
    }

    public Wheel(Side side, Position position, float offsetX, float offsetZ)
    {
        this(side, position, 2.0F, 1.0F, offsetX, 0F, offsetZ);
    }

    public Wheel(Side side, Position position, float offsetX, float offsetZ, float scale)
    {
        this(side, position, 2.0F, scale, offsetX, 0F, offsetZ);
    }

    public Wheel(Side side, Position position, float offsetX, float offsetY, float offsetZ, float scale)
    {
        this(side, position, 2.0F, scale, offsetX, offsetY, offsetZ);
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

    public float getScale()
    {
        return scale;
    }

    public Side getSide()
    {
        return side;
    }

    public Position getPosition()
    {
        return position;
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
