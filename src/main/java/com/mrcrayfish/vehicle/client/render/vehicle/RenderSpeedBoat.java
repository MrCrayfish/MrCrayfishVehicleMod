package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.EntityJetSki;
import com.mrcrayfish.vehicle.entity.vehicle.EntitySpeedBoat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class RenderSpeedBoat extends AbstractRenderVehicle<EntitySpeedBoat>
{
    public RenderSpeedBoat()
    {
        this.setFuelPortPosition(EntitySpeedBoat.FUEL_PORT_POSITION);
    }

    @Override
    public void render(EntitySpeedBoat entity, float partialTicks)
    {
        this.setFuelPortPosition(new PartPosition(-12.25, 6.0, -19.5, 0.0, -90.0, 0.0, 0.25));

        //Render the body
        renderDamagedPart(entity, entity.body);

        //Render the handles bars
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, 0.215, -0.125);
            GlStateManager.rotate(-45F, 1, 0, 0);
            GlStateManager.translate(0, 0.02, 0);

            float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 15F;
            GlStateManager.rotate(turnRotation, 0, 1, 0);

            Minecraft.getMinecraft().getRenderItem().renderItem(entity.handleBar, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(EntitySpeedBoat entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(20F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-20F);

        float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-65F - turnRotation);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(-7F);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-65F + turnRotation);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(7F);
    }

    @Override
    public void applyPlayerRender(EntitySpeedBoat entity, EntityPlayer player, float partialTicks)
    {
        double offset = 24 * 0.0625 + entity.getMountedYOffset() + player.getYOffset();
        GlStateManager.translate(0, offset, 0);
        float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
        float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / entity.getMaxTurnAngle();
        GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * 15F, 0, 0, 1);
        GlStateManager.rotate(-8F * Math.min(1.0F, currentSpeedNormal), 1, 0, 0);
        GlStateManager.translate(0, -offset, 0);
    }

    @Override
    protected boolean shouldRenderFuelLid()
    {
        return false;
    }
}
