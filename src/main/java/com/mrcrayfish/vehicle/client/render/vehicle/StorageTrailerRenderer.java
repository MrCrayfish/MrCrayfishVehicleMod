package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractTrailerRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.trailer.StorageTrailerEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Calendar;

/**
 * Author: MrCrayfish
 */
public class StorageTrailerRenderer extends AbstractTrailerRenderer<StorageTrailerEntity>
{
    //private static final ModelChest MOPED_CHEST = new ModelChest();
    private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");
    private final boolean isChristmas;

    public StorageTrailerRenderer(VehicleProperties properties)
    {
        super(properties);
        Calendar calendar = Calendar.getInstance();
        this.isChristmas = calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26;
    }

    @Override
    public void render(@Nullable StorageTrailerEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.STORAGE_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, false, -11.5F * 0.0625F, -0.5F, 0.0F, 1.25F, partialTicks, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, true, 11.5F * 0.0625F, -0.5F, 0.0F, 1.25F, partialTicks, light);

        matrixStack.pushPose();
        matrixStack.translate(0, 0.0625, 0);
        matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(180F));
        matrixStack.scale(0.9F, 0.9F, 0.9F);
        ItemStack chest = new ItemStack(Blocks.CHEST);
        RenderUtil.renderModel(chest, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, light, OverlayTexture.NO_OVERLAY, RenderUtil.getModel(chest));
        matrixStack.popPose();
        /*//Render chest
        GlStateManager.pushMatrix(); //TODO add this back once I create a model class for chest
        {
            GlStateManager.translate(0.0, 0.0625, 0.0);
            GlStateManager.rotate(180F, 0, 1, 0);
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            GlStateManager.scale(0.9F, 0.9F, 0.9F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            if(this.isChristmas)
            {
                Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_CHRISTMAS);
            }
            else
            {
                Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_NORMAL);
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            MOPED_CHEST.renderAll();
        }
        GlStateManager.popMatrix();*/
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.STORAGE_TRAILER, parts, transforms);
        };
    }
}
