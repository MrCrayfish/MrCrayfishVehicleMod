package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

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

    public void render(EntityLandVehicle vehicle, float partialTicks)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate((offsetX * 0.0625) * side.offset, offsetY * 0.0625, offsetZ * 0.0625);
            GlStateManager.pushMatrix();
            {
                if(position == Position.FRONT)
                {
                    float wheelAngle = vehicle.prevRenderWheelAngle + (vehicle.renderWheelAngle - vehicle.prevRenderWheelAngle) * partialTicks;
                    GlStateManager.rotate(wheelAngle, 0, 1, 0);
                }
                if(vehicle.isMoving())
                {
                    GlStateManager.rotate(-getWheelRotation(vehicle, partialTicks), 1, 0, 0);
                }
                GlStateManager.translate((((width * scale) / 2) * 0.0625) * side.offset, 0, 0);
                GlStateManager.scale(scale, scale, scale);
                if(side == Side.RIGHT)
                {
                    GlStateManager.rotate(180F, 0, 1, 0);
                }
                Minecraft.getMinecraft().getRenderItem().renderItem(vehicle.wheel, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    private float getWheelRotation(EntityLandVehicle vehicle, float partialTicks)
    {
        if(position == Position.REAR)
        {
            return vehicle.prevRearWheelRotation + (vehicle.rearWheelRotation - vehicle.prevRearWheelRotation) * partialTicks;
        }
        return vehicle.prevFrontWheelRotation + (vehicle.frontWheelRotation - vehicle.prevFrontWheelRotation) * partialTicks;
    }

    public enum Side
    {
        LEFT(-1), RIGHT(1), NONE(0);

        int offset;

        Side(int offset)
        {
            this.offset = offset;
        }
    }

    public enum Position
    {
        FRONT, REAR, NONE;
    }
}
