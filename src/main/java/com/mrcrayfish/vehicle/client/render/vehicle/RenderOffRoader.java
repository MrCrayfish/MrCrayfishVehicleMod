package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.render.AbstractRenderPoweredVehicle;
import com.mrcrayfish.vehicle.client.render.RenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.vehicle.EntityATV;
import com.mrcrayfish.vehicle.entity.vehicle.EntityOffRoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

/**
 * Author: MrCrayfish
 */
public class RenderOffRoader extends AbstractRenderPoweredVehicle<EntityOffRoader>
{
    public RenderOffRoader()
    {
        this.setFuelPortPosition(EntityOffRoader.FUEL_PORT_POSITION);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 10.0F, 14.5F, 2.25F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 10.0F, 14.5F, 2.25F);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 10.0F, -14.5F, 2.25F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 10.0F, -14.5F, 2.25F);
    }

    @Override
    public void render(EntityOffRoader entity, float partialTicks)
    {
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);

        //Render the handles bars
        GlStateManager.pushMatrix();
        {
            // Positions the steering wheel in the correct position
            GlStateManager.translate(-0.3125, 0.35, 0.2);
            GlStateManager.rotate(-45F, 1, 0, 0);
            GlStateManager.translate(0, -0.02, 0);
            GlStateManager.scale(0.75, 0.75, 0.75);

            // Rotates the steering wheel based on the wheel angle
            float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 25F;
            GlStateManager.rotate(turnRotation, 0, 1, 0);

            Minecraft.getMinecraft().getRenderItem().renderItem(entity.steeringWheel, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();
    }
}
