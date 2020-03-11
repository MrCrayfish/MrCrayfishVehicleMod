package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.PlaneEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.math.RayTraceResult;

/**
 * Author: MrCrayfish
 */
public class RenderPlaneWrapper<T extends PlaneEntity & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>> extends RenderVehicleWrapper<T, R>
{
    public RenderPlaneWrapper(R renderVehicle)
    {
        super(renderVehicle);
    }

    @Override
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

        GlStateManager.translated(0.0, 0.5, 0.0);

        float bodyPitch = entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks;
        GlStateManager.rotatef(-bodyPitch, 1, 0, 0);

        float bodyRoll = entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks;
        GlStateManager.rotatef(-bodyRoll, 0, 0, 1);

        GlStateManager.translated(0.0, -0.5, 0.0);

        //Translate the body
        GlStateManager.translated(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

        //Apply vehicle scale
        GlStateManager.scalef((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        GlStateManager.translated(0.0, 0.5, 0.0);

        //Translate the vehicle so it's axles are half way into the ground
        GlStateManager.translated(0.0, properties.getAxleOffset() * 0.0625, 0.0);

        //Translate the vehicle so it's actually riding on it's wheels
        GlStateManager.translated(0.0, properties.getWheelOffset() * 0.0625, 0.0);

        //Render body
        renderVehicle.render(entity, partialTicks);

        //Render the engine if the vehicle has explicitly stated it should
        if(entity.shouldRenderEngine() && entity.hasEngine())
        {
            IBakedModel engineModel = RenderUtil.getEngineModel(entity);
            this.renderEngine(entity, properties.getEnginePosition(), engineModel);
        }

        //Render the fuel port of the vehicle
        if(entity.shouldRenderFuelPort() && entity.requiresFuel())
        {
            PoweredVehicleEntity.FuelPortType fuelPortType = entity.getFuelPortType();
            EntityRaytracer.RayTraceResultRotated result = EntityRaytracer.getContinuousInteraction();
            if(result != null && result.getType() == RayTraceResult.Type.ENTITY && result.getEntity() == entity && result.equalsContinuousInteraction(EntityRaytracer.FUNCTION_FUELING))
            {
                this.renderPart(properties.getFuelPortPosition(), fuelPortType.getOpenModel().getModel(), entity.getColor());
                if(renderVehicle.shouldRenderFuelLid())
                {
                    //this.renderPart(properties.getFuelPortLidPosition(), entity.fuelPortLid);
                }
                entity.playFuelPortOpenSound();
            }
            else
            {
                this.renderPart(properties.getFuelPortPosition(), fuelPortType.getClosedModel().getModel(), entity.getColor());
                entity.playFuelPortCloseSound();
            }
        }

        if(entity.isKeyNeeded())
        {
            this.renderPart(properties.getKeyPortPosition(), renderVehicle.getKeyHoleModel().getModel(), entity.getColor());
            if(!entity.getKeyStack().isEmpty())
            {
                this.renderKey(properties.getKeyPosition(), RenderUtil.getModel(entity.getKeyStack()), -1);
            }
        }

        GlStateManager.popMatrix();
    }
}
