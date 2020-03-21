package com.mrcrayfish.vehicle.client.render.layer;

import com.mrcrayfish.vehicle.client.HeldVehicleEvents;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;

/**
 * Author: MrCrayfish
 */
public class LayerHeldVehicle implements LayerRenderer<AbstractClientPlayer>
{
    private Class<? extends Entity> cachedClass = null;
    private EntityVehicle cachedEntity = null;

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        NBTTagCompound tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
        if(!tagCompound.hasNoTags())
        {
            Class<? extends Entity> entityClass = EntityList.getClassFromName(tagCompound.getString("id"));
            if(entityClass != null && cachedClass != entityClass && EntityVehicle.class.isAssignableFrom(entityClass))
            {
                try
                {
                    cachedClass = entityClass;
                    cachedEntity = (EntityVehicle) entityClass.getDeclaredConstructor(World.class).newInstance(player.world);
                    cachedEntity.readFromNBT(tagCompound);
                    cachedEntity.getDataManager().getAll().forEach(dataEntry -> cachedEntity.notifyDataManagerChange(dataEntry.getKey()));
                }
                catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
            if(cachedEntity != null && cachedClass != null)
            {
                GlStateManager.pushMatrix();
                {
                    HeldVehicleEvents.AnimationCounter counter = HeldVehicleEvents.idToCounter.get(player.getUniqueID());
                    if(counter != null)
                    {
                        float width = cachedEntity.width / 2;
                        GlStateManager.translate(0F, 1F - 1F * counter.getProgress(partialTicks), -0.5F * Math.sin(Math.PI * counter.getProgress(partialTicks)) - width * (1.0F - counter.getProgress(partialTicks)));
                    }
                    Vec3d heldOffset = cachedEntity.getProperties().getHeldOffset();
                    GlStateManager.translate(heldOffset.x * 0.0625D, heldOffset.y * 0.0625D, heldOffset.z * 0.0625D);
                    GlStateManager.rotate(180F, 1, 0, 0);
                    GlStateManager.rotate(-90F, 0, 1, 0);
                    GlStateManager.translate(0F, player.isSneaking() ? 0.3F : 0.5F, 0F);
                    Render<Entity> render = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(cachedClass);
                    render.doRender(cachedEntity, 0.0D, 0.0D, 0.0D, 0F, 0F);
                }
                GlStateManager.popMatrix();
            }
        }
        else
        {
            cachedClass = null;
            cachedEntity = null;
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}
