package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractRenderTrailer<T extends TrailerEntity> extends AbstractRenderVehicle<T>
{
    //TODO Eventually converted to the wheel system. Consider it a pulled vehicle rather than powered
    protected void renderWheel(TrailerEntity trailer, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, boolean right, float offsetX, float offsetY, float offsetZ, float wheelScale, float partialTicks, int light)
    {
        matrixStack.pushPose();
        matrixStack.translate(offsetX, offsetY, offsetZ);
        if(right)
        {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));
        }
        float wheelRotation = trailer.prevWheelRotation + (trailer.wheelRotation - trailer.prevWheelRotation) * partialTicks;
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(right ? wheelRotation : -wheelRotation));
        matrixStack.scale(wheelScale, wheelScale, wheelScale);
        RenderUtil.renderColoredModel(RenderUtil.getModel(new ItemStack(ModItems.STANDARD_WHEEL.get())), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }
}
