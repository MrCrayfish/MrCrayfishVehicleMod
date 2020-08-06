package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.ISpecialModel;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractRenderVehicle<T extends VehicleEntity>
{
    public ISpecialModel getKeyHoleModel()
    {
        return SpecialModels.KEY_HOLE;
    }

    public ISpecialModel getTowBarModel()
    {
        return SpecialModels.TOW_BAR;
    }

    public abstract void render(T entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light);

    public void applyPlayerModel(T entity, PlayerEntity player, PlayerModel<AbstractClientPlayerEntity> model, float partialTicks) {}

    public void applyPlayerRender(T entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder) {}

    protected boolean shouldRenderFuelLid()
    {
        return true;
    }

    protected void renderDamagedPart(VehicleEntity vehicle, ItemStack part, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        this.renderDamagedPart(vehicle, RenderUtil.getModel(part), matrixStack, renderTypeBuffer, light);
    }

    protected void renderDamagedPart(VehicleEntity vehicle, IBakedModel model, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        this.renderDamagedPart(vehicle, model, matrixStack, renderTypeBuffer, false, light);
        this.renderDamagedPart(vehicle, model, matrixStack, renderTypeBuffer, true, light);
    }

    private void renderDamagedPart(VehicleEntity vehicle, IBakedModel model, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, boolean renderDamage, int light)
    {
        if(renderDamage)
        {
            int stage = vehicle.getDestroyedStage();
            if(stage <= 0)
                return;
            RenderUtil.renderDamagedVehicleModel(model, ItemCameraTransforms.TransformType.NONE, false, matrixStack, stage, vehicle.getColor(), light, OverlayTexture.NO_OVERLAY);
        }
        else
        {
            RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, vehicle.getColor(), light, OverlayTexture.NO_OVERLAY);
        }
    }
}