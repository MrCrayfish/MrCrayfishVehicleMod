package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceTransforms;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.entity.Transform;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.item.IDyeable;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractVehicleRenderer<T extends VehicleEntity>
{
    protected final PropertyFunction<T, VehicleProperties> vehiclePropertiesProperty;
    protected final PropertyFunction<T, Integer> colorProperty = new PropertyFunction<>(VehicleEntity::getColor, -1);
    protected final PropertyFunction<T, Float> bodyYawProperty = new PropertyFunction<>(VehicleEntity::getBodyRotationYaw, 0F);
    protected final PropertyFunction<T, Float> bodyPitchProperty = new PropertyFunction<>(VehicleEntity::getBodyRotationPitch, 0F);
    protected final PropertyFunction<T, Float> bodyRollProperty = new PropertyFunction<>(VehicleEntity::getBodyRotationRoll, 0F);
    protected final PropertyFunction<T, ItemStack> wheelStackProperty = new PropertyFunction<>(VehicleEntity::getWheelStack, ItemStack.EMPTY);
    protected final PropertyFunction<Pair<T, Wheel>, Float> wheelRotationProperty = new PropertyFunction<>((p, f) -> p.getLeft().getWheelRotation(p.getRight(), f), 0F);

    public AbstractVehicleRenderer(VehicleProperties defaultProperties)
    {
        this.vehiclePropertiesProperty = new PropertyFunction<>(VehicleEntity::getProperties, defaultProperties);
    }

    @Nullable
    public abstract RayTraceTransforms getRayTraceTransforms();

    protected abstract void render(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light);

    public void setupTransformsAndRender(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();

        VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
        Transform bodyPosition = properties.getBodyTransform();
        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(bodyPosition.getX() * 0.0625, bodyPosition.getY() * 0.0625, bodyPosition.getZ() * 0.0625);

        if(properties.canTowTrailers())
        {
            matrixStack.pushPose();
            double inverseScale = 1.0 / bodyPosition.getScale();
            matrixStack.scale((float) inverseScale, (float) inverseScale, (float) inverseScale);
            Vector3d towBarOffset = properties.getTowBarOffset().scale(bodyPosition.getScale());
            matrixStack.translate(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, towBarOffset.z * 0.0625);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));
            RenderUtil.renderColoredModel(this.getTowBarModel().getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }

        // Fixes the origin
        matrixStack.translate(0, 0.5, 0);

        // Translate the vehicle so the center of the axles are touching the ground
        matrixStack.translate(0, properties.getAxleOffset() * 0.0625F, 0);

        // Translate the vehicle so it's actually riding on it's wheels
        matrixStack.translate(0, properties.getWheelOffset() * 0.0625F, 0);

        matrixStack.pushPose();
        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) bodyPosition.getRotX()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) bodyPosition.getRotY()));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) bodyPosition.getRotZ()));
        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);
        matrixStack.popPose();

        this.renderWheels(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);

        matrixStack.popPose();
    }

    /**
     *
     * @param entity
     * @param partialTicks
     */
    public void applyPreRotations(T entity, MatrixStack stack, float partialTicks) {}

    public void applyPlayerModel(T entity, PlayerEntity player, PlayerModel<AbstractClientPlayerEntity> model, float partialTicks) {}

    public void applyPlayerRender(T entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vector3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyTransform().getScale()).multiply(-1, 1, 1).scale(0.0625);
            double playerScale = 32.0 / 30.0;
            double offsetX = -seatVec.x * playerScale;
            double offsetY = (seatVec.y + player.getMyRidingOffset()) * playerScale + (24 * 0.0625);
            double offsetZ = seatVec.z * playerScale;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-seat.getYawOffset()));
            matrixStack.translate(offsetX, offsetY, offsetZ);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(entity.getBodyRotationPitch(partialTicks)));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-entity.getBodyRotationRoll(partialTicks)));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(seat.getYawOffset()));
        }
    }

    protected void renderDamagedPart(@Nullable T vehicle, ItemStack part, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        this.renderDamagedPart(vehicle, RenderUtil.getModel(part), matrixStack, renderTypeBuffer, light);
    }

    protected void renderDamagedPart(@Nullable T vehicle, IBakedModel model, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        this.renderDamagedPart(vehicle, model, matrixStack, renderTypeBuffer, false, light);
        this.renderDamagedPart(vehicle, model, matrixStack, renderTypeBuffer, true, light);
    }

    private void renderDamagedPart(@Nullable T vehicle, IBakedModel model, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, boolean renderDamage, int light)
    {
        if(renderDamage && vehicle != null)
        {
            if(vehicle.getDestroyedStage() > 0)
            {
                RenderUtil.renderDamagedVehicleModel(model, ItemCameraTransforms.TransformType.NONE, false, matrixStack, vehicle.getDestroyedStage(), this.colorProperty.get(vehicle), light, OverlayTexture.NO_OVERLAY);
            }
        }
        else
        {
            RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, this.colorProperty.get(vehicle), light, OverlayTexture.NO_OVERLAY);
        }
    }

    /**
     * Renders a part (ItemStack) on the vehicle using the specified PartPosition. The rendering
     * will be cancelled if the PartPosition parameter is null.
     *
     * @param position the render definitions to construct to the part
     * @param model the part to render onto the vehicle
     */
    protected void renderPart(Transform position, IBakedModel model, MatrixStack matrixStack, IRenderTypeBuffer buffer, int color, int lightTexture, int overlayTexture)
    {
        if(position == null) return;
        matrixStack.pushPose();
        matrixStack.translate(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        matrixStack.translate(0.0, -0.5, 0.0);
        matrixStack.scale((float) position.getScale(), (float) position.getScale(), (float) position.getScale());
        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) position.getRotX()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) position.getRotY()));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) position.getRotZ()));
        RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, matrixStack, buffer, color, lightTexture, overlayTexture);
        matrixStack.popPose();
    }

    protected void renderKey(Transform position, ItemStack stack, IBakedModel model, MatrixStack matrixStack, IRenderTypeBuffer buffer, int color, int lightTexture, int overlayTexture)
    {
        if(position == null) return;
        matrixStack.pushPose();
        matrixStack.translate(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        matrixStack.translate(0.0, -0.25, 0.0);
        matrixStack.scale((float) position.getScale(), (float) position.getScale(), (float) position.getScale());
        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) position.getRotX()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) position.getRotY()));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) position.getRotZ()));
        matrixStack.translate(0.0, 0.0, -0.05);
        RenderUtil.renderModel(stack, ItemCameraTransforms.TransformType.NONE, false, matrixStack, buffer, lightTexture, overlayTexture, model);
        matrixStack.popPose();
    }


    /**
     * Renders the engine (ItemStack) on the vehicle using the specified PartPosition. It adds a
     * subtle shake to the render to simulate it being powered.
     *
     * @param position the render definitions to construct to the part
     */
    protected void renderEngine(@Nullable PoweredVehicleEntity entity, @Nullable Transform position, IBakedModel model, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light)
    {
        matrixStack.pushPose();
        if(entity != null && entity.isEnginePowered() && entity.getControllingPassenger() != null)
        {
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(0.5F * (entity.tickCount % 2)));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(0.5F * (entity.tickCount % 2)));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-0.5F * (entity.tickCount % 2)));
        }
        this.renderPart(position, model, matrixStack, buffer, -1, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }

    protected void renderWheels(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        ItemStack wheelStack = this.wheelStackProperty.get(vehicle);
        if(!wheelStack.isEmpty())
        {
            VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
            matrixStack.pushPose();
            matrixStack.translate(0.0, -8 * 0.0625, 0.0);
            matrixStack.translate(0.0, -properties.getAxleOffset() * 0.0625F, 0.0);
            IBakedModel wheelModel = RenderUtil.getModel(wheelStack);
            properties.getWheels().forEach(wheel -> this.renderWheel(vehicle, wheel, wheelStack, wheelModel, partialTicks, matrixStack, renderTypeBuffer, light));
            matrixStack.popPose();
        }
    }

    protected void renderWheel(@Nullable T vehicle, Wheel wheel, ItemStack stack, IBakedModel model, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        if(!wheel.shouldRender())
            return;

        matrixStack.pushPose();
        matrixStack.translate((wheel.getOffsetX() * 0.0625) * wheel.getSide().getOffset(), wheel.getOffsetY() * 0.0625, wheel.getOffsetZ() * 0.0625);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-this.getWheelRotation(vehicle, wheel, partialTicks)));
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

    protected ISpecialModel getKeyHoleModel()
    {
        return SpecialModels.KEY_HOLE;
    }

    protected ISpecialModel getTowBarModel()
    {
        return SpecialModels.TOW_BAR;
    }

    protected boolean shouldRenderFuelLid()
    {
        return true;
    }

    public void setVehicleProperties(VehicleProperties properties)
    {
        this.vehiclePropertiesProperty.setDefaultValue(properties);
    }

    public void setColor(int color)
    {
        this.colorProperty.setDefaultValue(color);
    }

    public void setBodyYaw(float yaw)
    {
        this.bodyYawProperty.setDefaultValue(yaw);
    }

    public void setBodyPitch(float pitch)
    {
        this.bodyPitchProperty.setDefaultValue(pitch);
    }

    public void setBodyRoll(float roll)
    {
        this.bodyRollProperty.setDefaultValue(roll);
    }

    public void setWheelStack(ItemStack wheel)
    {
        this.wheelStackProperty.setDefaultValue(wheel);
    }

    public void setWheelRotation(float rotation)
    {
        this.wheelRotationProperty.setDefaultValue(rotation);
    }

    public float getWheelRotation(@Nullable T vehicle, @Nullable Wheel wheel, float partialTicks)
    {
        if(vehicle != null)
        {
            return this.wheelRotationProperty.get(Pair.of(vehicle, wheel), partialTicks);
        }
        return this.wheelRotationProperty.get();
    }

    protected static class PropertyFunction<V, T>
    {
        protected BiFunction<V, Float, T> function;
        protected T defaultValue;

        public PropertyFunction(Function<V, T> function, T defaultValue)
        {
            this((v, p) -> function.apply(v), defaultValue);
        }

        public PropertyFunction(BiFunction<V, Float, T> function, T defaultValue)
        {
            this.function = function;
            this.defaultValue = defaultValue;
        }

        public T get()
        {
            return this.get(null);
        }

        public T get(@Nullable V vehicle)
        {
            return this.get(vehicle, 0F);
        }

        public T get(@Nullable V vehicle, float partialTicks)
        {
            return vehicle != null ? this.function.apply(vehicle, partialTicks) : this.defaultValue;
        }

        protected void setDefaultValue(T value)
        {
            this.defaultValue = value;
        }
    }
}
