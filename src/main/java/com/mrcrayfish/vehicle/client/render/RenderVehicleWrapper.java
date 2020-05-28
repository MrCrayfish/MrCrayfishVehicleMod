package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Author: MrCrayfish
 */
public class RenderVehicleWrapper<T extends EntityVehicle & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>>
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
        if(entity.isDead)
            return;

        GlStateManager.pushMatrix();
        {
            VehicleProperties properties = entity.getProperties();

            //Apply vehicle rotations and translations. This is applied to all other parts
            PartPosition bodyPosition = properties.getBodyPosition();
            GlStateManager.rotate((float) bodyPosition.getRotX(), 1, 0, 0);
            GlStateManager.rotate((float) bodyPosition.getRotY(), 0, 1, 0);
            GlStateManager.rotate((float) bodyPosition.getRotZ(), 0, 0, 1);

            //Render the tow bar. Performed before scaling so size is consistent for all vehicles
            if(entity.canTowTrailer())
            {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(180F, 0, 1, 0);
                Vec3d towBarOffset = properties.getTowBarPosition();
                GlStateManager.translate(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, -towBarOffset.z * 0.0625);
                RenderUtil.renderModel(SpecialModels.TOW_BAR.getModel(), ItemCameraTransforms.TransformType.NONE);
                GlStateManager.popMatrix();
            }

            //Translate the body
            GlStateManager.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

            //Translate the vehicle to match how it is shown in the model creator
            GlStateManager.translate(0, 0.5, 0);

            //Apply vehicle scale
            GlStateManager.translate(0, -0.5, 0);
            GlStateManager.scale(bodyPosition.getScale(), bodyPosition.getScale(), bodyPosition.getScale());
            GlStateManager.translate(0, 0.5, 0);

            //Translate the vehicle so it's axles are half way into the ground
            GlStateManager.translate(0, properties.getAxleOffset() * 0.0625F, 0);

            //Translate the vehicle so it's actually riding on it's wheels
            GlStateManager.translate(0, properties.getWheelOffset() * 0.0625F, 0);

            //Render body
            renderVehicle.render(entity, partialTicks);
        }
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
     * @param model the model of the part
     */
    protected void renderPart(@Nullable PartPosition position, @Nullable IBakedModel model, int color)
    {
        if(position == null || model == null)
            return;

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
            GlStateManager.translate(0, -0.5, 0);
            GlStateManager.scale(position.getScale(), position.getScale(), position.getScale());
            GlStateManager.translate(0, 0.5, 0);
            GlStateManager.rotate((float) position.getRotX(), 1, 0, 0);
            GlStateManager.rotate((float) position.getRotY(), 0, 1, 0);
            GlStateManager.rotate((float) position.getRotZ(), 0, 0, 1);
            RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, color);
        }
        GlStateManager.popMatrix();
    }

    /**
     * Renders a part (ItemStack) on the vehicle using the specified PartPosition. The rendering
     * will be cancelled if the PartPosition parameter is null.
     *
     * @param position the render definitions to apply to the part
     * @param model the special model of the part
     */
    protected void renderPart(@Nullable PartPosition position, @Nullable SpecialModels model, int color)
    {
        if(position == null || model == null)
            return;

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
            GlStateManager.translate(0, -0.5, 0);
            GlStateManager.scale(position.getScale(), position.getScale(), position.getScale());
            GlStateManager.translate(0, 0.5, 0);
            GlStateManager.rotate((float) position.getRotX(), 1, 0, 0);
            GlStateManager.rotate((float) position.getRotY(), 0, 1, 0);
            GlStateManager.rotate((float) position.getRotZ(), 0, 0, 1);
            RenderUtil.renderColoredModel(model .getModel(), ItemCameraTransforms.TransformType.NONE, color);
        }
        GlStateManager.popMatrix();
    }

    protected void renderKey(@Nullable PartPosition position, ItemStack part)
    {
        if(position == null)
            return;

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
            GlStateManager.translate(0, -0.25, 0);
            GlStateManager.scale(position.getScale(), position.getScale(), position.getScale());
            GlStateManager.rotate((float) position.getRotX(), 1, 0, 0);
            GlStateManager.rotate((float) position.getRotY(), 0, 1, 0);
            GlStateManager.rotate((float) position.getRotZ(), 0, 0, 1);
            GlStateManager.translate(0, 0, -0.05);
            Minecraft.getMinecraft().getRenderItem().renderItem(part, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();
    }


    /**
     * Renders the engine (ItemStack) on the vehicle using the specified PartPosition. It adds a
     * subtle shake to the render to simulate it being powered.
     *
     * @param entity the powered vehicle to render the engine to
     * @param position the render definitions to apply to the part
     */
    protected void renderEngine(EntityPoweredVehicle entity, @Nullable PartPosition position)
    {
        GlStateManager.pushMatrix();
        if(entity.isEnginePowered() && entity.getControllingPassenger() != null)
        {
            GlStateManager.rotate(0.5F * (entity.ticksExisted % 2), 1, 0, 1);
            GlStateManager.rotate(-0.5F * (entity.ticksExisted % 2), 0, 1, 0);
        }
        IBakedModel model = RenderUtil.getEngineModel(entity);
        if(model != null)
        {
            this.renderPart(position, model, -1);
        }
        GlStateManager.popMatrix();
    }

    /**
     * Renders the fuel port onto the vehicle model at the specified part position
     *
     * @param entity the powered vehicle to render the fuel port to
     * @param position the render definitions to apply to the part
     */
    protected void renderFuelPort(EntityPoweredVehicle entity, @Nullable PartPosition position)
    {
        if(entity.shouldRenderFuelPort() && entity.requiresFuel())
        {
            Color color = new Color(entity.getColor());
            int brightness = (int) Math.sqrt(color.getRed() * color.getRed() * 0.241 + color.getGreen() * color.getGreen() * 0.691 + color.getBlue() * color.getBlue() * 0.068);
            int colorInt = (brightness > 127 ? color.darker() : color.brighter()).getRGB();

            EntityRaytracer.RayTraceResultRotated result = EntityRaytracer.getContinuousInteraction();
            if (result != null && result.entityHit == entity && result.equalsContinuousInteraction(EntityRaytracer.FUNCTION_FUELING))
            {
                this.renderPart(position, entity.getFuelPort().getBody(), colorInt);
                if(renderVehicle.shouldRenderFuelLid())
                {
                    this.renderPart(position, entity.getFuelPort().getLid(), colorInt);
                }
                entity.playFuelPortOpenSound();
            }
            else
            {
                SpecialModels model = entity.getFuelPort().getClosed();
                if(model != null)
                {
                    this.renderPart(position, model, colorInt);
                }
                entity.playFuelPortCloseSound();
            }
        }
    }

    /**
     * Renders the key port onto the vehicle model
     *
     * @param entity the powered vehicle to render the key port to
     */
    protected void renderKeyPort(EntityPoweredVehicle entity)
    {
        if(entity.isKeyNeeded())
        {
            Color color = new Color(entity.getColor());
            int brightness = (int) Math.sqrt(color.getRed() * color.getRed() * 0.241 + color.getGreen() * color.getGreen() * 0.691 + color.getBlue() * color.getBlue() * 0.068);
            int colorInt = (brightness > 127 ? color.darker() : color.brighter()).getRGB();

            VehicleProperties properties = entity.getProperties();
            this.renderPart(properties.getKeyPortPosition(), SpecialModels.KEY_HOLE.getModel(), colorInt);
            if(!entity.getKeyStack().isEmpty())
            {
                this.renderKey(properties.getKeyPosition(), entity.getKeyStack());
            }
        }
    }
}
