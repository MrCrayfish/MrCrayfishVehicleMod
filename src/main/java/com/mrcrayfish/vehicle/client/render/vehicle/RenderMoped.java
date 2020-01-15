package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.vehicle.MopedEntity;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Calendar;

/**
 * Author: MrCrayfish
 */
public class RenderMoped extends AbstractRenderVehicle<MopedEntity>
{
    private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");

    private final ModelRenderer lid;
    private final ModelRenderer base;
    private final ModelRenderer lock;
    public final boolean isChristmas;

    public RenderMoped()
    {
        Calendar calendar = Calendar.getInstance();
        this.isChristmas = calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26;
        this.base = new ModelRenderer(64, 64, 0, 19);
        this.base.func_228301_a_(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
        this.lid = new ModelRenderer(64, 64, 0, 0);
        this.lid.func_228301_a_(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
        this.lid.rotationPointY = 9.0F;
        this.lid.rotationPointZ = 1.0F;
        this.lock = new ModelRenderer(64, 64, 0, 0);
        this.lock.func_228301_a_(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
        this.lock.rotationPointY = 8.0F;
    }

    @Override
    public SpecialModel getBodyModel()
    {
        return SpecialModel.MOPED_BODY;
    }

    @Override
    public void render(MopedEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModel.MOPED_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.func_227860_a_();

        matrixStack.func_227861_a_(0.0, -0.0625, 11.5 * 0.0625);
        matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-22.5F));

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 25F;

        matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_(turnRotation / 2));
        matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(22.5F));
        matrixStack.func_227861_a_(0.0, 0.0, -11.5 * 0.0625);

        //Render handles bars
        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(0, 0.835, 0.525);
        matrixStack.func_227862_a_(0.8F, 0.8F, 0.8F);
        this.renderDamagedPart(entity, SpecialModel.MOPED_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.func_227865_b_();

        //Render front bar and mud guard
        matrixStack.func_227860_a_();
        {
            matrixStack.func_227861_a_(0, -0.12, 0.785);
            matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-22.5F));
            matrixStack.func_227862_a_(0.9F, 0.9F, 0.9F);
            this.renderDamagedPart(entity, SpecialModel.MOPED_MUD_GUARD.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.func_227865_b_();

        //Render front wheel
        if(entity.hasWheels())
        {
            matrixStack.func_227860_a_();
            matrixStack.func_227861_a_(0, -0.4, 14.5 * 0.0625);
            float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
            if(entity.isMoving())
            {
                matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-frontWheelSpin));
            }
            matrixStack.func_227862_a_(1.3F, 1.3F, 1.3F);
            RenderUtil.renderColoredModel(RenderUtil.getModel(new ItemStack(ModItems.STANDARD_WHEEL)), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.field_229196_a_);
            matrixStack.func_227865_b_();
        }

        matrixStack.func_227865_b_();

        if(entity.hasChest())
        {
            matrixStack.func_227860_a_();
            matrixStack.func_227861_a_(0, 0.25, -0.65);
            matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_(180F));
            matrixStack.func_227862_a_(0.5F, 0.5F, 0.5F);
            ItemStack chest = new ItemStack(Blocks.CHEST);
            RenderUtil.renderModel(chest, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, light, OverlayTexture.field_229196_a_, RenderUtil.getModel(chest));
            matrixStack.func_227865_b_();
        }
    }

    @Override
    public void applyPlayerModel(MopedEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-75F - turnRotation);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(7F);
        //model.bipedRightArm.offsetZ -= 0.05 * wheelAngleNormal; //TODO figure out offsets
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-75F + turnRotation);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-7F);
        //model.bipedLeftArm.offsetZ -= 0.05 * -wheelAngleNormal;
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-55F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-55F);
    }

    @Override
    public void applyPlayerRender(MopedEntity entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        double offset = 24 * 0.0625 + entity.getMountedYOffset() + player.getYOffset();
        matrixStack.func_227861_a_(0, offset, 0);
        float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
        float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
        matrixStack.func_227863_a_(Axis.POSITIVE_Z.func_229187_a_(turnAngleNormal * currentSpeedNormal * 20F));
        matrixStack.func_227861_a_(0, -offset, 0);
    }

    private void renderChest(MatrixStack matrixStack, IVertexBuilder vertexBuilder, ModelRenderer lid, ModelRenderer lock, ModelRenderer base, float p_228871_6_, int lightTexture, int overlayTexture)
    {
        lid.rotateAngleX = -(p_228871_6_ * ((float) Math.PI / 2F));
        lock.rotateAngleX = lid.rotateAngleX;
        lid.func_228308_a_(matrixStack, vertexBuilder, lightTexture, overlayTexture);
        lock.func_228308_a_(matrixStack, vertexBuilder, lightTexture, overlayTexture);
        base.func_228308_a_(matrixStack, vertexBuilder, lightTexture, overlayTexture);
    }
}
