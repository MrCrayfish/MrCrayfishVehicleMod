package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.render.AbstractRenderPoweredVehicle;
import com.mrcrayfish.vehicle.client.render.RenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.vehicle.EntityGolfCart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class RenderGolfCart extends AbstractRenderPoweredVehicle<EntityGolfCart>
{
    public RenderGolfCart()
    {
        this.setFuelPortPosition(EntityGolfCart.FUEL_PORT_POSITION);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 9.0F, 16.0F, 1.75F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 9.0F, 16.0F, 1.75F);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 9.0F, -12.5F, 1.75F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 9.0F, -12.5F, 1.75F);
    }

    @Override
    public void render(EntityGolfCart entity, float partialTicks)
    {
        //Render the body
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);

        //Render the handles bars
        GlStateManager.pushMatrix();
        {
            // Positions the steering wheel in the correct position
            GlStateManager.translate(-0.345, 0.425, 0.1);
            GlStateManager.rotate(-45F, 1, 0, 0);
            GlStateManager.translate(0, -0.02, 0);
            GlStateManager.scale(0.95, 0.95, 0.95);

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
