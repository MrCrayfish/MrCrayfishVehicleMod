package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.BathEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderBath extends AbstractRenderVehicle<BathEntity>
{
    @Override
    public SpecialModel getBodyModel()
    {
        return SpecialModel.ATV_BODY;
    }

    @Override
    public void render(BathEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.rotate(Vector3f.field_229181_d_.func_229187_a_(90F));
        this.renderDamagedPart(entity, SpecialModel.ATV_BODY.getModel(), matrixStack, renderTypeBuffer, light);
    }

    @Override
    public void applyPlayerRender(BathEntity entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        double offsetY = 24 * 0.0625 + entity.getMountedYOffset() + player.getYOffset() - 0.5;//TODO make this last variable a variable in entity plane
        matrixStack.translate(0.0, offsetY, 0.0);
        float bodyPitch = entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks;
        float bodyRoll = entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks;
        matrixStack.rotate(Vector3f.field_229179_b_.func_229187_a_(-bodyPitch));
        matrixStack.rotate(Vector3f.field_229183_f_.func_229187_a_(bodyRoll));
        matrixStack.translate(0.0, -offsetY, 0.0);
    }

    @Override
    public void applyPlayerModel(BathEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.bipedHead.showModel = false;
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(10F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-10F);
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-80F);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(5F);
        model.bipedRightArm.rotateAngleZ = (float) Math.toRadians(0F);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-80F);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-5F);
        model.bipedLeftArm.rotateAngleZ = (float) Math.toRadians(0F);
    }
}
