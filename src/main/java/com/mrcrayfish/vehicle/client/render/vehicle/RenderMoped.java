package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.ISpecialModel;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
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
import net.minecraft.util.math.Vec3d;

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
        this.base.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
        this.lid = new ModelRenderer(64, 64, 0, 0);
        this.lid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
        this.lid.rotationPointY = 9.0F;
        this.lid.rotationPointZ = 1.0F;
        this.lock = new ModelRenderer(64, 64, 0, 0);
        this.lock.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
        this.lock.rotationPointY = 8.0F;
    }

    @Override
    public void render(MopedEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModels.MOPED_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.push();

        matrixStack.translate(0.0, -0.0625, 11.5 * 0.0625);
        matrixStack.rotate(Axis.POSITIVE_X.func_229187_a_(-22.5F));

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 25F;

        matrixStack.rotate(Axis.POSITIVE_Y.func_229187_a_(turnRotation / 2));
        matrixStack.rotate(Axis.POSITIVE_X.func_229187_a_(22.5F));
        matrixStack.translate(0.0, 0.0, -11.5 * 0.0625);

        //Render handles bars
        matrixStack.push();
        matrixStack.translate(0, 0.835, 0.525);
        matrixStack.scale(0.8F, 0.8F, 0.8F);
        this.renderDamagedPart(entity, SpecialModels.MOPED_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pop();

        //Render front bar and mud guard
        matrixStack.push();
        {
            matrixStack.translate(0, -0.12, 0.785);
            matrixStack.rotate(Axis.POSITIVE_X.func_229187_a_(-22.5F));
            matrixStack.scale(0.9F, 0.9F, 0.9F);
            this.renderDamagedPart(entity, SpecialModels.MOPED_MUD_GUARD.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.pop();

        //Render front wheel
        if(entity.hasWheels())
        {
            matrixStack.push();
            matrixStack.translate(0, -0.4, 14.5 * 0.0625);
            float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
            if(entity.isMoving())
            {
                matrixStack.rotate(Axis.POSITIVE_X.func_229187_a_(-frontWheelSpin));
            }
            matrixStack.scale(1.3F, 1.3F, 1.3F);
            RenderUtil.renderColoredModel(RenderUtil.getModel(new ItemStack(ModItems.STANDARD_WHEEL)), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.DEFAULT_LIGHT);
            matrixStack.pop();
        }

        matrixStack.pop();

        if(entity.hasChest())
        {
            matrixStack.push();
            matrixStack.translate(0, 0.25, -0.65);
            matrixStack.rotate(Axis.POSITIVE_Y.func_229187_a_(180F));
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            ItemStack chest = new ItemStack(Blocks.CHEST);
            RenderUtil.renderModel(chest, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, light, OverlayTexture.DEFAULT_LIGHT, RenderUtil.getModel(chest));
            matrixStack.pop();
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
        int index = entity.getSeatTracker().getSeatIndex(player.getUniqueID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).mul(-1, 1, 1).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y + player.getYOffset()) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = seatVec.z * scale;
            matrixStack.translate(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            matrixStack.rotate(Axis.POSITIVE_Z.func_229187_a_(turnAngleNormal * currentSpeedNormal * 20F));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
        }
    }

    private void renderChest(MatrixStack matrixStack, IVertexBuilder vertexBuilder, ModelRenderer lid, ModelRenderer lock, ModelRenderer base, float p_228871_6_, int lightTexture, int overlayTexture)
    {
        lid.rotateAngleX = -(p_228871_6_ * ((float) Math.PI / 2F));
        lock.rotateAngleX = lid.rotateAngleX;
        lid.render(matrixStack, vertexBuilder, lightTexture, overlayTexture);
        lock.render(matrixStack, vertexBuilder, lightTexture, overlayTexture);
        base.render(matrixStack, vertexBuilder, lightTexture, overlayTexture);
    }
}
