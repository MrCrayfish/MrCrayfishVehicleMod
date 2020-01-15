package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.CouchEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderCouch extends AbstractRenderVehicle<CouchEntity>
{
    @Override
    public SpecialModel getBodyModel()
    {
        return SpecialModel.RAINBOW_SOFA;
    }

    @Override
    public void render(CouchEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(0.0, 0.0625, 0.0);
        //matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(90F));
        this.renderDamagedPart(entity, this.getBodyModel().getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.func_227865_b_();
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
