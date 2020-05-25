package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class RenderLandVehicleWrapper<T extends EntityLandVehicle & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>> extends RenderVehicleWrapper<T, R>
{
    public RenderLandVehicleWrapper(R renderVehicle)
    {
        super(renderVehicle);
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

                Vec3d towBarOffset = properties.getTowBarPosition();
                GlStateManager.translate(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, -towBarOffset.z * 0.0625);
                RenderUtil.renderModel(this.renderVehicle.getTowBarModel().getModel(), ItemCameraTransforms.TransformType.NONE);
                GlStateManager.popMatrix();
            }

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

            if(entity.canWheelie())
            {
                if(properties.getRearAxelVec() == null)
                {
                    return;
                }
                GlStateManager.translate(0.0, -0.5, 0.0);
                GlStateManager.translate(0.0, -properties.getAxleOffset() * 0.0625, 0.0);
                GlStateManager.translate(0.0, 0.0, properties.getRearAxelVec().z * 0.0625);
                float wheelieProgress = (float) (MathHelper.clampedLerp(entity.prevWheelieCount, entity.wheelieCount, partialTicks) / 4F);
                wheelieProgress = (float) (1.0 - Math.pow(1.0 - wheelieProgress, 2));
                GlStateManager.rotate(-30F * wheelieProgress, 1, 0, 0); //TODO test
                GlStateManager.translate(0.0, 0.0, -properties.getRearAxelVec().z * 0.0625);
                GlStateManager.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);
                GlStateManager.translate(0.0, 0.5, 0.0);
            }

            //Render body
            renderVehicle.render(entity, partialTicks);

            //Render vehicle wheels
            if(entity.hasWheels())
            {
                GlStateManager.pushMatrix();
                {
                    //Offset wheels and compensate for axle offset
                    GlStateManager.translate(0, -8 * 0.0625, 0);
                    GlStateManager.translate(0, -properties.getAxleOffset() * 0.0625F, 0);
                    properties.getWheels().forEach(wheel -> this.renderWheel(entity, wheel, partialTicks));
                }
                GlStateManager.popMatrix();
            }

            //Render the engine if the vehicle has explicitly stated it should
            if(entity.shouldRenderEngine() && entity.hasEngine())
            {
                this.renderEngine(entity, properties.getEnginePosition());
            }

            //Render the fuel port of the vehicle
            this.renderFuelPort(entity, properties.getFuelPortPosition());

            //Render the key port
            this.renderKeyPort(entity);
        }
        GlStateManager.popMatrix();
    }

    protected void renderWheel(EntityLandVehicle vehicle, Wheel wheel, float partialTicks)
    {
        if(!wheel.shouldRender())
            return;

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate((wheel.getOffsetX() * 0.0625) * wheel.getSide().offset, wheel.getOffsetY() * 0.0625, wheel.getOffsetZ() * 0.0625);
            GlStateManager.pushMatrix();
            {
                if(wheel.getPosition() == Wheel.Position.FRONT)
                {
                    float wheelAngle = vehicle.prevRenderWheelAngle + (vehicle.renderWheelAngle - vehicle.prevRenderWheelAngle) * partialTicks;
                    GlStateManager.rotate(wheelAngle, 0, 1, 0);
                }
                if(vehicle.isMoving())
                {
                    GlStateManager.rotate(-wheel.getWheelRotation(vehicle, partialTicks), 1, 0, 0);
                }
                GlStateManager.translate((((wheel.getWidth() * wheel.getScaleX()) / 2) * 0.0625) * wheel.getSide().offset, 0, 0);
                GlStateManager.scale(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
                if(wheel.getSide() == Wheel.Side.RIGHT)
                {
                    GlStateManager.rotate(180F, 0, 1, 0);
                }
                RenderUtil.renderColoredModel(RenderUtil.getWheelModel(vehicle), ItemCameraTransforms.TransformType.NONE, vehicle.getWheelColor());
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
