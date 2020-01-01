package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class RenderLandVehicleWrapper<T extends LandVehicleEntity & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>> extends RenderVehicleWrapper<T, R>
{
    public RenderLandVehicleWrapper(R renderVehicle)
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

        float additionalYaw = entity.prevAdditionalYaw + (entity.additionalYaw - entity.prevAdditionalYaw) * partialTicks;
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(additionalYaw));

        matrixStack.func_227861_a_(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

        if(entity.canTowTrailer())
        {
            matrixStack.func_227860_a_();
            matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180F));
            Vec3d towBarOffset = properties.getTowBarPosition();
            matrixStack.func_227861_a_(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, -towBarOffset.z * 0.0625);
            RenderUtil.renderColoredModel(SpecialModel.TOW_BAR.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, 15728880, OverlayTexture.field_229196_a_);
            matrixStack.func_227865_b_();
        }

        matrixStack.func_227862_a_((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.func_227861_a_(0.0, 0.5, 0.0);
        matrixStack.func_227861_a_(0.0, properties.getAxleOffset() * 0.0625, 0.0);
        matrixStack.func_227861_a_(0.0, properties.getWheelOffset() * 0.0625, 0.0);
        renderVehicle.render(entity, matrixStack, renderTypeBuffer, partialTicks);

        if(entity.hasWheels())
        {
            matrixStack.func_227860_a_();
            matrixStack.func_227861_a_(0.0, -8 * 0.0625, 0.0);
            matrixStack.func_227861_a_(0.0, -properties.getAxleOffset() * 0.0625F, 0.0);
            IBakedModel wheelModel = this.getWheelModel(entity);
            properties.getWheels().forEach(wheel -> this.renderWheel(entity, wheel, wheelModel, partialTicks, matrixStack, renderTypeBuffer));
            matrixStack.func_227865_b_();
        }

        //Render the engine if the vehicle has explicitly stated it should
        if(entity.shouldRenderEngine() && entity.hasEngine())
        {
            IBakedModel engineModel = this.getEngineModel(entity);
            this.renderEngine(entity, properties.getEnginePosition(), engineModel, matrixStack, renderTypeBuffer);
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

    protected void renderWheel(LandVehicleEntity vehicle, Wheel wheel, IBakedModel model, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer)
    {
        if(!wheel.shouldRender())
            return;

        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_((wheel.getOffsetX() * 0.0625) * wheel.getSide().offset, wheel.getOffsetY() * 0.0625, wheel.getOffsetZ() * 0.0625);
        if(wheel.getPosition() == Wheel.Position.FRONT)
        {
            float wheelAngle = vehicle.prevRenderWheelAngle + (vehicle.renderWheelAngle - vehicle.prevRenderWheelAngle) * partialTicks;
            matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(wheelAngle / 2.0F));
        }
        if(vehicle.isMoving())
        {
            matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-wheel.getWheelRotation(vehicle, partialTicks)));
        }
        matrixStack.func_227861_a_((((wheel.getWidth() * wheel.getScaleX()) / 2) * 0.0625) * wheel.getSide().offset, 0.0, 0.0);
        matrixStack.func_227862_a_(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
        if(wheel.getSide() == Wheel.Side.RIGHT)
        {
            matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180F));
        }
        RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, vehicle.getWheelColor(), 15728880, OverlayTexture.field_229196_a_);
        matrixStack.func_227865_b_();
    }
}
