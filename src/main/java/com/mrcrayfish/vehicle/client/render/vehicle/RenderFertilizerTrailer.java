package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.trailer.FertilizerTrailerEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class RenderFertilizerTrailer extends AbstractRenderTrailer<FertilizerTrailerEntity>
{
    @Override
    public void render(FertilizerTrailerEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModels.FERTILIZER_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, false, -11.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, true, 11.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks, light);

        StorageInventory inventory = entity.getInventory();
        if(inventory != null)
        {
            int layer = 0;
            int index = 0;
            for(int i = 0; i < inventory.getSizeInventory(); i++)
            {
                ItemStack stack = inventory.getStackInSlot(i);
                if(!stack.isEmpty())
                {
                    matrixStack.push();
                    matrixStack.translate(-5.5 * 0.0625, -3 * 0.0625, -3 * 0.0625);
                    matrixStack.scale(0.45F, 0.45F, 0.45F);

                    int count = Math.max(1, stack.getCount() / 32);
                    int width = 3;
                    int maxLayerCount = 6;
                    for(int j = 0; j < count; j++)
                    {
                        matrixStack.push();
                        {
                            int layerIndex = index % maxLayerCount;
                            matrixStack.translate(0, layer * 0.1 + j * 0.0625, 0);
                            matrixStack.translate((layerIndex % width) * 0.5, 0, (float) (layerIndex / width) * 0.75);
                            matrixStack.translate(0.5 * (layer % 2), 0, 0);
                            matrixStack.rotate(Vector3f.XP.rotationDegrees(90F));
                            matrixStack.rotate(Vector3f.ZP.rotationDegrees(47F * index));
                            matrixStack.rotate(Vector3f.XP.rotationDegrees(2F * layerIndex));
                            matrixStack.translate(layer * 0.001, layer * 0.001, layer * 0.001); // Fixes Z fighting
                            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, light, OverlayTexture.NO_OVERLAY, RenderUtil.getModel(stack));
                        }
                        matrixStack.pop();
                        index++;
                        if(index % maxLayerCount == 0)
                        {
                            layer++;
                        }
                    }
                    matrixStack.pop();
                }
            }
        }

        /* Renders the spike */
        matrixStack.push();
        {
            matrixStack.translate(0, -0.5, -0.4375);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(90F));
            float wheelRotation = entity.prevWheelRotation + (entity.wheelRotation - entity.prevWheelRotation) * partialTicks;
            matrixStack.rotate(Vector3f.XP.rotationDegrees(-wheelRotation));
            matrixStack.scale((float) 1.25, (float) 1.25, (float) 1.25);
            RenderUtil.renderColoredModel(SpecialModels.SEED_SPIKER.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
        }
        matrixStack.pop();
    }
}
