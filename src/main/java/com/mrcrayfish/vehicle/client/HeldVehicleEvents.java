package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.obfuscate.client.event.PlayerModelEvent;
import com.mrcrayfish.vehicle.client.render.layer.LayerHeldVehicle;
import com.mrcrayfish.vehicle.common.CustomDataParameters;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class HeldVehicleEvents
{
    private static boolean setupExtraLayers = false;

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event)
    {
        if(!setupExtraLayers)
        {
            Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
            this.patchPlayerRender(skinMap.get("default"));
            this.patchPlayerRender(skinMap.get("slim"));
            setupExtraLayers = true;
        }
    }

    private void patchPlayerRender(PlayerRenderer player)
    {
        List<LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>> layers = ObfuscationReflectionHelper.getPrivateValue(LivingRenderer.class, player, "field_177097_h");
        if(layers != null)
        {
            layers.add(new LayerHeldVehicle(player));
        }
    }

    public static final Map<UUID, AnimationCounter> idToCounter = new HashMap<>();

    @SubscribeEvent
    public void onSetupAngles(PlayerModelEvent.SetupAngles.Post event)
    {
        PlayerModel model = event.getModelPlayer();
        PlayerEntity player = event.getPlayer();

        boolean holdingVehicle = !player.getDataManager().get(CustomDataParameters.HELD_VEHICLE).isEmpty();
        if(holdingVehicle && !idToCounter.containsKey(player.getUniqueID()))
        {
            idToCounter.put(player.getUniqueID(), new AnimationCounter(40));
        }
        else if(idToCounter.containsKey(player.getUniqueID()))
        {
            if(idToCounter.get(player.getUniqueID()).getProgress(event.getPartialTicks()) == 0F)
            {
                idToCounter.remove(player.getUniqueID());
                return;
            }
            if(!holdingVehicle)
            {
                AnimationCounter counter = idToCounter.get(player.getUniqueID());
                player.renderYawOffset = player.getRotationYawHead() - (player.getRotationYawHead() - player.prevRenderYawOffset) * counter.getProgress(event.getPartialTicks());
            }
        }
        else
        {
            return;
        }

        AnimationCounter counter = idToCounter.get(player.getUniqueID());
        counter.update(holdingVehicle);
        float progress = counter.getProgress(event.getPartialTicks());
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-180F * progress);
        model.bipedRightArm.rotateAngleZ = (float) Math.toRadians(-5F * progress);
        model.bipedRightArm.rotationPointY = (player.isCrouching() ? 3.0F : -0.5F) * progress;
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-180F * progress);
        model.bipedLeftArm.rotateAngleZ = (float) Math.toRadians(5F * progress);
        model.bipedLeftArm.rotationPointY = (player.isCrouching() ? 3.0F : -0.5F) * progress;
    }

    public static class AnimationCounter
    {
        private final int MAX_COUNT;
        private int prevCount;
        private int currentCount;

        private AnimationCounter(int maxCount)
        {
            this.MAX_COUNT = maxCount;
        }

        public int update(boolean increment)
        {
            prevCount = currentCount;
            if(increment)
            {
                if(currentCount < MAX_COUNT)
                {
                    currentCount++;
                }
            }
            else
            {
                if(currentCount > 0)
                {
                    currentCount = Math.max(0, currentCount - 2);
                }
            }
            return currentCount;
        }

        public int getMaxCount()
        {
            return MAX_COUNT;
        }

        public int getCurrentCount()
        {
            return currentCount;
        }

        public float getProgress(float partialTicks)
        {
            return (prevCount + (currentCount - prevCount) * partialTicks) / (float) MAX_COUNT;
        }
    }
}
