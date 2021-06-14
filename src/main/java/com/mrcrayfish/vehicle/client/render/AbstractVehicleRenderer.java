package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
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

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractVehicleRenderer<T extends VehicleEntity & EntityRayTracer.IEntityRayTraceable>
{
    protected final PropertyFunction<T, VehicleProperties> vehiclePropertiesProperty;
    protected final PropertyFunction<T, Boolean> hasDriverProperty = new PropertyFunction<>(t -> t.getControllingPassenger() != null, false);
    protected final PropertyFunction<T, Boolean> towTrailerProperty = new PropertyFunction<>(VehicleEntity::canTowTrailer, false);
    protected final PropertyFunction<T, Integer> colorProperty = new PropertyFunction<>(VehicleEntity::getColor, -1);

    public AbstractVehicleRenderer(VehicleProperties defaultProperties)
    {
        this.vehiclePropertiesProperty = new PropertyFunction<>(VehicleEntity::getProperties, defaultProperties);
    }

    @Nullable
    public abstract EntityRayTracer.IRayTraceTransforms getRayTraceTransforms();

    protected abstract void render(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light);

    public void setupTransformsAndRender(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();

        VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) bodyPosition.getRotX()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) bodyPosition.getRotY()));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) bodyPosition.getRotZ()));

        if(this.towTrailerProperty.get(vehicle))
        {
            matrixStack.pushPose();
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));
            Vector3d towBarOffset = properties.getTowBarPosition();
            matrixStack.translate(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, -towBarOffset.z * 0.0625);
            RenderUtil.renderColoredModel(SpecialModels.TOW_BAR.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }

        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);
        matrixStack.translate(0.0, properties.getWheelOffset() * 0.0625, 0.0);

        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);

        matrixStack.popPose();
    }

    /**
     *
     * @param entity
     * @param partialTicks
     */
    public void applyPreRotations(T entity, MatrixStack stack, float partialTicks) {}

    public void applyPlayerModel(T entity, PlayerEntity player, PlayerModel<AbstractClientPlayerEntity> model, float partialTicks) {}

    public void applyPlayerRender(T entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder) {}

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
    protected void renderPart(PartPosition position, IBakedModel model, MatrixStack matrixStack, IRenderTypeBuffer buffer, int color, int lightTexture, int overlayTexture)
    {
        if(position == null) return;
        matrixStack.pushPose();
        matrixStack.translate(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        matrixStack.translate(0.0, -0.5, 0.0);
        matrixStack.scale((float) position.getScale(), (float) position.getScale(), (float) position.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) position.getRotX()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) position.getRotY()));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) position.getRotZ()));
        RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, matrixStack, buffer, color, lightTexture, overlayTexture);
        matrixStack.popPose();
    }

    protected void renderKey(PartPosition position, ItemStack stack, IBakedModel model, MatrixStack matrixStack, IRenderTypeBuffer buffer, int color, int lightTexture, int overlayTexture)
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
    protected void renderEngine(@Nullable PoweredVehicleEntity entity, @Nullable PartPosition position, IBakedModel model, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light)
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

    public void setHasDriverProperty(boolean hasDriver)
    {
        this.hasDriverProperty.setDefaultValue(hasDriver);
    }

    public void setCanTowTrailer(boolean canTowTrailer)
    {
        this.towTrailerProperty.setDefaultValue(canTowTrailer);
    }

    public void setColor(int color)
    {
        this.colorProperty.setDefaultValue(color);
    }

    protected static class PropertyFunction<V extends VehicleEntity, T>
    {
        protected Function<V, T> function;
        protected T defaultValue;

        public PropertyFunction(Function<V, T> function, T defaultValue)
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
            return vehicle != null ? this.function.apply(vehicle) : this.defaultValue;
        }

        protected void setDefaultValue(T value)
        {
            this.defaultValue = value;
        }
    }
}
