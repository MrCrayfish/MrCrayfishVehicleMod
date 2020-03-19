package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.EntityTrailer;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractRenderTrailer<T extends EntityTrailer> extends AbstractRenderVehicle<T>
{
    //TODO Eventually converted to the wheel system. Consider it a pulled vehicle rather than powered
    protected void renderWheel(EntityTrailer trailer, boolean right, float offsetX, float offsetY, float offsetZ, float wheelScale, float partialTicks)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(offsetX, offsetY, offsetZ);
            if(right)
            {
                GlStateManager.rotate(180F, 0, 1, 0);
            }
            GlStateManager.pushMatrix();
            {
                GlStateManager.pushMatrix();
                {
                    float wheelRotation = trailer.prevWheelRotation + (trailer.wheelRotation - trailer.prevWheelRotation) * partialTicks;
                    GlStateManager.rotate(right ? wheelRotation : -wheelRotation, 1, 0, 0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    ItemStack wheel = new ItemStack(ModItems.WHEEL);
                    IBakedModel model = RenderUtil.getModel(wheel);
                    RenderUtil.renderItemModel(wheel, model, ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
