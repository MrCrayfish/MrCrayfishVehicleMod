package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class RenderLandVehicleWrapper<T extends LandVehicleEntity & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>> extends RenderVehicleWrapper<T, R>
{
    public RenderLandVehicleWrapper(R renderVehicle)
    {
        super(renderVehicle);
    }

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

        float additionalYaw = entity.prevAdditionalYaw + (entity.additionalYaw - entity.prevAdditionalYaw) * partialTicks;
        GlStateManager.rotatef(additionalYaw, 0, 1, 0);

        GlStateManager.translated(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

        if(entity.canTowTrailer())
        {
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(180F, 0, 1, 0);
            Vec3d towBarOffset = properties.getTowBarPosition();
            GlStateManager.translated(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, -towBarOffset.z * 0.0625);
            RenderUtil.renderColoredModel(this.renderVehicle.getTowBarModel().getModel(), ItemCameraTransforms.TransformType.NONE, false, -1);
            GlStateManager.popMatrix();
        }

        GlStateManager.scalef((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        GlStateManager.translated(0.0, 0.5, 0.0);
        GlStateManager.translated(0.0, properties.getAxleOffset() * 0.0625, 0.0);
        GlStateManager.translated(0.0, properties.getWheelOffset() * 0.0625, 0.0);

        if(entity.canWheelie())
        {
            if(properties.getRearAxelVec() == null)
            {
                return;
            }
            GlStateManager.translated(0.0, -0.5, 0.0);
            GlStateManager.translated(0.0, -properties.getAxleOffset() * 0.0625, 0.0);
            GlStateManager.translated(0.0, 0.0, properties.getRearAxelVec().z * 0.0625);
            float wheelieProgress = MathHelper.lerp(partialTicks, entity.prevWheelieCount, entity.wheelieCount) / 4F;
            wheelieProgress = (float) (1.0 - Math.pow(1.0 - wheelieProgress, 2));
            GlStateManager.rotatef(-30F * wheelieProgress, 1, 0, 0); //TODO test
            GlStateManager.translated(0.0, 0.0, -properties.getRearAxelVec().z * 0.0625);
            GlStateManager.translated(0.0, properties.getAxleOffset() * 0.0625, 0.0);
            GlStateManager.translated(0.0, 0.5, 0.0);
        }

        renderVehicle.render(entity, partialTicks);

        if(entity.hasWheels())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translated(0.0, -8 * 0.0625, 0.0);
            GlStateManager.translated(0.0, -properties.getAxleOffset() * 0.0625F, 0.0);
            IBakedModel wheelModel = RenderUtil.getWheelModel(entity);
            properties.getWheels().forEach(wheel -> this.renderWheel(entity, wheel, wheelModel, partialTicks));
            GlStateManager.popMatrix();
        }

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
                this.renderKey(properties.getKeyPosition(), RenderUtil.getModel(entity.getKeyStack()), entity.getKeyStack());
            }
        }

        this.renderSteeringDebug(properties, entity);

        GlStateManager.popMatrix();
    }

    private void renderSteeringDebug(VehicleProperties properties, LandVehicleEntity entity)
    {
        if(Config.CLIENT.renderSteeringDebug.get())
        {
            if(properties.getFrontAxelVec() != null && properties.getRearAxelVec() != null)
            {
                GlStateManager.pushMatrix();
                {
                    GlStateManager.translated(0.0, -0.5, 0.0);
                    GlStateManager.translated(0.0, -properties.getAxleOffset() * 0.0625, 0.0);
                    GlStateManager.translated(0.0, -properties.getWheelOffset() * 0.0625, 0.0);

                    GlStateManager.pushMatrix();
                    {
                        Vec3d frontAxelVec = properties.getFrontAxelVec();
                        frontAxelVec = frontAxelVec.scale(0.0625);
                        GlStateManager.translated(frontAxelVec.x, 0, frontAxelVec.z);
                        this.renderSteeringLine(0xFFFFFF);
                    }
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    {
                        Vec3d frontAxelVec = properties.getFrontAxelVec();
                        frontAxelVec = frontAxelVec.scale(0.0625);
                        Vec3d nextFrontAxelVec = new Vec3d(0, 0, entity.getSpeed() / 20F).rotateYaw(entity.renderWheelAngle * 0.017453292F);
                        frontAxelVec = frontAxelVec.add(nextFrontAxelVec);
                        GlStateManager.translated(frontAxelVec.x, 0, frontAxelVec.z);
                        this.renderSteeringLine(0xFFDD00);
                    }
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    {
                        Vec3d rearAxelVec = properties.getRearAxelVec();
                        rearAxelVec = rearAxelVec.scale(0.0625);
                        GlStateManager.translated(rearAxelVec.x, 0, rearAxelVec.z);
                        this.renderSteeringLine(0xFFFFFF);
                    }
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    {
                        Vec3d frontAxelVec = properties.getFrontAxelVec();
                        frontAxelVec = frontAxelVec.scale(0.0625);
                        Vec3d nextFrontAxelVec = new Vec3d(0, 0, entity.getSpeed() / 20F).rotateYaw(entity.renderWheelAngle * 0.017453292F);
                        frontAxelVec = frontAxelVec.add(nextFrontAxelVec);
                        Vec3d rearAxelVec = properties.getRearAxelVec();
                        rearAxelVec = rearAxelVec.scale(0.0625);
                        double deltaYaw = Math.toDegrees(Math.atan2(rearAxelVec.z - frontAxelVec.z, rearAxelVec.x - frontAxelVec.x)) + 90;
                        if(entity.isRearWheelSteering())
                        {
                            deltaYaw += 180;
                        }
                        rearAxelVec = rearAxelVec.add(Vec3d.fromPitchYaw(0, (float) deltaYaw).scale(entity.getSpeed() / 20F));
                        GlStateManager.translated(rearAxelVec.x, 0, rearAxelVec.z);
                        this.renderSteeringLine(0xFFDD00);
                    }
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    {
                        Vec3d nextFrontAxelVec = new Vec3d(0, 0, entity.getSpeed() / 20F).rotateYaw(entity.wheelAngle * 0.017453292F);
                        nextFrontAxelVec = nextFrontAxelVec.add(properties.getFrontAxelVec().scale(0.0625));
                        Vec3d nextRearAxelVec = new Vec3d(0, 0, entity.getSpeed() / 20F);
                        nextRearAxelVec = nextRearAxelVec.add(properties.getRearAxelVec().scale(0.0625));
                        Vec3d nextVehicleVec = nextFrontAxelVec.add(nextRearAxelVec).scale(0.5);
                        nextVehicleVec = nextVehicleVec.subtract(properties.getFrontAxelVec().add(properties.getRearAxelVec()).scale(0.0625).scale(0.5));
                        GlStateManager.pushMatrix();
                        {
                            this.renderSteeringLine(0xFFFFFF);
                        }
                        GlStateManager.popMatrix();
                        GlStateManager.pushMatrix();
                        {
                            GlStateManager.translated(nextVehicleVec.x, 0, nextVehicleVec.z);
                            this.renderSteeringLine(0xFFDD00);
                        }
                        GlStateManager.popMatrix();
                    }
                    GlStateManager.popMatrix();
                }
                GlStateManager.popMatrix();
            }
        }
    }

    private void renderSteeringLine(int color)
    {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        GlStateManager.disableTexture();
        GlStateManager.lineWidth(Math.max(2.0F, (float) Minecraft.getInstance().mainWindow.getFramebufferWidth() / 1920.0F * 2.0F));
        GlStateManager.disableLighting();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0, 0, 0).color(red, green, blue, 1.0F).endVertex();
        buffer.pos(0, 2, 0).color(red, green, blue, 1.0F).endVertex();
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
    }

    protected void renderWheel(LandVehicleEntity vehicle, Wheel wheel, IBakedModel model, float partialTicks)
    {
        if(!wheel.shouldRender())
            return;

        GlStateManager.pushMatrix();
        GlStateManager.translated((wheel.getOffsetX() * 0.0625) * wheel.getSide().offset, wheel.getOffsetY() * 0.0625, wheel.getOffsetZ() * 0.0625);
        if(wheel.getPosition() == Wheel.Position.FRONT)
        {
            float wheelAngle = vehicle.prevRenderWheelAngle + (vehicle.renderWheelAngle - vehicle.prevRenderWheelAngle) * partialTicks;
            GlStateManager.rotatef(wheelAngle, 0, 1, 0);
        }
        if(vehicle.isMoving())
        {
            GlStateManager.rotatef(-wheel.getWheelRotation(vehicle, partialTicks), 1, 0, 0);
        }
        GlStateManager.translated((((wheel.getWidth() * wheel.getScaleX()) / 2) * 0.0625) * wheel.getSide().offset, 0.0, 0.0);
        GlStateManager.scalef(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
        if(wheel.getSide() == Wheel.Side.RIGHT)
        {
            GlStateManager.rotatef(180F, 0, 1, 0);
        }
        RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, false, vehicle.getWheelColor());
        GlStateManager.popMatrix();
    }
}
