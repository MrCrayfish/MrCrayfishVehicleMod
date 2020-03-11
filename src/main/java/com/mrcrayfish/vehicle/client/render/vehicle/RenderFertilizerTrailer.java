package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.trailer.FertilizerTrailerEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class RenderFertilizerTrailer extends AbstractRenderTrailer<FertilizerTrailerEntity>
{
    @Override
    public void render(FertilizerTrailerEntity entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.FERTILIZER_TRAILER.getModel());
        this.renderWheel(entity, false, -11.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks);
        this.renderWheel(entity, true, 11.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks);

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
                    GlStateManager.pushMatrix();
                    GlStateManager.translated(-5.5 * 0.0625, -3 * 0.0625, -3 * 0.0625);
                    GlStateManager.scalef(0.45F, 0.45F, 0.45F);

                    int count = Math.max(1, stack.getCount() / 32);
                    int width = 3;
                    int maxLayerCount = 6;
                    for(int j = 0; j < count; j++)
                    {
                        GlStateManager.pushMatrix();
                        {
                            int layerIndex = index % maxLayerCount;
                            GlStateManager.translated(0, layer * 0.1 + j * 0.0625, 0);
                            GlStateManager.translated((layerIndex % width) * 0.5, 0, (float) (layerIndex / width) * 0.75);
                            GlStateManager.translated(0.5 * (layer % 2), 0, 0);
                            GlStateManager.rotatef(90F, 1, 0, 0);
                            GlStateManager.rotatef(47F * index, 0, 0, 1);
                            GlStateManager.rotatef(2F * layerIndex, 1, 0, 0);
                            GlStateManager.translated(layer * 0.001, layer * 0.001, layer * 0.001); // Fixes Z fighting
                            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
                        }
                        GlStateManager.popMatrix();
                        index++;
                        if(index % maxLayerCount == 0)
                        {
                            layer++;
                        }
                    }
                    GlStateManager.popMatrix();
                }
            }
        }

        /* Renders the spike */
        GlStateManager.pushMatrix();
        {
            GlStateManager.translated(0, -0.5, -0.4375);
            GlStateManager.rotatef(90F, 0, 0, 1);
            float wheelRotation = entity.prevWheelRotation + (entity.wheelRotation - entity.prevWheelRotation) * partialTicks;
            GlStateManager.rotatef(-wheelRotation, 1, 0, 0);
            GlStateManager.scalef((float) 1.25, (float) 1.25, (float) 1.25);
            RenderUtil.renderColoredModel(SpecialModels.SEED_SPIKER.getModel(), ItemCameraTransforms.TransformType.NONE, false, -1);
        }
        GlStateManager.popMatrix();
    }
}
