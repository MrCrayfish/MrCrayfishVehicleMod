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
import javax.vecmath.Vector3f;

/**
 * Author: MrCrayfish
 */
public abstract class RenderPoweredVehicle<T extends EntityPoweredVehicle> extends RenderVehicle<T>
{
    private PartPosition enginePosition = new PartPosition(1.0F);
    private PartPosition fuelPortBodyPosition = new PartPosition(0.25F);
    private PartPosition fuelPortLidPosition = new PartPosition(0.25F);
    private PartPosition fuelPortClosedPosition = new PartPosition(0.25F);

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
            enginePosition.renderPart(entity.engine);
        }

        if(entity.shouldRenderFuelPort())
        {
            RayTraceResultRotated result = EntityRaytracer.getContinuousInteraction();
            if (result != null && result.entityHit == entity && result.equalsContinuousInteraction(EntityRaytracer.FUNCTION_FUELING))
            {
                fuelPortBodyPosition.renderPart(entity.fuelPort.getBody());
                fuelPortLidPosition.renderPart(entity.fuelPort.getLid());
                entity.playFuelPortOpenSound();
            }
            else
            {
                fuelPortClosedPosition.renderPart(entity.fuelPort.getClosed());
                entity.playFuelPortCloseSound();
            }
        }
    }

    private void setPartPosition(PartPosition partPosition, float x, float y, float z, Vector3f rotation, float scale)
    {
        partPosition.x = x;
        partPosition.y = y;
        partPosition.z = z;
        partPosition.rotation = rotation;
        partPosition.scale = scale;
    }

    public void setEnginePosition(float x, float y, float z, float rotation, float scale)
    {
        setPartPosition(enginePosition, x, y, z, new Vector3f(0, rotation, 0), scale);
    }

    public void setFuelPortPosition(float x, float y, float z, float rotation, float scale)
    {
        setFuelPortPosition(x, y, z, new Vector3f(0, rotation, 0), scale);
    }

    public void setFuelPortPosition(float x, float y, float z, Vector3f rotation, float scale)
    {
        setPartPosition(fuelPortClosedPosition, x, y, z, rotation, scale);
        setPartPosition(fuelPortBodyPosition, x, y, z, rotation, scale);
        setPartPosition(fuelPortLidPosition, x, y, z, new Vector3f(rotation.x, rotation.y - 110, rotation.z), scale);
    }

    public void setFuelPortClosedPosition(float x, float y, float z, Vector3f rotation, float scale)
    {
        setPartPosition(fuelPortClosedPosition, x, y, z, rotation, scale);
    }

    public void setFuelPortLidPosition(float x, float y, float z, Vector3f rotation, float scale)
    {
        setPartPosition(fuelPortLidPosition, x, y, z, rotation, scale);
    }

    private static class PartPosition
    {
        private float x, y, z;
        private Vector3f rotation;
        private float scale;

        public PartPosition(float scale)
        {
            this(0, 0, 0, new Vector3f(0, 0, 0), scale);
        }

        public PartPosition(float x, float y, float z, Vector3f rotation, float scale)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.rotation = rotation;
            this.scale = scale;
        }

        public void renderPart(ItemStack part)
        {
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(x * 0.0625, y * 0.0625, z * 0.0625);
                GlStateManager.translate(0, -0.5, 0);
                GlStateManager.scale(scale, scale, scale);
                GlStateManager.translate(0, 0.5, 0);
                GlStateManager.rotate(rotation.x, 1, 0, 0);
                GlStateManager.rotate(rotation.y, 0, 1, 0);
                GlStateManager.rotate(rotation.z, 0, 0, 1);
                Minecraft.getMinecraft().getRenderItem().renderItem(part, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();
        }
    }
}
