package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.obfuscate.client.event.ModelPlayerEvent;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.InvocationTargetException;

/**
 * Author: MrCrayfish
 */
public class HeldVehicleEvents
{
    private Class<? extends Entity> cachedClass = null;
    private EntityVehicle cachedEntity = null;

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

    @SubscribeEvent
    public void onPreRender(ModelPlayerEvent.Render.Post event)
    {
        EntityPlayer player = event.getEntityPlayer();
        NBTTagCompound tagCompound = player.getDataManager().get(CommonEvents.HELD_VEHICLE);
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
                    Vec3d heldOffset = cachedEntity.getHeldOffset();
                    GlStateManager.translate(heldOffset.x * 0.0625D, heldOffset.y * 0.0625D, heldOffset.z * 0.0625D);
                    GlStateManager.rotate(180F, 1, 0, 0);
                    GlStateManager.rotate(-90F, 0, 1, 0);
                    GlStateManager.translate(0F, player.isSneaking() ? 0.22F : 0.5F, 0F);
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
}
