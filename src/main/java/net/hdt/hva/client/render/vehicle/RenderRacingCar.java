package net.hdt.hva.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.RenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import net.hdt.hva.entity.vehicle.EntityRaceCar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

/**
 * Author: MrCrayfish
 */
public class RenderRacingCar extends RenderLandVehicle<EntityRaceCar>
{
    public RenderRacingCar(RenderManager renderManager)
    {
        super(renderManager);
        wheels.add(new Wheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 7.0F,  0.15f,8.5F, 0.75F));
        wheels.add(new Wheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 7.0F,  0.15f,8.5F, 0.75F));
        wheels.add(new Wheel(Wheel.Side.LEFT, Wheel.Position.REAR, 7.0F,  0.15f,-8.5F, 0.75F));
        wheels.add(new Wheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 7.0F,  0.15f,-8.5F, 0.75F));
    }

    @Override
    public void doRender(EntityRaceCar entity, double x, double y, double z, float currentYaw, float partialTicks)
    {
        RenderHelper.enableStandardItemLighting();

        float additionalYaw = entity.prevAdditionalYaw + (entity.additionalYaw - entity.prevAdditionalYaw) * partialTicks;

        EntityLivingBase entityLivingBase = (EntityLivingBase) entity.getControllingPassenger();
        if(entityLivingBase != null)
        {
            entityLivingBase.renderYawOffset = currentYaw - additionalYaw;
            entityLivingBase.prevRenderYawOffset = currentYaw - additionalYaw;
        }

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(-currentYaw, 0, 1, 0);
            GlStateManager.rotate(additionalYaw, 0, 1, 0);
            GlStateManager.translate(0, 0, 0.4F);
            GlStateManager.scale(1.2, 1.2, 1.2);

            this.setupBreakAnimation(entity, partialTicks);

            double bodyOffset = 0.6;

            //Render the body
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, bodyOffset, 0);
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();

            //Render the steering wheel
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, bodyOffset + 0.2, -0.5);
//                GlStateManager.rotate(-45F, 1, 0, 0);
                GlStateManager.rotate(26F, 1, 0, 0);
                GlStateManager.translate(0, -0.02, 0);
                GlStateManager.scale(0.9, 0.9, 0.9);

                float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
                float wheelAngleNormal = wheelAngle / 45F;
                float turnRotation = wheelAngleNormal * 25F;
                GlStateManager.rotate(turnRotation, 0, 1, 0);

                Minecraft.getMinecraft().getRenderItem().renderItem(entity.steeringWheel, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();

            GlStateManager.translate(0, 2.5F * 0.0625F * 0.75F, 0);
            super.doRender(entity, x, y, z, currentYaw, partialTicks);
        }
        GlStateManager.popMatrix();
    }
}
