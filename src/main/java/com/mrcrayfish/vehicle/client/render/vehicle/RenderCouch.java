package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.CouchEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderCouch extends AbstractRenderVehicle<CouchEntity>
{
    @Override
    public void render(CouchEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.push();
        matrixStack.translate(0.0, 0.0625, 0.0);
        //matrixStack.rotate(Vector3f.YP.rotationDegrees(90F));
        this.renderDamagedPart(entity, SpecialModels.RAINBOW_SOFA.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pop();
    }

    @Override
    public void applyPlayerModel(CouchEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
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
