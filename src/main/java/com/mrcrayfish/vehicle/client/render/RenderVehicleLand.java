package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class RenderVehicleLand<T extends EntityLandVehicle & EntityRaytracer.IEntityRaytraceable> extends RenderVehicle<T>
{
    private AbstractRenderLandVehicle<T> renderVehicle;

    public RenderVehicleLand(RenderManager renderManager, AbstractRenderLandVehicle<T> renderVehicle)
    {
        super(renderManager);
        this.renderVehicle = renderVehicle;
    }

    public AbstractRenderVehicle<T> getRenderVehicle()
    {
        return renderVehicle;
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        {
            //Enable the standard item lighting so vehicles render correctly
            RenderHelper.enableStandardItemLighting();

            //Translate and rotate using parameters
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(-entityYaw, 0, 1, 0);

            //Applies the break animation
            this.setupBreakAnimation(entity, partialTicks);

            //TODO make vehicle translate to height of axels for better positioning
            //Apply vehicle rotations and translations. This is applied to all other parts
            PartPosition bodyPosition = entity.getBodyPosition();
            GlStateManager.rotate((float) bodyPosition.getRotX(), 1, 0, 0);
            GlStateManager.rotate((float) bodyPosition.getRotY(), 1, 0, 0);
            GlStateManager.rotate((float) bodyPosition.getRotZ(), 1, 0, 0);

            //Applies the additional yaw which is caused by drifting
            float additionalYaw = entity.prevAdditionalYaw + (entity.additionalYaw - entity.prevAdditionalYaw) * partialTicks;
            GlStateManager.rotate(additionalYaw, 0, 1, 0);

            //Translate the body
            GlStateManager.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

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

            //Translate the vehicle to match how it is shown in the model creator
            GlStateManager.translate(0, 0.5, 0);

            //Apply vehicle scale
            GlStateManager.translate(0, -0.5, 0);
            GlStateManager.scale(bodyPosition.getScale(), bodyPosition.getScale(), bodyPosition.getScale());
            GlStateManager.translate(0, 0.5, 0);

            //Translate the vehicle so it's axles are half way into the ground
            GlStateManager.translate(0, renderVehicle.getAxelOffset() * 0.0625F, 0);

            //Translate the vehicle so it's actually riding on it's wheels
            GlStateManager.translate(0, renderVehicle.getWheelOffset() * 0.0625F, 0);

            //Render body
            renderVehicle.render(entity, partialTicks);

            //Render vehicle wheels
            GlStateManager.pushMatrix();
            {
                //Offset wheels and compensate for axle offset
                GlStateManager.translate(0, -8 * 0.0625, 0);
                GlStateManager.translate(0, -renderVehicle.getAxelOffset() * 0.0625F, 0);
                renderVehicle.getWheels().forEach(wheel -> wheel.render(entity, partialTicks));
            }
            GlStateManager.popMatrix();

            //Render the engine if the vehicle has explicitly stated it should
            if(entity.shouldRenderEngine())
            {
                this.renderPart(renderVehicle.getEnginePosition(), entity.engine);
            }

            //Render the fuel port of the vehicle
            if(entity.shouldRenderFuelPort())
            {
                EntityRaytracer.RayTraceResultRotated result = EntityRaytracer.getContinuousInteraction();
                if (result != null && result.entityHit == entity && result.equalsContinuousInteraction(EntityRaytracer.FUNCTION_FUELING))
                {
                    this.renderPart(renderVehicle.getFuelPortPosition(), entity.fuelPortBody);
                    if(renderVehicle.shouldRenderFuelLid())
                    {
                        this.renderPart(renderVehicle.getFuelPortLidPosition(), entity.fuelPortLid);
                    }
                    entity.playFuelPortOpenSound();
                }
                else
                {
                    this.renderPart(renderVehicle.getFuelPortPosition(), entity.fuelPortClosed);
                    entity.playFuelPortCloseSound();
                }
            }
        }
        GlStateManager.popMatrix();

        EntityRaytracer.renderRaytraceElements(entity, x, y, z, entityYaw);
    }
}
