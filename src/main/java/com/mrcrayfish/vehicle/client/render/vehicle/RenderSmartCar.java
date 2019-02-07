package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntitySmartCar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

/**
 * Author: MrCrayfish
 */
public class RenderSmartCar extends AbstractRenderLandVehicle<EntitySmartCar>
{
    public RenderSmartCar()
    {
        this.setEnginePosition(0, 7.5, -12.5, 180, 1.2);
        this.setFuelPortPosition(EntitySmartCar.FUEL_PORT_POSITION);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 7F, 12F, 1.5F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 7F, 12F, 1.5F);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 7F, -12F, 1.5F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 7F, -12F, 1.5F);
    }

    @Override
    public void render(EntitySmartCar entity, float partialTicks)
    {
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);

        //Render the handles bars
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, 0.2, 0.3);
            GlStateManager.rotate(-67.5F, 1, 0, 0);
            GlStateManager.translate(0, -0.02, 0);
            GlStateManager.scale(0.9, 0.9, 0.9);

            float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 25F;
            GlStateManager.rotate(turnRotation, 0, 1, 0);

            Minecraft.getMinecraft().getRenderItem().renderItem(entity.steeringWheel, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();
    }
}
