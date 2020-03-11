package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.MopedEntity;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.model.ChestModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import java.util.Calendar;

/**
 * Author: MrCrayfish
 */
public class RenderMoped extends AbstractRenderVehicle<MopedEntity>
{
    private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");

    private final ChestModel chest = new ChestModel();
    public final boolean isChristmas;

    public RenderMoped()
    {
        Calendar calendar = Calendar.getInstance();
        this.isChristmas = calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26;
    }

    @Override
    public void render(MopedEntity entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.MOPED_BODY.getModel());

        GlStateManager.pushMatrix();

        GlStateManager.translated(0.0, -0.0625, 11.5 * 0.0625);
        GlStateManager.rotatef(-22.5F, 1, 0, 0);

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 25F;

        GlStateManager.rotatef(turnRotation / 2, 0, 1, 0);
        GlStateManager.rotatef(22.5F, 1, 0, 0);
        GlStateManager.translated(0.0, 0.0, -11.5 * 0.0625);

        //Render handles bars
        GlStateManager.pushMatrix();
        GlStateManager.translated(0, 0.835, 0.525);
        GlStateManager.scalef(0.8F, 0.8F, 0.8F);
        this.renderDamagedPart(entity, SpecialModels.MOPED_HANDLES.getModel());
        GlStateManager.popMatrix();

        //Render front bar and mud guard
        GlStateManager.pushMatrix();
        {
            GlStateManager.translated(0, -0.12, 0.785);
            GlStateManager.rotatef(-22.5F, 1, 0, 0);
            GlStateManager.scalef(0.9F, 0.9F, 0.9F);
            this.renderDamagedPart(entity, SpecialModels.MOPED_MUD_GUARD.getModel());
        }
        GlStateManager.popMatrix();

        //Render front wheel
        if(entity.hasWheels())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translated(0, -0.4, 14.5 * 0.0625);
            float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
            if(entity.isMoving())
            {
                GlStateManager.rotatef(-frontWheelSpin, 1, 0, 0);
            }
            GlStateManager.scalef(1.3F, 1.3F, 1.3F);
            RenderUtil.renderColoredModel(RenderUtil.getModel(new ItemStack(ModItems.STANDARD_WHEEL)), ItemCameraTransforms.TransformType.NONE, false, -1);
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();

        if(entity.hasChest())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translated(0, 0.25, -0.65);
            GlStateManager.rotatef(180F, 0, 1, 0);
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
            if(this.isChristmas)
            {
                Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_CHRISTMAS);
            }
            else
            {
                Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_NORMAL);
            }
            this.chest.renderAll();
            //ItemStack chest = new ItemStack(Blocks.CHEST);
            //RenderUtil.renderModel(chest, ItemCameraTransforms.TransformType.NONE, false, RenderUtil.getModel(chest));
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void applyPlayerModel(MopedEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-75F - turnRotation);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(7F);
        //model.bipedRightArm.offsetZ -= 0.05 * wheelAngleNormal; //TODO figure out offsets
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-75F + turnRotation);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-7F);
        //model.bipedLeftArm.offsetZ -= 0.05 * -wheelAngleNormal;
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-55F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-55F);
    }

    @Override
    public void applyPlayerRender(MopedEntity entity, PlayerEntity player, float partialTicks)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUniqueID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).mul(-1, 1, 1).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y + player.getYOffset()) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = seatVec.z * scale;
            GlStateManager.translated(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            GlStateManager.rotatef(turnAngleNormal * currentSpeedNormal * 20F, 0, 0, 1);
            GlStateManager.translated(-offsetX, -offsetY, -offsetZ);
        }
    }
}
