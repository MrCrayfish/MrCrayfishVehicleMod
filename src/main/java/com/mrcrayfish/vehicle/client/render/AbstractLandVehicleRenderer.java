package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.item.IDyeable;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractLandVehicleRenderer<T extends LandVehicleEntity & EntityRayTracer.IEntityRayTraceable> extends AbstractPoweredRenderer<T>
{
    protected final PropertyFunction<T, Float> wheelAngleProperty = new PropertyFunction<>(t -> t.wheelAngle, 0F);
    protected final PropertyFunction<T, Float> prevWheelAngleProperty = new PropertyFunction<>(t -> t.prevWheelAngle, 0F);

    public AbstractLandVehicleRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    public void setupTransformsAndRender(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();

        VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) bodyPosition.getRotX()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) bodyPosition.getRotY()));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) bodyPosition.getRotZ()));

        if(vehicle != null)
        {
            float additionalYaw = vehicle.prevAdditionalYaw + (vehicle.additionalYaw - vehicle.prevAdditionalYaw) * partialTicks;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(additionalYaw));
        }

        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

        if(this.towTrailerProperty.get(vehicle))
        {
            matrixStack.pushPose();
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));
            Vector3d towBarOffset = properties.getTowBarPosition();
            matrixStack.translate(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, -towBarOffset.z * 0.0625);
            RenderUtil.renderColoredModel(this.getTowBarModel().getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }

        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);
        matrixStack.translate(0.0, properties.getWheelOffset() * 0.0625, 0.0);

        if(vehicle != null && vehicle.canWheelie())
        {
            if(properties.getRearAxelVec() == null)
            {
                return;
            }
            matrixStack.translate(0.0, -0.5, 0.0);
            matrixStack.translate(0.0, -properties.getAxleOffset() * 0.0625, 0.0);
            matrixStack.translate(0.0, 0.0, properties.getRearAxelVec().z * 0.0625);
            float wheelieProgress = MathHelper.lerp(partialTicks, vehicle.prevWheelieCount, vehicle.wheelieCount) / 4F;
            wheelieProgress = (float) (1.0 - Math.pow(1.0 - wheelieProgress, 2));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-30F * wheelieProgress));
            matrixStack.translate(0.0, 0.0, -properties.getRearAxelVec().z * 0.0625);
            matrixStack.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);
            matrixStack.translate(0.0, 0.5, 0.0);
        }

        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);

        ItemStack wheelStack = this.wheelStackProperty.get(vehicle);
        if(!wheelStack.isEmpty())
        {
            matrixStack.pushPose();
            matrixStack.translate(0.0, -8 * 0.0625, 0.0);
            matrixStack.translate(0.0, -properties.getAxleOffset() * 0.0625F, 0.0);
            IBakedModel wheelModel = RenderUtil.getModel(wheelStack);
            properties.getWheels().forEach(wheel -> this.renderWheel(vehicle, wheel, wheelStack, wheelModel, partialTicks, matrixStack, renderTypeBuffer, light));
            matrixStack.popPose();
        }

        this.renderEngine(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderFuelPort(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderKeyPort(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderSteeringDebug(matrixStack, properties, vehicle);

        matrixStack.popPose();
    }

    protected void renderSteeringDebug(MatrixStack matrixStack, VehicleProperties properties, @Nullable T vehicle)
    {
        if(vehicle == null)
            return;

        if(Config.CLIENT.renderSteeringDebug.get())
        {
            if(properties.getFrontAxelVec() != null && properties.getRearAxelVec() != null)
            {
                matrixStack.pushPose();
                {
                    matrixStack.translate(0.0, -0.5, 0.0);
                    matrixStack.translate(0.0, -properties.getAxleOffset() * 0.0625, 0.0);
                    matrixStack.translate(0.0, -properties.getWheelOffset() * 0.0625, 0.0);

                    matrixStack.pushPose();
                    {
                        Vector3d frontAxelVec = properties.getFrontAxelVec();
                        frontAxelVec = frontAxelVec.scale(0.0625);
                        matrixStack.translate(frontAxelVec.x, 0, frontAxelVec.z);
                        this.renderSteeringLine(matrixStack, 0xFFFFFF);
                    }
                    matrixStack.popPose();

                    matrixStack.pushPose();
                    {
                        Vector3d frontAxelVec = properties.getFrontAxelVec();
                        frontAxelVec = frontAxelVec.scale(0.0625);
                        Vector3d nextFrontAxelVec = new Vector3d(0, 0, vehicle.getSpeed() / 20F).yRot(vehicle.renderWheelAngle * 0.017453292F);
                        frontAxelVec = frontAxelVec.add(nextFrontAxelVec);
                        matrixStack.translate(frontAxelVec.x, 0, frontAxelVec.z);
                        this.renderSteeringLine(matrixStack, 0xFFDD00);
                    }
                    matrixStack.popPose();

                    matrixStack.pushPose();
                    {
                        Vector3d rearAxelVec = properties.getRearAxelVec();
                        rearAxelVec = rearAxelVec.scale(0.0625);
                        matrixStack.translate(rearAxelVec.x, 0, rearAxelVec.z);
                        this.renderSteeringLine(matrixStack, 0xFFFFFF);
                    }
                    matrixStack.popPose();

                    matrixStack.pushPose();
                    {
                        Vector3d frontAxelVec = properties.getFrontAxelVec();
                        frontAxelVec = frontAxelVec.scale(0.0625);
                        Vector3d nextFrontAxelVec = new Vector3d(0, 0, vehicle.getSpeed() / 20F).yRot(vehicle.renderWheelAngle * 0.017453292F);
                        frontAxelVec = frontAxelVec.add(nextFrontAxelVec);
                        Vector3d rearAxelVec = properties.getRearAxelVec();
                        rearAxelVec = rearAxelVec.scale(0.0625);
                        double deltaYaw = Math.toDegrees(Math.atan2(rearAxelVec.z - frontAxelVec.z, rearAxelVec.x - frontAxelVec.x)) + 90;
                        if(vehicle.isRearWheelSteering())
                        {
                            deltaYaw += 180;
                        }
                        rearAxelVec = rearAxelVec.add(Vector3d.directionFromRotation(0, (float) deltaYaw).scale(vehicle.getSpeed() / 20F));
                        matrixStack.translate(rearAxelVec.x, 0, rearAxelVec.z);
                        this.renderSteeringLine(matrixStack, 0xFFDD00);
                    }
                    matrixStack.popPose();

                    matrixStack.pushPose();
                    {
                        Vector3d nextFrontAxelVec = new Vector3d(0, 0, vehicle.getSpeed() / 20F).yRot(vehicle.wheelAngle * 0.017453292F);
                        nextFrontAxelVec = nextFrontAxelVec.add(properties.getFrontAxelVec().scale(0.0625));
                        Vector3d nextRearAxelVec = new Vector3d(0, 0, vehicle.getSpeed() / 20F);
                        nextRearAxelVec = nextRearAxelVec.add(properties.getRearAxelVec().scale(0.0625));
                        Vector3d nextVehicleVec = nextFrontAxelVec.add(nextRearAxelVec).scale(0.5);
                        nextVehicleVec = nextVehicleVec.subtract(properties.getFrontAxelVec().add(properties.getRearAxelVec()).scale(0.0625).scale(0.5));
                        matrixStack.pushPose();
                        {
                            this.renderSteeringLine(matrixStack, 0xFFFFFF);
                        }
                        matrixStack.popPose();
                        matrixStack.pushPose();
                        {
                            matrixStack.translate(nextVehicleVec.x, 0, nextVehicleVec.z);
                            this.renderSteeringLine(matrixStack, 0xFFDD00);
                        }
                        matrixStack.popPose();
                    }
                    matrixStack.popPose();
                }
                matrixStack.popPose();
            }
        }
    }

    private void renderSteeringLine(MatrixStack stack, int color)
    {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(Math.max(2.0F, (float) Minecraft.getInstance().getWindow().getWidth() / 1920.0F * 2.0F));
        RenderSystem.enableDepthTest();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(stack.last().pose(), 0, 0, 0).color(red, green, blue, 1.0F).endVertex();
        buffer.vertex(stack.last().pose(), 0, 2, 0).color(red, green, blue, 1.0F).endVertex();
        tessellator.end();
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
    }

    protected void renderWheel(@Nullable T vehicle, Wheel wheel, ItemStack stack, IBakedModel model, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        if(!wheel.shouldRender())
            return;

        matrixStack.pushPose();
        matrixStack.translate((wheel.getOffsetX() * 0.0625) * wheel.getSide().getOffset(), wheel.getOffsetY() * 0.0625, wheel.getOffsetZ() * 0.0625);
        if(wheel.getPosition() == Wheel.Position.FRONT)
        {
            float wheelAngle = MathHelper.lerp(partialTicks, this.prevWheelAngleProperty.get(vehicle), this.wheelAngleProperty.get(vehicle));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(wheelAngle));
        }
        if(vehicle != null && vehicle.isMoving())
        {
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-wheel.getWheelRotation(vehicle, partialTicks)));
        }
        matrixStack.translate((((wheel.getWidth() * wheel.getScaleX()) / 2) * 0.0625) * wheel.getSide().getOffset(), 0.0, 0.0);
        matrixStack.scale(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
        if(wheel.getSide() == Wheel.Side.RIGHT)
        {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));
        }
        int wheelColor = IDyeable.getColorFromStack(stack);
        RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, wheelColor, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }
}
