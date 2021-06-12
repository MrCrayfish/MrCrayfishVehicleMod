package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.RayTraceFunction;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class RenderLandVehicleWrapper<T extends LandVehicleEntity & EntityRayTracer.IEntityRayTraceable, R extends AbstractRenderVehicle<T>> extends RenderVehicleWrapper<T, R>
{
    public RenderLandVehicleWrapper(R renderVehicle)
    {
        super(renderVehicle);
    }

    public void render(T entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        if(!entity.isAlive())
            return;

        matrixStack.pushPose();

        VehicleProperties properties = entity.getProperties();
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) bodyPosition.getRotX()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) bodyPosition.getRotY()));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) bodyPosition.getRotZ()));

        float additionalYaw = entity.prevAdditionalYaw + (entity.additionalYaw - entity.prevAdditionalYaw) * partialTicks;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(additionalYaw));

        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

        if(entity.canTowTrailer())
        {
            matrixStack.pushPose();
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));
            Vector3d towBarOffset = properties.getTowBarPosition();
            matrixStack.translate(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, -towBarOffset.z * 0.0625);
            RenderUtil.renderColoredModel(this.renderVehicle.getTowBarModel().getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }

        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);
        matrixStack.translate(0.0, properties.getWheelOffset() * 0.0625, 0.0);

        if(entity.canWheelie())
        {
            if(properties.getRearAxelVec() == null)
            {
                return;
            }
            matrixStack.translate(0.0, -0.5, 0.0);
            matrixStack.translate(0.0, -properties.getAxleOffset() * 0.0625, 0.0);
            matrixStack.translate(0.0, 0.0, properties.getRearAxelVec().z * 0.0625);
            float wheelieProgress = MathHelper.lerp(partialTicks, entity.prevWheelieCount, entity.wheelieCount) / 4F;
            wheelieProgress = (float) (1.0 - Math.pow(1.0 - wheelieProgress, 2));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-30F * wheelieProgress));
            matrixStack.translate(0.0, 0.0, -properties.getRearAxelVec().z * 0.0625);
            matrixStack.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);
            matrixStack.translate(0.0, 0.5, 0.0);
        }

        renderVehicle.render(entity, matrixStack, renderTypeBuffer, partialTicks, light);

        if(entity.hasWheels())
        {
            matrixStack.pushPose();
            matrixStack.translate(0.0, -8 * 0.0625, 0.0);
            matrixStack.translate(0.0, -properties.getAxleOffset() * 0.0625F, 0.0);
            IBakedModel wheelModel = RenderUtil.getWheelModel(entity);
            properties.getWheels().forEach(wheel -> this.renderWheel(entity, wheel, wheelModel, partialTicks, matrixStack, renderTypeBuffer, light));
            matrixStack.popPose();
        }

        //Render the engine if the vehicle has explicitly stated it should
        if(entity.shouldRenderEngine() && entity.hasEngine())
        {
            IBakedModel engineModel = RenderUtil.getEngineModel(entity);
            this.renderEngine(entity, properties.getEnginePosition(), engineModel, matrixStack, renderTypeBuffer, light);
        }

        //Render the fuel port of the vehicle
        if(entity.shouldRenderFuelPort() && entity.requiresFuel())
        {
            PoweredVehicleEntity.FuelPortType fuelPortType = entity.getFuelPortType();
            EntityRayTracer.RayTraceResultRotated result = EntityRayTracer.instance().getContinuousInteraction();
            if(result != null && result.getType() == RayTraceResult.Type.ENTITY && result.getEntity() == entity && result.equalsContinuousInteraction(RayTraceFunction.FUNCTION_FUELING))
            {
                this.renderPart(properties.getFuelPortPosition(), fuelPortType.getOpenModel().getModel(), matrixStack, renderTypeBuffer, entity.getColor(), light, OverlayTexture.NO_OVERLAY);
                if(renderVehicle.shouldRenderFuelLid())
                {
                    //this.renderPart(properties.getFuelPortLidPosition(), entity.fuelPortLid);
                }
                entity.playFuelPortOpenSound();
            }
            else
            {
                this.renderPart(properties.getFuelPortPosition(), fuelPortType.getClosedModel().getModel(), matrixStack, renderTypeBuffer, entity.getColor(), light, OverlayTexture.NO_OVERLAY);
                entity.playFuelPortCloseSound();
            }
        }

        if(entity.isKeyNeeded())
        {
            this.renderPart(properties.getKeyPortPosition(), renderVehicle.getKeyHoleModel().getModel(), matrixStack, renderTypeBuffer, entity.getColor(), light, OverlayTexture.NO_OVERLAY);
            if(!entity.getKeyStack().isEmpty())
            {
                this.renderKey(properties.getKeyPosition(), entity.getKeyStack(), RenderUtil.getModel(entity.getKeyStack()), matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            }
        }

        this.renderSteeringDebug(matrixStack, properties, entity);

        matrixStack.popPose();
    }

    protected void renderSteeringDebug(MatrixStack matrixStack, VehicleProperties properties, T entity)
    {
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
                        Vector3d nextFrontAxelVec = new Vector3d(0, 0, entity.getSpeed() / 20F).yRot(entity.renderWheelAngle * 0.017453292F);
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
                        Vector3d nextFrontAxelVec = new Vector3d(0, 0, entity.getSpeed() / 20F).yRot(entity.renderWheelAngle * 0.017453292F);
                        frontAxelVec = frontAxelVec.add(nextFrontAxelVec);
                        Vector3d rearAxelVec = properties.getRearAxelVec();
                        rearAxelVec = rearAxelVec.scale(0.0625);
                        double deltaYaw = Math.toDegrees(Math.atan2(rearAxelVec.z - frontAxelVec.z, rearAxelVec.x - frontAxelVec.x)) + 90;
                        if(entity.isRearWheelSteering())
                        {
                            deltaYaw += 180;
                        }
                        rearAxelVec = rearAxelVec.add(Vector3d.directionFromRotation(0, (float) deltaYaw).scale(entity.getSpeed() / 20F));
                        matrixStack.translate(rearAxelVec.x, 0, rearAxelVec.z);
                        this.renderSteeringLine(matrixStack, 0xFFDD00);
                    }
                    matrixStack.popPose();

                    matrixStack.pushPose();
                    {
                        Vector3d nextFrontAxelVec = new Vector3d(0, 0, entity.getSpeed() / 20F).yRot(entity.wheelAngle * 0.017453292F);
                        nextFrontAxelVec = nextFrontAxelVec.add(properties.getFrontAxelVec().scale(0.0625));
                        Vector3d nextRearAxelVec = new Vector3d(0, 0, entity.getSpeed() / 20F);
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

    protected void renderWheel(LandVehicleEntity vehicle, Wheel wheel, IBakedModel model, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        if(!wheel.shouldRender())
            return;

        matrixStack.pushPose();
        matrixStack.translate((wheel.getOffsetX() * 0.0625) * wheel.getSide().offset, wheel.getOffsetY() * 0.0625, wheel.getOffsetZ() * 0.0625);
        if(wheel.getPosition() == Wheel.Position.FRONT)
        {
            float wheelAngle = vehicle.prevRenderWheelAngle + (vehicle.renderWheelAngle - vehicle.prevRenderWheelAngle) * partialTicks;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(wheelAngle));
        }
        if(vehicle.isMoving())
        {
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-wheel.getWheelRotation(vehicle, partialTicks)));
        }
        matrixStack.translate((((wheel.getWidth() * wheel.getScaleX()) / 2) * 0.0625) * wheel.getSide().offset, 0.0, 0.0);
        matrixStack.scale(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
        if(wheel.getSide() == Wheel.Side.RIGHT)
        {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));
        }
        RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, vehicle.getWheelColor(), light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }
}
