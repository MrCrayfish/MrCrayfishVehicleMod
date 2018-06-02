package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.obfuscate.client.event.ModelPlayerEvent;
import com.mrcrayfish.vehicle.client.render.LayerHeldVehicle;
import com.mrcrayfish.vehicle.common.CommonEvents;
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

import java.util.List;
import java.util.Map;

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

    @SubscribeEvent
    public void onSetupAngles(ModelPlayerEvent.SetupAngles.Post event)
    {
        EntityPlayer player = event.getEntityPlayer();
        ModelPlayer model = event.getModelPlayer();
        if(!player.getDataManager().get(CommonEvents.HELD_VEHICLE).hasNoTags())
        {
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-180F);
            model.bipedRightArm.rotateAngleZ = (float) Math.toRadians(-5F);
            model.bipedRightArm.rotationPointY -= 1.5F;
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-180F);
            model.bipedLeftArm.rotateAngleZ = (float) Math.toRadians(5F);
            model.bipedLeftArm.rotationPointY -= 1.5F;
        }
    }
}
