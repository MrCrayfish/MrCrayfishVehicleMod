package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.trailer.EntityFertilizerTrailer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class RenderFertilizerTrailer extends AbstractRenderTrailer<EntityFertilizerTrailer>
{
    @Override
    public void render(EntityFertilizerTrailer entity, float partialTicks)
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
                    {
                        GlStateManager.translate(-5.5 * 0.0625, -3 * 0.0625, -3 * 0.0625);
                        GlStateManager.scale(0.45, 0.45, 0.45);

                        int count = Math.max(1, stack.getCount() / 32);
                        int width = 3;
                        int maxLayerCount = 6;
                        for(int j = 0; j < count; j++)
                        {
                            GlStateManager.pushMatrix();
                            {
                                int layerIndex = index % maxLayerCount;
                                GlStateManager.translate(0, layer * 0.1 + j * 0.0625, 0);
                                GlStateManager.translate((layerIndex % width) * 0.5, 0, (float) (layerIndex / width) * 0.75);
                                GlStateManager.translate(0.5 * (layer % 2), 0, 0);
                                GlStateManager.rotate(90F, 1, 0, 0);
                                GlStateManager.rotate(47F * index, 0, 0, 1);
                                GlStateManager.rotate(2F * layerIndex, 1, 0, 0);
                                GlStateManager.translate(layer * 0.001, layer * 0.001, layer * 0.001); // Fixes Z fighting
                                Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
                            }
                            GlStateManager.popMatrix();
                            index++;
                            if(index % maxLayerCount == 0)
                            {
                                layer++;
                            }
                        }
                    }
                    GlStateManager.popMatrix();
                }
            }
        }

        /* Renders the spike */
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, -0.5, -0.4375);
            GlStateManager.rotate(90F, 0, 0, 1);
            float wheelRotation = entity.prevWheelRotation + (entity.wheelRotation - entity.prevWheelRotation) * partialTicks;
            GlStateManager.rotate(-wheelRotation, 1, 0, 0);
            GlStateManager.scale((float) 1.25, (float) 1.25, (float) 1.25);
            this.renderDamagedPart(entity, SpecialModels.SEED_SPIKER.getModel());
        }
        GlStateManager.popMatrix();
    }
}
