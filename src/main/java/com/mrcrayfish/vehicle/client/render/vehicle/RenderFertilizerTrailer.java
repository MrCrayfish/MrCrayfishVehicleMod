package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModel;
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
    public SpecialModel getBodyModel()
    {
        return SpecialModel.FERTILIZER_TRAILER;
    }

    @Override
    public void render(FertilizerTrailerEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModel.FERTILIZER_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, false, -11.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, true, 11.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks);

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
                    matrixStack.func_227861_a_(-5.5 * 0.0625, -3 * 0.0625, -3 * 0.0625);
                    matrixStack.func_227862_a_(0.45F, 0.45F, 0.45F);

                    int count = Math.max(1, stack.getCount() / 32);
                    int width = 3;
                    int maxLayerCount = 6;
                    for(int j = 0; j < count; j++)
                    {
                        matrixStack.func_227860_a_();
                        {
                            int layerIndex = index % maxLayerCount;
                            matrixStack.func_227861_a_(0, layer * 0.1 + j * 0.0625, 0);
                            matrixStack.func_227861_a_((layerIndex % width) * 0.5, 0, (float) (layerIndex / width) * 0.75);
                            matrixStack.func_227861_a_(0.5 * (layer % 2), 0, 0);
                            matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(90F));
                            matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(47F * index));
                            matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(2F * layerIndex));
                            matrixStack.func_227861_a_(layer * 0.001, layer * 0.001, layer * 0.001); // Fixes Z fighting
                            Minecraft.getInstance().getItemRenderer().func_229111_a_(stack, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, light, OverlayTexture.field_229196_a_, RenderUtil.getModel(stack));
                        }
                        matrixStack.func_227865_b_();
                        index++;
                        if(index % maxLayerCount == 0)
                        {
                            layer++;
                        }
                    }
                    matrixStack.func_227865_b_();
                }
            }
        }

        /* Renders the spike */
        matrixStack.func_227860_a_();
        {
            matrixStack.func_227861_a_(0, -0.5, -0.4375);
            matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(90F));
            float wheelRotation = entity.prevWheelRotation + (entity.wheelRotation - entity.prevWheelRotation) * partialTicks;
            matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-wheelRotation));
            matrixStack.func_227862_a_((float) 1.25, (float) 1.25, (float) 1.25);
            RenderUtil.renderColoredModel(SpecialModel.SEED_SPIKER.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.field_229196_a_);
        }
        matrixStack.func_227865_b_();
    }
}
