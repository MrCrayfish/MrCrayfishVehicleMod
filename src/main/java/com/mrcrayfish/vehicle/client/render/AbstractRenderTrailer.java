package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractRenderTrailer<T extends TrailerEntity> extends AbstractRenderVehicle<T>
{
    //TODO Eventually converted to the wheel system. Consider it a pulled vehicle rather than powered
    protected void renderWheel(TrailerEntity trailer, boolean right, float offsetX, float offsetY, float offsetZ, float wheelScale, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translated(offsetX, offsetY, offsetZ);
        if(right)
        {
            GlStateManager.rotatef(180F, 0, 1, 0);
        }
        float wheelRotation = trailer.prevWheelRotation + (trailer.wheelRotation - trailer.prevWheelRotation) * partialTicks;
        GlStateManager.rotatef(right ? wheelRotation : -wheelRotation, 1, 0, 0);
        GlStateManager.scalef(wheelScale, wheelScale, wheelScale);
        RenderUtil.renderColoredModel(RenderUtil.getModel(new ItemStack(ModItems.STANDARD_WHEEL)), ItemCameraTransforms.TransformType.NONE, false, -1);
        GlStateManager.popMatrix();
    }
}
