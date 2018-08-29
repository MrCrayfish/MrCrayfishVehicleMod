package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.EntityRaytracer.RayTraceResultRotated;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class RenderPoweredVehicle<T extends EntityPoweredVehicle> extends RenderVehicle<T>
{
    private PartPosition enginePosition = new PartPosition(0, 0, 0, 0, 1.0F);
    private PartPosition fuelPortBodyPosition = new PartPosition(0, 0, 0, 0, 0.25F);
    private PartPosition fuelPortLidPosition = new PartPosition(0, 0, 0, 0, 0.25F);

    protected RenderPoweredVehicle(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity)
    {
        return null;
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if(entity.shouldRenderEngine())
        {
            renderPart(enginePosition, entity.engine);
        }

        if(entity.shouldRenderFuelPort())
        {
            RayTraceResultRotated result = EntityRaytracer.getContinuousInteraction();
            if (result != null && result.entityHit == entity && result.equalsContinuousInteraction(EntityRaytracer.FUNCTION_FUELING))
            {
                renderPart(fuelPortBodyPosition, entity.fuelPortBody);
                renderPart(fuelPortLidPosition, entity.fuelPortLid);
            }
            else
            {
                renderPart(fuelPortBodyPosition, entity.fuelPortClosed);
            }
        }
    }

    private void renderPart(PartPosition partPosition, ItemStack part)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(partPosition.x * 0.0625, partPosition.y * 0.0625, partPosition.z * 0.0625);
            GlStateManager.translate(0, -0.5, 0);
            GlStateManager.scale(partPosition.scale, partPosition.scale, partPosition.scale);
            GlStateManager.translate(0, 0.5, 0);
            GlStateManager.rotate(partPosition.rotation, 0, 1, 0);
            Minecraft.getMinecraft().getRenderItem().renderItem(part, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();
    }

    private void setPartPosition(PartPosition partPosition, float x, float y, float z, float rotation, float scale)
    {
        partPosition.x = x;
        partPosition.y = y;
        partPosition.z = z;
        partPosition.rotation = rotation;
        partPosition.scale = scale;
    }

    public void setEnginePosition(float x, float y, float z, float rotation, float scale)
    {
        setPartPosition(enginePosition, x, y, z, rotation, scale);
    }

    public void setFuelPortPosition(float x, float y, float z, float rotation, float scale)
    {
        setPartPosition(fuelPortBodyPosition, x, y, z, rotation, scale);
        setPartPosition(fuelPortLidPosition, x, y, z, rotation - 110, scale);
    }

    private static class PartPosition
    {
        private float x, y, z;
        private float rotation;
        private float scale;

        public PartPosition(float x, float y, float z, float rotation, float scale)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.rotation = rotation;
            this.scale = scale;
        }
    }
}
