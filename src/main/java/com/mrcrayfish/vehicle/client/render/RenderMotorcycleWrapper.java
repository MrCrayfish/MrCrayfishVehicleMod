package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityMotorcycle;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

/**
 * Author: MrCrayfish
 */
public class RenderMotorcycleWrapper<T extends EntityMotorcycle & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>> extends RenderLandVehicleWrapper<T, R>
{
    public RenderMotorcycleWrapper(R renderVehicle)
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

            //Applies leaning rotation caused by turning
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * -20F, 0, 0, 1);

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
                GlStateManager.rotate(-30F * wheelieProgress, 1, 0, 0);
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
}
