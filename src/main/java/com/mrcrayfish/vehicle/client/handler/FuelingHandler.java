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
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundCategory;
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
            if(this.fueling)
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
        boolean rightHanded = player.getPrimaryHand() == HandSide.RIGHT;
        if(rightHanded)
        {
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-20F);
            model.bipedRightArm.rotateAngleY = (float) Math.toRadians(0F);
            model.bipedRightArm.rotateAngleZ = (float) Math.toRadians(0F);
        }
        else
        {
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-20F);
            model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(0F);
            model.bipedLeftArm.rotateAngleZ = (float) Math.toRadians(0F);
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
            if(this.fuelTickCounter % 3 == 0)
            {
                player.playSound(ModSounds.LIQUID_GLUG.get(), 0.3F, 1.0F);
            }

            double offset = Math.sin((this.fuelTickCounter + minecraft.getRenderPartialTicks()) / 3.0) * 0.1;
            matrixStack.translate(0, 0.35 + offset, -0.2);
            matrixStack.rotate(Axis.POSITIVE_X.rotationDegrees(-25F));
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
                HandSide handSide = mainHand ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
                int handOffset = handSide == HandSide.RIGHT ? 1 : -1;
                IRenderTypeBuffer renderTypeBuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
                int light = minecraft.getRenderManager().getPackedLight(player, event.getPartialTicks());

                matrixStack.push();
                matrixStack.translate(handOffset * 0.65, -0.27, -0.72);
                matrixStack.rotate(Axis.POSITIVE_X.rotationDegrees(45F));
                RenderUtil.renderColoredModel(SpecialModels.NOZZLE.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY); //TODO check
                matrixStack.pop();
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
        matrixStack.push();

        if(event.getModelPlayer().isChild)
        {
            matrixStack.translate(0.0, 0.75, 0.0);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
        }

        if(player.isCrouching())
        {
            matrixStack.translate(0.0, 0.2, 0.0);
        }

        event.getModelPlayer().translateHand(HandSide.RIGHT, event.getMatrixStack());
        matrixStack.rotate(Axis.POSITIVE_X.rotationDegrees(180F));
        matrixStack.rotate(Axis.POSITIVE_Y.rotationDegrees(180F));
        boolean leftHanded = player.getPrimaryHand() == HandSide.LEFT;
        matrixStack.translate((leftHanded ? -1 : 1) / 16.0, 0.125, -0.625);
        matrixStack.translate(0, -9 * 0.0625F, 5.75 * 0.0625F);

        IRenderTypeBuffer renderTypeBuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        RenderUtil.renderColoredModel(SpecialModels.NOZZLE.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, 15728880, OverlayTexture.NO_OVERLAY);

        matrixStack.pop();
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
