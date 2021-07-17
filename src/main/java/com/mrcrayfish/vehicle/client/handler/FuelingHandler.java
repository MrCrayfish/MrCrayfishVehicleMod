package com.mrcrayfish.vehicle.client.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.obfuscate.client.event.PlayerModelEvent;
import com.mrcrayfish.obfuscate.client.event.RenderItemEvent;
import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.RayTraceFunction;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class FuelingHandler
{
    private int fuelTickCounter;
    private boolean fueling;
    private boolean renderNozzle;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if(event.phase != TickEvent.Phase.END || player == null)
            return;

        if(this.fueling)
        {
            this.fuelTickCounter++;
        }

        EntityRayTracer.RayTraceResultRotated result = EntityRayTracer.instance().getContinuousInteraction();
        if(result != null && result.equalsContinuousInteraction(RayTraceFunction.FUNCTION_FUELING))
        {
            if(this.fuelTickCounter % 20 == 0)
            {
                Vector3d vec = result.getLocation();
                player.level.playSound(player, vec.x(), vec.y(), vec.z(), ModSounds.ITEM_JERRY_CAN_LIQUID_GLUG.get(), SoundCategory.PLAYERS, 0.6F, 1.0F + 0.1F * player.level.random.nextFloat());
            }
            if(!this.fueling)
            {
                this.fuelTickCounter = 0;
                this.fueling = true;
            }
        }
        else
        {
            this.fueling = false;
        }
    }

    static void applyFuelingPose(PlayerEntity player, PlayerModel<?> model)
    {
        boolean rightHanded = player.getMainArm() == HandSide.RIGHT;
        if(rightHanded)
        {
            model.rightArm.xRot = (float) Math.toRadians(-20F);
            model.rightArm.yRot = (float) Math.toRadians(0F);
            model.rightArm.zRot = (float) Math.toRadians(0F);
        }
        else
        {
            model.leftArm.xRot = (float) Math.toRadians(-20F);
            model.leftArm.yRot = (float) Math.toRadians(0F);
            model.leftArm.zRot = (float) Math.toRadians(0F);
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event)
    {
        /*if(event.getHand() == Hand.OFF_HAND && this.fuelingHandOffset > -1)
        {
            double offset = Math.sin((this.fuelTickCounter + minecraft.getRenderPartialTicks()) / 3.0) * 0.1;
            matrixStack.rotate(Axis.POSITIVE_X.rotationDegrees(25F));
            matrixStack.translate(0, -0.35 - this.fuelingHandOffset, 0.2);
        }*/

        Minecraft minecraft = Minecraft.getInstance();
        PlayerEntity player = minecraft.player;
        MatrixStack matrixStack = event.getMatrixStack();
        EntityRayTracer.RayTraceResultRotated result = EntityRayTracer.instance().getContinuousInteraction();
        if(result != null && result.equalsContinuousInteraction(RayTraceFunction.FUNCTION_FUELING) && event.getHand() == EntityRayTracer.instance().getContinuousInteractionHand())
        {
            double offset = Math.sin((this.fuelTickCounter + minecraft.getFrameTime()) / 3.0) * 0.1;
            matrixStack.translate(0, 0.35 + offset, -0.2);
            matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-25F));
        }

        if(SyncedPlayerData.instance().get(player, ModDataKeys.GAS_PUMP).isPresent())
        {
            if(event.getSwingProgress() > 0) // WHY? TEST THIS TODO
            {
                this.renderNozzle = true;
            }

            if(event.getHand() == Hand.MAIN_HAND && this.renderNozzle)
            {
                if(event.getSwingProgress() > 0 && event.getSwingProgress() <= 0.25) //WHAT IS THIS?
                    return;

                event.setCanceled(true);

                boolean mainHand = event.getHand() == Hand.MAIN_HAND;
                HandSide handSide = mainHand ? player.getMainArm() : player.getMainArm().getOpposite();
                int handOffset = handSide == HandSide.RIGHT ? 1 : -1;
                IRenderTypeBuffer renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
                int light = minecraft.getEntityRenderDispatcher().getPackedLightCoords(player, event.getPartialTicks());

                matrixStack.pushPose();
                matrixStack.translate(handOffset * 0.65, -0.27, -0.72);
                matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(45F));
                RenderUtil.renderColoredModel(SpecialModels.NOZZLE.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY); //TODO check
                matrixStack.popPose();
            }
        }
        else
        {
            this.renderNozzle = false;
        }
    }

    @SubscribeEvent
    public void onModelRenderPost(PlayerModelEvent.Render.Post event)
    {
        PlayerEntity player = event.getPlayer();
        if(!SyncedPlayerData.instance().get(player, ModDataKeys.GAS_PUMP).isPresent())
            return;

        MatrixStack matrixStack = event.getMatrixStack();
        matrixStack.pushPose();

        if(event.getModelPlayer().young)
        {
            matrixStack.translate(0.0, 0.75, 0.0);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
        }

        if(player.isCrouching())
        {
            matrixStack.translate(0.0, 0.2, 0.0);
        }

        event.getModelPlayer().translateToHand(HandSide.RIGHT, event.getMatrixStack());
        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(180F));
        matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(180F));
        boolean leftHanded = player.getMainArm() == HandSide.LEFT;
        matrixStack.translate((leftHanded ? -1 : 1) / 16.0, 0.125, -0.625);
        matrixStack.translate(0, -9 * 0.0625F, 5.75 * 0.0625F);

        IRenderTypeBuffer renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderUtil.renderColoredModel(SpecialModels.NOZZLE.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, 15728880, OverlayTexture.NO_OVERLAY);

        matrixStack.popPose();
    }

    @SubscribeEvent
    public void onRenderThirdPerson(RenderItemEvent.Held.Pre event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof PlayerEntity && SyncedPlayerData.instance().get((PlayerEntity) entity, ModDataKeys.GAS_PUMP).isPresent())
        {
            event.setCanceled(true);
        }
    }
}
