package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractMotorcycleRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.MopedEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.item.IDyeable;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.Calendar;

/**
 * Author: MrCrayfish
 */
public class MopedRenderer extends AbstractMotorcycleRenderer<MopedEntity>
{
    private final ModelRenderer lid;
    private final ModelRenderer base;
    private final ModelRenderer lock;
    public final boolean isChristmas;

    protected final PropertyFunction<MopedEntity, Boolean> hasChestProperty = new PropertyFunction<>(MopedEntity::hasChest, false);
    protected final PropertyFunction<MopedEntity, Float> openProgressProperty = new PropertyFunction<>(MopedEntity::getOpenProgress, 0F);
    protected final PropertyFunction<MopedEntity, Float> prevOpenProgressProperty = new PropertyFunction<>(MopedEntity::getPrevOpenProgress, 0F);

    public MopedRenderer(VehicleProperties properties)
    {
        super(properties);
        Calendar calendar = Calendar.getInstance();
        this.isChristmas = calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26;
        this.base = new ModelRenderer(64, 64, 0, 19);
        this.base.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
        this.lid = new ModelRenderer(64, 64, 0, 0);
        this.lid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
        this.lid.y = 9.0F;
        this.lid.z = 1.0f;
        this.lock = new ModelRenderer(64, 64, 0, 0);
        this.lock.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
        this.lock.y = 8.0F;
    }

    @Override
    public void render(@Nullable MopedEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.MOPED_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.pushPose();

        matrixStack.translate(0.0, 0.0, 11.5 * 0.0625);
        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-22.5F));
        if(vehicle != null)
        {
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 25F;
            matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(turnRotation));
        }
        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(22.5F));
        matrixStack.translate(0.0, 0.0, -11.5 * 0.0625);

        //Render handles bars
        matrixStack.pushPose();
        matrixStack.translate(0, (12.2739 - 8) * 0.0625, (16.4071 - 8) * 0.0625);
        this.renderDamagedPart(vehicle, SpecialModels.MOPED_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();

        //Render front bar and mud guard
        matrixStack.pushPose();
        {
            matrixStack.translate(0, (4.1044 - 8) * 0.0625, (19.8181 - 8) * 0.0625);
            this.renderDamagedPart(vehicle, SpecialModels.MOPED_MUD_GUARD.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.popPose();

        //Render front wheel
        ItemStack wheelStack = this.wheelStackProperty.get(vehicle);
        if(!wheelStack.isEmpty())
        {
            matrixStack.pushPose();
            VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
            Wheel wheel = properties.getFirstFrontWheel();
            if(wheel != null)
            {
                matrixStack.translate(0.0, -8 * 0.0625, 0.0);
                matrixStack.translate(0.0, -properties.getAxleOffset() * 0.0625F, 0.0);
                matrixStack.translate(wheel.getOffsetX() * 0.0625, wheel.getOffsetY() * 0.0625, wheel.getOffsetZ() * 0.0625);
                if(vehicle != null)
                {
                    float frontWheelSpin = MathHelper.lerp(partialTicks, vehicle.prevFrontWheelRotation, vehicle.frontWheelRotation);
                    if(vehicle.isMoving())
                    {
                        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-frontWheelSpin));
                    }
                }
                matrixStack.scale(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
                IBakedModel wheelModel = RenderUtil.getModel(wheelStack);
                int wheelColor = IDyeable.getColorFromStack(wheelStack);
                RenderUtil.renderColoredModel(wheelModel, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, wheelColor, light, OverlayTexture.NO_OVERLAY);
            }
            matrixStack.popPose();
        }

        matrixStack.popPose();

        if(this.hasChestProperty.get(vehicle))
        {
            matrixStack.pushPose();
            matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(180F));
            matrixStack.translate(0, 0, 6.5 * 0.0625F);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            matrixStack.translate(-0.5, 0, 0);
            float progress = MathHelper.lerp(partialTicks, this.prevOpenProgressProperty.get(vehicle), this.openProgressProperty.get(vehicle));
            progress = 1.0F - progress;
            progress = 1.0F - progress * progress * progress;
            RenderMaterial renderMaterial = this.isChristmas ? Atlases.CHEST_XMAS_LOCATION : Atlases.CHEST_LOCATION;
            IVertexBuilder builder = renderMaterial.buffer(renderTypeBuffer, RenderType::entityCutout);
            this.renderChest(matrixStack, builder, this.lid, this.lock, this.base, progress, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }
    }

    @Override
    public void applyPlayerModel(MopedEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;
        model.rightArm.xRot = (float) Math.toRadians(-65F - turnRotation);
        model.rightArm.yRot = (float) Math.toRadians(5F);
        model.rightArm.z -= 1;
        model.rightArm.z -= wheelAngleNormal * 2;
        model.leftArm.xRot = (float) Math.toRadians(-65F + turnRotation);
        model.leftArm.yRot = (float) Math.toRadians(-5F);
        model.leftArm.z -= 1;
        model.leftArm.z += wheelAngleNormal * 2;
        model.rightLeg.xRot = (float) Math.toRadians(-62F);
        model.leftLeg.xRot = (float) Math.toRadians(-62F);
    }

    @Override
    public void applyPlayerRender(MopedEntity entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vector3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).multiply(-1, 1, 1).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y + player.getMyRidingOffset()) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = seatVec.z * scale;
            matrixStack.translate(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            matrixStack.mulPose(Axis.POSITIVE_Z.rotationDegrees(turnAngleNormal * currentSpeedNormal * 20F));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
        }
    }

    private void renderChest(MatrixStack matrixStack, IVertexBuilder builder, ModelRenderer lid, ModelRenderer lock, ModelRenderer base, float openProgress, int lightTexture, int overlayTexture)
    {
        lid.xRot = -(openProgress * ((float) Math.PI / 2F));
        lock.xRot = lid.xRot;
        lid.render(matrixStack, builder, lightTexture, overlayTexture);
        lock.render(matrixStack, builder, lightTexture, overlayTexture);
        base.render(matrixStack, builder, lightTexture, overlayTexture);
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.MOPED_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.MOPED_HANDLES, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.0625F, 0.0F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.835F, 0.525F),
                    EntityRayTracer.MatrixTransformation.createScale(0.8F));
            EntityRayTracer.createTransformListForPart(SpecialModels.MOPED_MUD_GUARD, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.0625F, 0.0F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.12F, 0.785F),
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -22.5F),
                    EntityRayTracer.MatrixTransformation.createScale(0.9F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.MOPED.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}
