package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

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
            GlStateManager.translate(0, properties.getAxleOffset() * 0.0625F, 0);

            //Translate the vehicle so it's actually riding on it's wheels
            GlStateManager.translate(0, properties.getWheelOffset() * 0.0625F, 0);

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
                this.renderEngine(entity, properties.getEnginePosition(), entity.engine);
            }

            //Render the fuel port of the vehicle
            if(entity.shouldRenderFuelPort() && entity.requiresFuel())
            {
                EntityRaytracer.RayTraceResultRotated result = EntityRaytracer.getContinuousInteraction();
                if (result != null && result.entityHit == entity && result.equalsContinuousInteraction(EntityRaytracer.FUNCTION_FUELING))
                {
                    this.renderPart(properties.getFuelPortPosition(), entity.fuelPortBody);
                    if(renderVehicle.shouldRenderFuelLid())
                    {
                        this.renderPart(properties.getFuelPortLidPosition(), entity.fuelPortLid);
                    }
                    entity.playFuelPortOpenSound();
                }
                else
                {
                    this.renderPart(properties.getFuelPortPosition(), entity.fuelPortClosed);
                    entity.playFuelPortCloseSound();
                }
            }


            if(entity.isKeyNeeded())
            {
                this.renderPart(properties.getKeyPortPosition(), entity.keyPort);
                if(!entity.getKeyStack().isEmpty())
                {
                    this.renderKey(properties.getKeyPosition(), entity.getKeyStack());
                }
            }
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
                    GlStateManager.rotate(wheelAngle / 2.0F, 0, 1, 0);
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
                Minecraft.getMinecraft().getRenderItem().renderItem(vehicle.wheel, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
