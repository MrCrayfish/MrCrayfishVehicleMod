package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.Models;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.trailer.EntityStorageTrailer;
import com.mrcrayfish.vehicle.entity.trailer.EntityTrailer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.util.ResourceLocation;

import java.util.Calendar;

/**
 * Author: MrCrayfish
 */
public class RenderStorageTrailer extends AbstractRenderVehicle<EntityStorageTrailer>
{
    private static final ModelChest MOPED_CHEST = new ModelChest();
    private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");
    private final boolean isChristmas;

    public RenderStorageTrailer()
    {
        Calendar calendar = Calendar.getInstance();
        this.isChristmas = calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26;
    }

    @Override
    public void render(EntityStorageTrailer entity, float partialTicks)
    {
        this.renderDamagedPart(entity, entity.body, Models.CHEST_TRAILER.getModel());
        this.renderWheel(entity, false, -11.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks);
        this.renderWheel(entity, true, 11.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks);

        //Render chest
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0.0, 0.875, 0.0);
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
        GlStateManager.popMatrix();


    }

    private void renderWheel(EntityTrailer trailer, boolean right, float offsetX, float offsetY, float offsetZ, float wheelScale, float partialTicks)
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
                    GlStateManager.rotate(-wheelRotation, 1, 0, 0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    Minecraft.getMinecraft().getRenderItem().renderItem(trailer.wheel, ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
