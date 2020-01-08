package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.BoatEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.RayTraceResult;

/**
 * Author: MrCrayfish
 */
public class RenderBoatWrapper<T extends BoatEntity & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>> extends RenderVehicleWrapper<T, R>
{
    public RenderBoatWrapper(R renderVehicle)
    {
        super(renderVehicle);
    }

    @Override
    public void render(T entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        if(!entity.isAlive())
            return;

        matrixStack.func_227860_a_();

        VehicleProperties properties = entity.getProperties();
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_((float) bodyPosition.getRotX()));
        matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_((float) bodyPosition.getRotY()));
        matrixStack.func_227863_a_(Axis.POSITIVE_Z.func_229187_a_((float) bodyPosition.getRotZ()));

        //Applies leaning rotation caused by turning
        float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
        float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / entity.getMaxTurnAngle();
        matrixStack.func_227863_a_(Axis.POSITIVE_Z.func_229187_a_(turnAngleNormal * currentSpeedNormal * -15F));

        //Makes the boat tilt up the faster it goes
        matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-8F * Math.min(1.0F, currentSpeedNormal)));

        //Translate the body
        matrixStack.func_227861_a_(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

        //Translate the vehicle to match how it is shown in the model creator
        matrixStack.func_227861_a_(0.0, 0.5, 0.0);

        //Apply vehicle scale
        matrixStack.func_227861_a_(0.0, -0.5, 0.0);
        matrixStack.func_227862_a_((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.func_227861_a_(0.0, 0.5, 0.0);

        //Translate the vehicle so it's axles are half way into the ground
        matrixStack.func_227861_a_(0.0, properties.getAxleOffset() * 0.0625, 0.0);

        //Translate the vehicle so it's actually riding on it's wheels
        matrixStack.func_227861_a_(0.0, properties.getWheelOffset() * 0.0625, 0.0);

        //Render body
        renderVehicle.render(entity, matrixStack, renderTypeBuffer, partialTicks, light);

        //Render the engine if the vehicle has explicitly stated it should
        if(entity.shouldRenderEngine() && entity.hasEngine())
        {
            this.renderEngine(entity, properties.getEnginePosition(), this.getEngineModel(entity), matrixStack, renderTypeBuffer, light);
        }

        //Render the fuel port of the vehicle
        if(entity.shouldRenderFuelPort() && entity.requiresFuel())
        {
            PoweredVehicleEntity.FuelPortType fuelPortType = entity.getFuelPortType();
            EntityRaytracer.RayTraceResultRotated result = EntityRaytracer.getContinuousInteraction();
            if(result != null && result.getType() == RayTraceResult.Type.ENTITY && result.getEntity() == entity && result.equalsContinuousInteraction(EntityRaytracer.FUNCTION_FUELING))
            {
                this.renderPart(properties.getFuelPortPosition(), fuelPortType.getOpenModel().getModel(), matrixStack, renderTypeBuffer, entity.getColor(), light, OverlayTexture.field_229196_a_);
                if(renderVehicle.shouldRenderFuelLid())
                {
                    //this.renderPart(properties.getFuelPortLidPosition(), entity.fuelPortLid);
                }
                entity.playFuelPortOpenSound();
            }
            else
            {
                this.renderPart(properties.getFuelPortPosition(), fuelPortType.getClosedModel().getModel(), matrixStack, renderTypeBuffer, entity.getColor(), light, OverlayTexture.field_229196_a_);
                entity.playFuelPortCloseSound();
            }
        }

        if(entity.isKeyNeeded())
        {
            this.renderPart(properties.getKeyPortPosition(), renderVehicle.getKeyHoleModel().getModel(), matrixStack, renderTypeBuffer, entity.getColor(), light, OverlayTexture.field_229196_a_);
            if(!entity.getKeyStack().isEmpty())
            {
                this.renderKey(properties.getKeyPosition(), RenderUtil.getModel(entity.getKeyStack()), matrixStack, renderTypeBuffer, -1, light, OverlayTexture.field_229196_a_);
            }
        }

        matrixStack.func_227865_b_();
    }
}
