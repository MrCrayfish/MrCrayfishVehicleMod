package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.ISpecialModel;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.SofacopterEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderCouchHelicopter extends AbstractRenderVehicle<SofacopterEntity>
{
    @Override
    public ISpecialModel getBodyModel()
    {
        return SpecialModels.RED_SOFA;
    }

    @Override
    public void render(SofacopterEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.push();
        this.renderDamagedPart(entity, SpecialModels.RED_SOFA.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(0.0, 8 * 0.0625, 0.0);
        this.renderDamagedPart(entity, SpecialModels.SOFA_HELICOPTER_ARM.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(0.0, 32 * 0.0625, 0.0);
        float bladeRotation = entity.prevBladeRotation + (entity.bladeRotation - entity.prevBladeRotation) * partialTicks;
        matrixStack.rotate(Vector3f.field_229181_d_.func_229187_a_(bladeRotation));
        matrixStack.scale(1.5F, 1.5F, 1.5F);
        this.renderDamagedPart(entity, SpecialModels.ALUMINUM_BOAT_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pop();

       /* GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.skid, ItemCameraTransforms.TransformType.NONE);
        GlStateManager.popMatrix();*/
    }

    @Override
    public void applyPlayerModel(SofacopterEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
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

    @Override
    public void applyPlayerRender(SofacopterEntity entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        float entityYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float playerOffset = (float) ((entity.getMountedYOffset() + player.getYOffset()) * 16.0F - 14.0F * 0.0625F);
        matrixStack.translate(0.0, -playerOffset, 0.0);
        matrixStack.rotate(Vector3f.field_229181_d_.func_229187_a_(-entityYaw));
        matrixStack.rotate(Vector3f.field_229183_f_.func_229187_a_(-(entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks)));
        matrixStack.rotate(Vector3f.field_229179_b_.func_229187_a_(entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks));
        matrixStack.rotate(Vector3f.field_229181_d_.func_229187_a_(entityYaw));
        matrixStack.translate(0.0, playerOffset, 0.0);
    }
}