package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntityCouch;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class RenderCouch extends AbstractRenderLandVehicle<EntityCouch>
{
    public RenderCouch()
    {
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 8.0F, 7.0F, 1.75F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 8.0F, 7.0F, 1.75F);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 8.0F, -7.0F, 1.75F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 8.0F, -7.0F, 1.75F);
    }

    @Override
    public void render(EntityCouch entity, float partialTicks)
    {
        entity.setWheelOffset(4.0F);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90F, 0, 1, 0);
        this.renderDamagedPart(entity, entity.body);
        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(EntityCouch entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-55F);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(25F);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-55F);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-25F);
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-90F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(15F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-90F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-15F);
    }
}
