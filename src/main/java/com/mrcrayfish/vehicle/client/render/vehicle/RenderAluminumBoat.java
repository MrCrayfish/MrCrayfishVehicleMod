package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.ISpecialModel;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.AluminumBoatEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderAluminumBoat extends AbstractRenderVehicle<AluminumBoatEntity>
{
    private final ModelRenderer noWater;

    public RenderAluminumBoat()
    {
        this.noWater = (new ModelRenderer(new Model(resource -> RenderType.waterMask()){
            @Override
            public void render(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {}
        }, 0, 0)).setTextureSize(128, 64);
        this.noWater.addBox(-15F, -6F, -21F, 30, 8, 35, 0.0F);
    }

    @Override
    public ISpecialModel getBodyModel()
    {
        return SpecialModels.ALUMINUM_BOAT_BODY;
    }

    @Override
    public void render(AluminumBoatEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModels.ALUMINUM_BOAT_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        IVertexBuilder buffer = renderTypeBuffer.getBuffer(RenderType.waterMask());
        this.noWater.render(matrixStack, buffer, light, OverlayTexture.DEFAULT_LIGHT);
    }

    @Override
    public void applyPlayerModel(AluminumBoatEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(20F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-20F);
    }

    @Override
    public void applyPlayerRender(AluminumBoatEntity entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        double offsetX = -0.5;
        double offsetY = 24 * 0.0625 + entity.getMountedYOffset() + player.getYOffset();
        double offsetZ = -0.9;

        int index = entity.getPassengers().indexOf(player);
        if(index > 0)
        {
            offsetX += (index % 2) * 1F;
            offsetZ += (index / 2F) * 1.2F;
        }

        matrixStack.translate(offsetX, offsetY, offsetZ);
        float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
        float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / entity.getMaxTurnAngle();
        matrixStack.rotate(Vector3f.field_229183_f_.func_229187_a_(turnAngleNormal * currentSpeedNormal * 15F));
        matrixStack.rotate(Vector3f.field_229179_b_.func_229187_a_(-8F * Math.min(1.0F, currentSpeedNormal)));
        matrixStack.translate(-offsetX, -offsetY, -offsetZ);
    }
}
