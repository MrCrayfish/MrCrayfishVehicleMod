package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.SeederTrailerEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class RenderSeederTrailer extends AbstractRenderTrailer<SeederTrailerEntity>
{
    @Override
    public void render(SeederTrailerEntity entity, float partialTicks)
    {
        //Render the body
        this.renderDamagedPart(entity, SpecialModels.SEEDER_TRAILER.getModel());
        this.renderWheel(entity, true, -17.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks);
        this.renderWheel(entity, false, 17.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks);

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
                        GlStateManager.translated(-10.5 * 0.0625, -3 * 0.0625, -2 * 0.0625);
                        GlStateManager.scalef(0.45F, 0.45F, 0.45F);

                        int count = Math.max(1, stack.getCount() / 16);
                        int width = 4;
                        int maxLayerCount = 8;
                        for(int j = 0; j < count; j++)
                        {
                            GlStateManager.pushMatrix();
                            {
                                int layerIndex = index % maxLayerCount;
                                //double yOffset = Math.sin(Math.PI * (((layerIndex + 0.5) % (double) width) / (double) width)) * 0.1;
                                //GlStateManager.translate(0, yOffset * ((double) layer / inventory.getSizeInventory()), 0);
                                GlStateManager.translated(0, layer * 0.05, 0);
                                GlStateManager.translated((layerIndex % width) * 0.75, 0, (float) (layerIndex / width) * 0.5);
                                GlStateManager.translated(0.7 * (layer % 2), 0, 0);
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
                    }
                    GlStateManager.popMatrix();
                }
            }
        }

        this.renderSpike(entity, -12.0F * 0.0625F, partialTicks);
        this.renderSpike(entity, -8.0F * 0.0625F, partialTicks);
        this.renderSpike(entity, -4.0F * 0.0625F, partialTicks);
        this.renderSpike(entity, 0.0F, partialTicks);
        this.renderSpike(entity, 4.0F * 0.0625F, partialTicks);
        this.renderSpike(entity, 8.0F * 0.0625F, partialTicks);
        this.renderSpike(entity, 12.0F * 0.0625F, partialTicks);
    }

    private void renderSpike(TrailerEntity trailer, double offsetX, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translated(offsetX, -0.65, 0.0);
        float wheelRotation = trailer.prevWheelRotation + (trailer.wheelRotation - trailer.prevWheelRotation) * partialTicks;
        GlStateManager.rotatef(-wheelRotation, 1, 0, 0);
        GlStateManager.scalef(0.75F, 0.75F, 0.75F);
        RenderUtil.renderColoredModel(SpecialModels.SEED_SPIKER.getModel(), ItemCameraTransforms.TransformType.NONE, false, -1);
        GlStateManager.popMatrix();
    }
}
