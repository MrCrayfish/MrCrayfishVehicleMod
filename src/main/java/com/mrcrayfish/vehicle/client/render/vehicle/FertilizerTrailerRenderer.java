package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractTrailerRenderer;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.trailer.FertilizerTrailerEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class FertilizerTrailerRenderer extends AbstractTrailerRenderer<FertilizerTrailerEntity>
{
    protected final PropertyFunction<FertilizerTrailerEntity, StorageInventory> storageProperty = new PropertyFunction<>(FertilizerTrailerEntity::getInventory, null);

    public FertilizerTrailerRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable FertilizerTrailerEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.FERTILIZER_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, false, -11.5F * 0.0625F, -0.5F, 0.0F, 1.25F, partialTicks, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, true, 11.5F * 0.0625F, -0.5F, 0.0F, 1.25F, partialTicks, light);

        StorageInventory inventory = this.storageProperty.get(vehicle);
        if(inventory != null)
        {
            int layer = 0;
            int index = 0;
            for(int i = 0; i < inventory.getContainerSize(); i++)
            {
                ItemStack stack = inventory.getItem(i);
                if(!stack.isEmpty())
                {
                    matrixStack.pushPose();
                    matrixStack.translate(-5.5 * 0.0625, -3 * 0.0625, -3 * 0.0625);
                    matrixStack.scale(0.45F, 0.45F, 0.45F);

                    int count = Math.max(1, stack.getCount() / 32);
                    int width = 3;
                    int maxLayerCount = 6;
                    for(int j = 0; j < count; j++)
                    {
                        matrixStack.pushPose();
                        {
                            int layerIndex = index % maxLayerCount;
                            matrixStack.translate(0, layer * 0.1 + j * 0.0625, 0);
                            matrixStack.translate((layerIndex % width) * 0.5, 0, (float) (layerIndex / width) * 0.75);
                            matrixStack.translate(0.5 * (layer % 2), 0, 0);
                            matrixStack.mulPose(Vector3f.XP.rotationDegrees(90F));
                            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(47F * index));
                            matrixStack.mulPose(Vector3f.XP.rotationDegrees(2F * layerIndex));
                            matrixStack.translate(layer * 0.001, layer * 0.001, layer * 0.001); // Fixes Z fighting
                            Minecraft.getInstance().getItemRenderer().render(stack, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, light, OverlayTexture.NO_OVERLAY, RenderUtil.getModel(stack));
                        }
                        matrixStack.popPose();
                        index++;
                        if(index % maxLayerCount == 0)
                        {
                            layer++;
                        }
                    }
                    matrixStack.popPose();
                }
            }
        }

        /* Renders the spike */
        matrixStack.pushPose();
        {
            matrixStack.translate(0, -0.5, -0.4375);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90F));
            if(vehicle != null)
            {
                float wheelRotation = vehicle.prevWheelRotation + (vehicle.wheelRotation - vehicle.prevWheelRotation) * partialTicks;
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(-wheelRotation));
            }
            matrixStack.scale((float) 1.25, (float) 1.25, (float) 1.25);
            RenderUtil.renderColoredModel(SpecialModels.SEED_SPIKER.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
        }
        matrixStack.popPose();
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.FERTILIZER_TRAILER, parts, transforms);
        };
    }
}
