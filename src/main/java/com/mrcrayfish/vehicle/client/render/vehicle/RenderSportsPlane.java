package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.vehicle.SportsPlaneEntity;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class RenderSportsPlane extends AbstractRenderVehicle<SportsPlaneEntity>
{
    @Override
    public SpecialModel getBodyModel()
    {
        return SpecialModel.SPORTS_PLANE;
    }

    @Override
    public void render(SportsPlaneEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModel.SPORTS_PLANE.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.func_227860_a_();
        {
            matrixStack.func_227861_a_(0, -3 * 0.0625, 8 * 0.0625);
            matrixStack.func_227861_a_(8 * 0.0625, 0, 0);
            matrixStack.func_227861_a_(6 * 0.0625, 0, 0);
            matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-5F));
            this.renderDamagedPart(entity, SpecialModel.SPORTS_PLANE_WING.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.func_227865_b_();

        matrixStack.func_227860_a_();
        {
            matrixStack.func_227861_a_(0, -3 * 0.0625, 8 * 0.0625);
            matrixStack.func_227863_a_(Axis.POSITIVE_Z.func_229187_a_(180F));
            matrixStack.func_227861_a_(8 * 0.0625, 0.0625, 0);
            matrixStack.func_227861_a_(6 * 0.0625, 0, 0);
            matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(5F));
            this.renderDamagedPart(entity, SpecialModel.SPORTS_PLANE_WING.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.func_227865_b_();

        matrixStack.func_227860_a_();
        {
            matrixStack.func_227861_a_(0, -0.5, 0);
            matrixStack.func_227862_a_(0.85F, 0.85F, 0.85F);
            renderWheel(entity, matrixStack, renderTypeBuffer, 0F, -3 * 0.0625F, 24 * 0.0625F, 0F, partialTicks, light);
            renderWheel(entity, matrixStack, renderTypeBuffer, 7.5F * 0.0625F, -3 * 0.0625F, 2 * 0.0625F, 100F, partialTicks, light);
            renderWheel(entity, matrixStack, renderTypeBuffer, -7.5F * 0.0625F, -3 * 0.0625F, 2 * 0.0625F, -100F, partialTicks, light);
        }
        matrixStack.func_227865_b_();

        matrixStack.func_227860_a_();
        {
            float propellerRotation = entity.prevPropellerRotation + (entity.propellerRotation - entity.prevPropellerRotation) * partialTicks;
            matrixStack.func_227861_a_(0, -1.5 * 0.0625, 22.2 * 0.0625);
            matrixStack.func_227863_a_(Axis.POSITIVE_Z.func_229187_a_(propellerRotation));
            this.renderDamagedPart(entity, SpecialModel.SPORTS_PLANE_PROPELLER.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.func_227865_b_();
    }

    private void renderWheel(SportsPlaneEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float offsetX, float offsetY, float offsetZ, float legRotation, float partialTicks, int light)
    {
        matrixStack.func_227860_a_();
        {
            matrixStack.func_227861_a_(offsetX, offsetY, offsetZ);
            this.renderDamagedPart(vehicle, SpecialModel.SPORTS_PLANE_WHEEL_COVER.getModel(), matrixStack, renderTypeBuffer, light);

            matrixStack.func_227860_a_();
            {
                matrixStack.func_227861_a_(0, -2.25F / 16F, 0);
                matrixStack.func_227860_a_();
                {
                    if(vehicle.isMoving())
                    {
                        float wheelRotation = vehicle.prevWheelRotation + (vehicle.wheelRotation - vehicle.prevWheelRotation) * partialTicks;
                        matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-wheelRotation));
                    }
                    matrixStack.func_227862_a_(0.8F, 0.8F, 0.8F);
                    RenderUtil.renderColoredModel(RenderUtil.getModel(new ItemStack(ModItems.STANDARD_WHEEL)), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.field_229196_a_);
                }
                matrixStack.func_227865_b_();
            }
            matrixStack.func_227865_b_();

            matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_(legRotation));
            this.renderDamagedPart(vehicle, SpecialModel.SPORTS_PLANE_LEG.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.func_227865_b_();
    }

    @Override
    public void applyPlayerModel(SportsPlaneEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(10F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-10F);
    }

    @Override
    public void applyPlayerRender(SportsPlaneEntity entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        double offsetY = 24 * 0.0625 + entity.getMountedYOffset() + player.getYOffset() - 0.5; //TODO make this last variable a variable in entity plane
        matrixStack.func_227861_a_(0, offsetY, 0);
        float bodyPitch = entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks;
        float bodyRoll = entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks;
        matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-bodyPitch));
        matrixStack.func_227863_a_(Axis.POSITIVE_Z.func_229187_a_(bodyRoll));
        matrixStack.func_227861_a_(0, -offsetY, 0);
    }
}
