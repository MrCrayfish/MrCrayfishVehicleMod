package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.EntityMoped;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import java.util.Calendar;

/**
 * Author: MrCrayfish
 */
public class RenderMoped extends AbstractRenderVehicle<EntityMoped>
{
    private static final ModelChest MOPED_CHEST = new ModelChest();
    private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");
    public final boolean isChristmas;

    public RenderMoped()
    {
        Calendar calendar = Calendar.getInstance();
        this.isChristmas = calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26;
    }

    @Override
    public void render(EntityMoped entity, float partialTicks)
    {
        Minecraft.getMinecraft().getRenderManager().setDebugBoundingBox(false);

        this.renderDamagedPart(entity, SpecialModels.MOPED_BODY.getModel());

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, -0.0625, 11.5 * 0.0625);
            GlStateManager.rotate(-22.5F, 1, 0, 0);

            float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 25F;

            GlStateManager.rotate(turnRotation / 2, 0, 1, 0);
            GlStateManager.rotate(22.5F, 1, 0, 0);
            GlStateManager.translate(0, 0, -11.5 * 0.0625);

            //Render handles bars
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, 0.835, 0.525);
                GlStateManager.scale(0.8, 0.8, 0.8);
                this.renderDamagedPart(entity, SpecialModels.MOPED_HANDLE_BAR.getModel());
            }
            GlStateManager.popMatrix();

            //Render front bar and mud guard
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, -0.12, 0.785);
                GlStateManager.rotate(-22.5F, 1, 0, 0);
                GlStateManager.scale(0.9, 0.9, 0.9);
                this.renderDamagedPart(entity, SpecialModels.MOPED_MUD_GUARD.getModel());
            }
            GlStateManager.popMatrix();

            //Render front wheel
            if(entity.hasWheels())
            {
                GlStateManager.pushMatrix();
                {
                    GlStateManager.translate(0, -0.4, 14.5 * 0.0625);
                    float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-frontWheelSpin, 1, 0, 0);
                    }
                    GlStateManager.scale(1.3F, 1.3F, 1.3F);
                    IBakedModel model = RenderUtil.getWheelModel(entity);
                    if(model != null)
                    {
                        RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, entity.getWheelColor());
                    }
                }
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();

        if(entity.hasChest())
        {
            //Render chest
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, 0.3, -0.675);
                GlStateManager.rotate(180F, 0, 1, 0);
                GlStateManager.scale(1.0F, -1.0F, -1.0F);
                GlStateManager.scale(0.6F, 0.6F, 0.6F);
                GlStateManager.translate(-0.5F, -0.5F, -0.5F);

                if(this.isChristmas)
                {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_CHRISTMAS);
                }
                else
                {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_NORMAL);
                }
                MOPED_CHEST.renderAll();
            }
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void applyPlayerModel(EntityMoped entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-75F - turnRotation);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(7F);
        model.bipedRightArm.offsetZ -= 0.05 * wheelAngleNormal;
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-75F + turnRotation);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-7F);
        model.bipedLeftArm.offsetZ -= 0.05 * -wheelAngleNormal;
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-55F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-55F);
    }

    @Override
    public void applyPlayerRender(EntityMoped entity, EntityPlayer player, float partialTicks)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUniqueID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3d seatVec = seat.getPosition().addVector(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale());
            seatVec = new Vec3d(-seatVec.x, seatVec.y, seatVec.z);
            seatVec = seatVec.scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = seatVec.x * scale;
            double offsetY = (seatVec.y + player.getYOffset()) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = -seatVec.z * scale;
            GlStateManager.translate(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * 20F, 0, 0, 1);
            GlStateManager.translate(-offsetX, -offsetY, -offsetZ);
        }
    }
}
