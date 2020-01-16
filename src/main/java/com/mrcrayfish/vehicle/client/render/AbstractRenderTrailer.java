package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractRenderTrailer<T extends TrailerEntity> extends AbstractRenderVehicle<T>
{
    //TODO Eventually converted to the wheel system. Consider it a pulled vehicle rather than powered
    protected void renderWheel(TrailerEntity trailer, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, boolean right, float offsetX, float offsetY, float offsetZ, float wheelScale, float partialTicks, int light)
    {
        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(offsetX, offsetY, offsetZ);
        if(right)
        {
            matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180F));
        }
        float wheelRotation = trailer.prevWheelRotation + (trailer.wheelRotation - trailer.prevWheelRotation) * partialTicks;
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(right ? wheelRotation : -wheelRotation));
        matrixStack.func_227862_a_(wheelScale, wheelScale, wheelScale);
        RenderUtil.renderColoredModel(RenderUtil.getModel(new ItemStack(ModItems.STANDARD_WHEEL)), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.field_229196_a_);
        matrixStack.func_227865_b_();
    }
}
