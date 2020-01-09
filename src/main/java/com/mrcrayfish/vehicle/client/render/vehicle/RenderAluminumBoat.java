package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.SpecialModel;
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
        this.noWater = (new ModelRenderer(new Model(resource -> RenderType.func_228643_e_()){
            @Override
            public void func_225598_a_(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int i, int i1, float v, float v1, float v2, float v3) {}
        }, 0, 0)).setTextureSize(128, 64);
        this.noWater.func_228301_a_(-15F, -6F, -21F, 30, 8, 35, 0.0F);
    }

    @Override
    public SpecialModel getBodyModel()
    {
        return SpecialModel.ALUMINUM_BOAT_BODY;
    }

    @Override
    public void render(AluminumBoatEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModel.ALUMINUM_BOAT_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        IVertexBuilder buffer = renderTypeBuffer.getBuffer(RenderType.func_228651_i_());
        this.noWater.func_228308_a_(matrixStack, buffer, light, OverlayTexture.field_229196_a_);
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

        matrixStack.func_227861_a_(offsetX, offsetY, offsetZ);
        float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
        float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / entity.getMaxTurnAngle();
        matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(turnAngleNormal * currentSpeedNormal * 15F));
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-8F * Math.min(1.0F, currentSpeedNormal)));
        matrixStack.func_227861_a_(-offsetX, -offsetY, -offsetZ);
    }

    //TODO fix this
    /*@Override
    public void renderMultipass(AluminumBoatEntity entity, double x, double y, double z, float currentYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(-currentYaw, 0, 1, 0);
            GlStateManager.scale(1.1, 1.1, 1.1);
            GlStateManager.translate(0, 0.5, 0.2);

            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * -15F, 0, 0, 1);
            GlStateManager.rotate(-8F * Math.min(1.0F, currentSpeedNormal), 1, 0, 0);

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.colorMask(false, false, false, false);
            this.noWater.render(0.0625F);
            GlStateManager.colorMask(true, true, true, true);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isMultipass()
    {
        return true;
    }*/
}
