package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.Models;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.RenderVehicle;
import com.mrcrayfish.vehicle.entity.trailer.EntityStorageTrailer;
import com.mrcrayfish.vehicle.entity.trailer.EntityTrailer;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import java.util.Calendar;

/**
 * Author: MrCrayfish
 */
public class RenderChestTrailer extends RenderVehicle<EntityStorageTrailer, AbstractRenderVehicle<EntityStorageTrailer>>
{
    private static final ModelChest MOPED_CHEST = new ModelChest();
    private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");
    public final boolean isChristmas;

    public RenderChestTrailer(RenderManager renderManager)
    {
        super(renderManager, null);
        Calendar calendar = Calendar.getInstance();
        this.isChristmas = calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26;
    }

    @Override
    public void doRender(EntityStorageTrailer entity, double x, double y, double z, float currentYaw, float partialTicks)
    {
        RenderHelper.enableStandardItemLighting();

        EntityLivingBase entityLivingBase = (EntityLivingBase) entity.getControllingPassenger();
        if(entityLivingBase != null)
        {
            entityLivingBase.renderYawOffset = currentYaw;
            entityLivingBase.prevRenderYawOffset = currentYaw;
        }

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(-currentYaw, 0, 1, 0);
            GlStateManager.scale(1.1, 1.1, 1.1);

            this.setupBreakAnimation(entity, partialTicks);

            double bodyOffset = 0.8;

            //Render the body
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, bodyOffset, 0);
                RenderUtil.renderItemModel(entity.body, Models.CHEST_TRAILER.getModel(), ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();

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
                    this.bindTexture(TEXTURE_CHRISTMAS);
                }
                else
                {
                    this.bindTexture(TEXTURE_NORMAL);
                }
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                MOPED_CHEST.renderAll();
            }
            GlStateManager.popMatrix();

            RenderHelper.enableStandardItemLighting();

            renderWheel(entity, -11.5F * 0.0625F, 0.3F, 0.0F, 2.0F, partialTicks);
            renderWheel(entity, 11.5F * 0.0625F, 0.3F, 0.0F, 2.0F, partialTicks);
        }
        GlStateManager.popMatrix();

        EntityRaytracer.renderRaytraceElements(entity, x, y, z, currentYaw);
    }

    public void renderWheel(EntityTrailer trailer, float offsetX, float offsetY, float offsetZ, float wheelScale, float partialTicks)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(offsetX, offsetY, offsetZ);
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
