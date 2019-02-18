package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

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
            //Enable the standard item lighting so vehicles render correctly
            RenderHelper.enableStandardItemLighting();

            //Apply vehicle rotations and translations. This is applied to all other parts
            PartPosition bodyPosition = entity.getBodyPosition();
            GlStateManager.rotate((float) bodyPosition.getRotX(), 1, 0, 0);
            GlStateManager.rotate((float) bodyPosition.getRotY(), 0, 1, 0);
            GlStateManager.rotate((float) bodyPosition.getRotZ(), 0, 0, 1);

            //Render the tow bar. Performed before scaling so size is consistent for all vehicles
            if(entity.canTowTrailer())
            {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(180F, 0, 1, 0);

                Vec3d towBarOffset = entity.getTowBarVec();
                GlStateManager.translate(towBarOffset.x, towBarOffset.y + 0.5, -towBarOffset.z);
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.towBar, ItemCameraTransforms.TransformType.NONE);
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
            GlStateManager.translate(0, entity.getAxleOffset() * 0.0625F, 0);

            //Translate the vehicle so it's actually riding on it's wheels
            GlStateManager.translate(0, entity.getWheelOffset() * 0.0625F, 0);

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
     * @param part the part to render onto the vehicle
     */
    protected void renderPart(@Nullable PartPosition position, ItemStack part)
    {
        if(position == null)
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
            Minecraft.getMinecraft().getRenderItem().renderItem(part, ItemCameraTransforms.TransformType.NONE);
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
     * @param position the render definitions to apply to the part
     * @param part the part to render onto the vehicle
     */
    protected void renderEngine(EntityPoweredVehicle entity, @Nullable PartPosition position, ItemStack part)
    {
        if(entity.isFueled() && entity.getControllingPassenger() != null)
        {
            GlStateManager.rotate(0.5F * (entity.ticksExisted % 2), 1, 0, 1);
            GlStateManager.rotate(-0.5F * (entity.ticksExisted % 2), 0, 1, 0);
        }
        this.renderPart(position, part);
    }
}
