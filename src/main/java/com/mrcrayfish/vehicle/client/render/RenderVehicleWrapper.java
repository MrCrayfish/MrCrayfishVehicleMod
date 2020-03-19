package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class RenderVehicleWrapper<T extends VehicleEntity & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>>
{
    protected final R renderVehicle;

    public RenderVehicleWrapper(R renderVehicle)
    {
        this.renderVehicle = renderVehicle;
    }

    public R getRenderVehicle()
    {
        return renderVehicle;
    }

    public void render(T entity, float partialTicks)
    {
        if(!entity.isAlive())
            return;

        GlStateManager.pushMatrix();

        VehicleProperties properties = entity.getProperties();
        PartPosition bodyPosition = properties.getBodyPosition();
        GlStateManager.rotated(bodyPosition.getRotX(), 1, 0, 0);
        GlStateManager.rotated(bodyPosition.getRotY(), 0, 1, 0);
        GlStateManager.rotated(bodyPosition.getRotZ(), 0, 0, 1);

        if(entity.canTowTrailer())
        {
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(180F, 0, 1, 0);
            Vec3d towBarOffset = properties.getTowBarPosition();
            GlStateManager.translated(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, -towBarOffset.z * 0.0625);
            RenderUtil.renderColoredModel(SpecialModels.TOW_BAR.getModel(), ItemCameraTransforms.TransformType.NONE, false, -1);
            GlStateManager.popMatrix();
        }

        GlStateManager.translated(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        GlStateManager.scaled(bodyPosition.getScale(), bodyPosition.getScale(), bodyPosition.getScale());
        GlStateManager.translated(0.0, 0.5, 0.0);
        GlStateManager.translated(0.0, properties.getAxleOffset() * 0.0625, 0.0);
        GlStateManager.translated(0.0, properties.getWheelOffset() * 0.0625, 0.0);
        this.renderVehicle.render(entity, partialTicks);

        GlStateManager.popMatrix();
    }

    /**
     *
     * @param entity
     * @param partialTicks
     */
    public void applyPreRotations(T entity, float partialTicks) {}

    /**
     * Renders a part (ItemStack) on the vehicle using the specified PartPosition. The rendering
     * will be cancelled if the PartPosition parameter is null.
     *
     * @param position the render definitions to apply to the part
     * @param model the part to render onto the vehicle
     */
    protected void renderPart(@Nullable PartPosition position, IBakedModel model, int color)
    {
        if(position == null)
            return;

        GlStateManager.pushMatrix();
        GlStateManager.translated(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        GlStateManager.translated(0.0, -0.5, 0.0);
        GlStateManager.scalef((float) position.getScale(), (float) position.getScale(), (float) position.getScale());
        GlStateManager.translated(0.0, 0.5, 0.0);
        GlStateManager.rotated(position.getRotX(), 1, 0, 0);
        GlStateManager.rotated(position.getRotY(), 0, 1, 0);
        GlStateManager.rotated(position.getRotZ(), 0, 0, 1);
        RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, color);
        GlStateManager.popMatrix();
    }

    protected void renderKey(@Nullable PartPosition position, IBakedModel model, int color)
    {
        if(position == null)
            return;

        GlStateManager.pushMatrix();
        GlStateManager.translated(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        GlStateManager.translated(0.0, -0.25, 0.0);
        GlStateManager.scalef((float) position.getScale(), (float) position.getScale(), (float) position.getScale());
        GlStateManager.rotated(position.getRotX(), 1, 0, 0);
        GlStateManager.rotated(position.getRotY(), 0, 1, 0);
        GlStateManager.rotated(position.getRotZ(), 0, 0, 1);
        GlStateManager.translated(0.0, 0.0, -0.05);
        RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, color);
        GlStateManager.popMatrix();
    }


    /**
     * Renders the engine (ItemStack) on the vehicle using the specified PartPosition. It adds a
     * subtle shake to the render to simulate it being powered.
     *
     * @param position the render definitions to apply to the part
     */
    protected void renderEngine(PoweredVehicleEntity entity, @Nullable PartPosition position, IBakedModel model)
    {
        GlStateManager.pushMatrix();
        if(entity.isEnginePowered())
        {
            GlStateManager.rotatef(0.5F * (entity.ticksExisted % 2), 1, 0, 1);
            GlStateManager.rotatef(-0.5F * (entity.ticksExisted % 2), 0, 1, 0);
        }
        this.renderPart(position, model, -1);
        GlStateManager.popMatrix();
    }
}
