package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.RayTraceResult;

/**
 * Author: MrCrayfish
 */
public class RenderHelicopterWrapper<T extends HelicopterEntity & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>> extends RenderVehicleWrapper<T, R>
{
    public RenderHelicopterWrapper(R renderVehicle)
    {
        super(renderVehicle);
    }

    public void render(T entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks)
    {
        if(!entity.isAlive())
            return;

        matrixStack.func_227860_a_();

        VehicleProperties properties = entity.getProperties();
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_((float) bodyPosition.getRotX()));
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_((float) bodyPosition.getRotY()));
        matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_((float) bodyPosition.getRotZ()));

        //Translate the body
        matrixStack.func_227861_a_(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

        //Apply vehicle scale
        matrixStack.func_227862_a_((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.func_227861_a_(0, 0.5, 0);

        //Translate the vehicle so it's axles are half way into the ground
        matrixStack.func_227861_a_(0, properties.getAxleOffset() * 0.0625F, 0);

        //Translate the vehicle so it's actually riding on it's wheels
        matrixStack.func_227861_a_(0, properties.getWheelOffset() * 0.0625F, 0);

        //Render body
        renderVehicle.render(entity, matrixStack, renderTypeBuffer, partialTicks);

        //Render the engine if the vehicle has explicitly stated it should
        if(entity.shouldRenderEngine() && entity.hasEngine())
        {
            this.renderEngine(entity, properties.getEnginePosition(), this.getEngineModel(entity), matrixStack, renderTypeBuffer);
        }

        //Render the fuel port of the vehicle
        if(entity.shouldRenderFuelPort() && entity.requiresFuel())
        {
            EntityRaytracer.RayTraceResultRotated result = EntityRaytracer.getContinuousInteraction();
            if (result != null && result.getType() == RayTraceResult.Type.ENTITY && result.getEntity() == entity && result.equalsContinuousInteraction(EntityRaytracer.FUNCTION_FUELING))
            {
                this.renderPart(properties.getFuelPortPosition(), renderVehicle.getOpenFuelDoorModel().getModel(), matrixStack, renderTypeBuffer, entity.getColor(), 15728880, OverlayTexture.field_229196_a_);
                if(renderVehicle.shouldRenderFuelLid())
                {
                    //this.renderPart(properties.getFuelPortLidPosition(), entity.fuelPortLid);
                }
                entity.playFuelPortOpenSound();
            }
            else
            {
                this.renderPart(properties.getFuelPortPosition(), renderVehicle.getClosedFuelDoorModel().getModel(), matrixStack, renderTypeBuffer, entity.getColor(), 15728880, OverlayTexture.field_229196_a_);
                entity.playFuelPortCloseSound();
            }
        }

        if(entity.isKeyNeeded())
        {
            this.renderPart(properties.getKeyPortPosition(), renderVehicle.getKeyHoleModel().getModel(), matrixStack, renderTypeBuffer, entity.getColor(), 15728880, OverlayTexture.field_229196_a_);
            if(!entity.getKeyStack().isEmpty())
            {
                this.renderKey(properties.getKeyPosition(), RenderUtil.getModel(entity.getKeyStack()), matrixStack, renderTypeBuffer, -1, 15728880, OverlayTexture.field_229196_a_);
            }
        }

        matrixStack.func_227865_b_();
    }

    @Override
    public void applyPreRotations(T entity, MatrixStack matrixStack, float partialTicks)
    {
        matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks));
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks));
    }
}
