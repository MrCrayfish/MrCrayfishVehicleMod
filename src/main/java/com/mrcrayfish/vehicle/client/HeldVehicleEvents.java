package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.obfuscate.client.event.ModelPlayerEvent;
import com.mrcrayfish.vehicle.client.render.layer.LayerHeldVehicle;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
            Render render = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(AbstractClientPlayer.class);
            Map<String, RenderPlayer> skinMap = render.getRenderManager().getSkinMap();
            this.patchPlayerRender(skinMap.get("default"));
            this.patchPlayerRender(skinMap.get("slim"));
            setupExtraLayers = true;
        }
    }

    private void patchPlayerRender(RenderPlayer player)
    {
        List<LayerRenderer<AbstractClientPlayer>> layers = ObfuscationReflectionHelper.getPrivateValue(RenderLivingBase.class, player, "field_177097_h");
        if(layers != null)
        {
            layers.add(new LayerHeldVehicle());
        }
    }

    public static final Map<UUID, AnimationCounter> idToCounter = new HashMap<>();

    @SubscribeEvent
    public void onSetupAngles(ModelPlayerEvent.SetupAngles.Post event)
    {
        ModelPlayer model = event.getModelPlayer();
        EntityPlayer player = event.getEntityPlayer();

        boolean holdingVehicle = HeldVehicleDataHandler.isHoldingVehicle(player);
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
        model.bipedRightArm.rotationPointY = -1.5F * progress;
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-180F * progress);
        model.bipedLeftArm.rotateAngleZ = (float) Math.toRadians(5F * progress);
        model.bipedLeftArm.rotationPointY = -1.5F * progress;
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
