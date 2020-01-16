package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.SeederTrailerEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class RenderSeederTrailer extends AbstractRenderTrailer<SeederTrailerEntity>
{
    @Override
    public SpecialModel getBodyModel()
    {
        return SpecialModel.SEEDER_TRAILER;
    }

    @Override
    public void render(SeederTrailerEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        //Render the body
        this.renderDamagedPart(entity, SpecialModel.SEEDER_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, true, -17.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, false, 17.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks, light);

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
                    matrixStack.func_227860_a_();
                    {
                        matrixStack.func_227861_a_(-10.5 * 0.0625, -3 * 0.0625, -2 * 0.0625);
                        matrixStack.func_227862_a_(0.45F, 0.45F, 0.45F);

                        int count = Math.max(1, stack.getCount() / 16);
                        int width = 4;
                        int maxLayerCount = 8;
                        for(int j = 0; j < count; j++)
                        {
                            matrixStack.func_227860_a_();
                            {
                                int layerIndex = index % maxLayerCount;
                                //double yOffset = Math.sin(Math.PI * (((layerIndex + 0.5) % (double) width) / (double) width)) * 0.1;
                                //GlStateManager.translate(0, yOffset * ((double) layer / inventory.getSizeInventory()), 0);
                                matrixStack.func_227861_a_(0, layer * 0.05, 0);
                                matrixStack.func_227861_a_((layerIndex % width) * 0.75, 0, (float) (layerIndex / width) * 0.5);
                                matrixStack.func_227861_a_(0.7 * (layer % 2), 0, 0);
                                matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(90F));
                                matrixStack.func_227863_a_(Axis.POSITIVE_Z.func_229187_a_(47F * index));
                                matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(2F * layerIndex));
                                matrixStack.func_227861_a_(layer * 0.001, layer * 0.001, layer * 0.001); // Fixes Z fighting
                                Minecraft.getInstance().getItemRenderer().func_229110_a_(stack, ItemCameraTransforms.TransformType.NONE, light, OverlayTexture.field_229196_a_, matrixStack, renderTypeBuffer);
                            }
                            matrixStack.func_227865_b_();
                            index++;
                            if(index % maxLayerCount == 0)
                            {
                                layer++;
                            }
                        }
                    }
                    matrixStack.func_227865_b_();
                }
            }
        }

        this.renderSpike(entity, matrixStack, renderTypeBuffer, -12.0F * 0.0625F, partialTicks, light);
        this.renderSpike(entity, matrixStack, renderTypeBuffer, -8.0F * 0.0625F, partialTicks, light);
        this.renderSpike(entity, matrixStack, renderTypeBuffer, -4.0F * 0.0625F, partialTicks, light);
        this.renderSpike(entity, matrixStack, renderTypeBuffer, 0.0F, partialTicks, light);
        this.renderSpike(entity, matrixStack, renderTypeBuffer, 4.0F * 0.0625F, partialTicks, light);
        this.renderSpike(entity, matrixStack, renderTypeBuffer, 8.0F * 0.0625F, partialTicks, light);
        this.renderSpike(entity, matrixStack, renderTypeBuffer, 12.0F * 0.0625F, partialTicks, light);
    }

    private void renderSpike(TrailerEntity trailer, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, double offsetX, float partialTicks, int light)
    {
        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(offsetX, -0.65, 0.0);
        float wheelRotation = trailer.prevWheelRotation + (trailer.wheelRotation - trailer.prevWheelRotation) * partialTicks;
        matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-wheelRotation));
        matrixStack.func_227862_a_(0.75F, 0.75F, 0.75F);
        RenderUtil.renderColoredModel(SpecialModel.SEED_SPIKER.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.field_229196_a_);
        matrixStack.func_227865_b_();
    }
}
