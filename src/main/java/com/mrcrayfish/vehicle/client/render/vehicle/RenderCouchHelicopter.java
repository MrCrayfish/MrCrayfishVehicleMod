package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.SpecialModel;
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
    public SpecialModel getBodyModel()
    {
        return SpecialModel.ALUMINUM_BOAT_BODY;
    }

    @Override
    public void render(SofacopterEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks)
    {
        matrixStack.func_227860_a_();
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(90F));
        this.renderDamagedPart(entity, SpecialModel.ALUMINUM_BOAT_BODY.getModel(), matrixStack, renderTypeBuffer);
        matrixStack.func_227865_b_();

        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(0.0, 8 * 0.0625, 0.0);
        this.renderDamagedPart(entity, SpecialModel.ALUMINUM_BOAT_BODY.getModel(), matrixStack, renderTypeBuffer);
        matrixStack.func_227865_b_();

        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(0.0, 32 * 0.0625, 0.0);
        float bladeRotation = entity.prevBladeRotation + (entity.bladeRotation - entity.prevBladeRotation) * partialTicks;
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(bladeRotation));
        matrixStack.func_227862_a_(1.5F, 1.5F, 1.5F);
        this.renderDamagedPart(entity, SpecialModel.ALUMINUM_BOAT_BODY.getModel(), matrixStack, renderTypeBuffer);
        matrixStack.func_227865_b_();

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
        matrixStack.func_227861_a_(0.0, -playerOffset, 0.0);
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(-entityYaw));
        matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(-(entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks)));
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks));
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(entityYaw));
        matrixStack.func_227861_a_(0.0, playerOffset, 0.0);
    }
}