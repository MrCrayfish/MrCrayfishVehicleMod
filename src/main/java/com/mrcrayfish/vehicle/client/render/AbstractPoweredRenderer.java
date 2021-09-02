package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.RayTraceFunction;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.entity.properties.PoweredProperties;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.item.IDyeable;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3f;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractPoweredRenderer<T extends PoweredVehicleEntity & EntityRayTracer.IEntityRayTraceable> extends AbstractVehicleRenderer<T>
{
    protected final PropertyFunction<T, ItemStack> engineStackProperty = new PropertyFunction<>(PoweredVehicleEntity::getEngineStack, ItemStack.EMPTY);
    protected final PropertyFunction<T, Boolean> renderFuelPortProperty = new PropertyFunction<>(PoweredVehicleEntity::shouldRenderFuelPort, true);
    protected final PropertyFunction<T, Float> wheelAngleProperty = new PropertyFunction<>(PoweredVehicleEntity::getRenderWheelAngle, 0F);

    public AbstractPoweredRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    protected void renderEngine(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
        if(properties.getExtended(PoweredProperties.class).isRenderEngine() && !this.engineStackProperty.get(vehicle).isEmpty())
        {
            ItemStack engine = this.engineStackProperty.get(vehicle);
            if(!engine.isEmpty())
            {
                IBakedModel engineModel = RenderUtil.getModel(this.engineStackProperty.get(vehicle));
                this.renderEngine(vehicle, properties.getExtended(PoweredProperties.class).getEngineTransform(), engineModel, matrixStack, renderTypeBuffer, light);
            }
        }
    }

    protected void renderFuelPort(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        if(vehicle != null && vehicle.shouldRenderFuelPort() && vehicle.requiresEnergy())
        {
            VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
            PoweredVehicleEntity.FuelPortType fuelPortType = vehicle.getFuelPortType();
            EntityRayTracer.RayTraceResultRotated result = EntityRayTracer.instance().getContinuousInteraction();
            if(result != null && result.getType() == RayTraceResult.Type.ENTITY && result.getEntity() == vehicle && result.equalsContinuousInteraction(RayTraceFunction.FUNCTION_FUELING))
            {
                this.renderPart(properties.getExtended(PoweredProperties.class).getFuelFillerTransform(), fuelPortType.getOpenModel().getModel(), matrixStack, renderTypeBuffer, vehicle.getColor(), light, OverlayTexture.NO_OVERLAY);
                if(this.shouldRenderFuelLid())
                {
                    //this.renderPart(properties.getFuelPortLidPosition(), entity.fuelPortLid);
                }
                vehicle.playFuelPortOpenSound();
            }
            else
            {
                this.renderPart(properties.getExtended(PoweredProperties.class).getFuelFillerTransform(), fuelPortType.getClosedModel().getModel(), matrixStack, renderTypeBuffer, vehicle.getColor(), light, OverlayTexture.NO_OVERLAY);
                vehicle.playFuelPortCloseSound();
            }
        }
    }

    protected void renderKeyPort(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        if(vehicle != null && vehicle.isKeyNeeded())
        {
            VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
            this.renderPart(properties.getExtended(PoweredProperties.class).getIgnitionTransform(), this.getKeyHoleModel().getModel(), matrixStack, renderTypeBuffer, vehicle.getColor(), light, OverlayTexture.NO_OVERLAY);
            if(!vehicle.getKeyStack().isEmpty())
            {
                this.renderKey(properties.getExtended(PoweredProperties.class).getIgnitionTransform(), vehicle.getKeyStack(), RenderUtil.getModel(vehicle.getKeyStack()), matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            }
        }
    }

    @Override
    protected void renderWheel(@Nullable T vehicle, Wheel wheel, ItemStack stack, IBakedModel model, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        if(!wheel.shouldRender())
            return;

        matrixStack.pushPose();
        matrixStack.translate((wheel.getOffsetX() * 0.0625) * wheel.getSide().getOffset(), wheel.getOffsetY() * 0.0625, wheel.getOffsetZ() * 0.0625);
        if(wheel.getPosition() == Wheel.Position.FRONT)
        {
            float wheelAngle = this.wheelAngleProperty.get(vehicle, partialTicks);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(wheelAngle));
        }
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-this.wheelRotationProperty.get(Pair.of(vehicle, wheel), partialTicks)));
        if(wheel.getSide() != Wheel.Side.NONE)
        {
            matrixStack.translate((((wheel.getWidth() * wheel.getScaleX()) / 2) * 0.0625) * wheel.getSide().getOffset(), 0.0, 0.0);
        }
        matrixStack.scale(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
        if(wheel.getSide() == Wheel.Side.RIGHT)
        {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));
        }
        int wheelColor = IDyeable.getColorFromStack(stack);
        RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, wheelColor, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }

    public void setEngineStack(ItemStack engine)
    {
        this.engineStackProperty.setDefaultValue(engine);
    }

    public void setRenderFuelPort(boolean renderFuelPort)
    {
        this.renderFuelPortProperty.setDefaultValue(renderFuelPort);
    }
}
