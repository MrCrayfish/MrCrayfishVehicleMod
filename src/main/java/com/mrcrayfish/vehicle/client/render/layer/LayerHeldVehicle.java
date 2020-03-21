package com.mrcrayfish.vehicle.client.render.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.HeldVehicleEvents;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class LayerHeldVehicle extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>
{
    private EntityType<VehicleEntity> cachedType = null;
    private VehicleEntity cachedEntity = null;

    public LayerHeldVehicle(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRendererIn)
    {
        super(entityRendererIn);
    }

    @Override
    public void render(AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        CompoundNBT tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
        if(!tagCompound.isEmpty())
        {
            Optional<EntityType<?>> optional = EntityType.byKey(tagCompound.getString("id"));
            if(optional.isPresent())
            {
                EntityType<?> entityType = optional.get();
                Entity entity = entityType.create(player.world);
                if(entity instanceof VehicleEntity)
                {
                    entity.read(tagCompound);
                    entity.getDataManager().getAll().forEach(dataEntry -> entity.notifyDataManagerChange(dataEntry.getKey()));
                    this.cachedType = (EntityType<VehicleEntity>) entityType;
                    this.cachedEntity = (VehicleEntity) entity;
                }
            }
            if(this.cachedEntity != null && this.cachedType != null)
            {
                GlStateManager.pushMatrix();
                {
                    HeldVehicleEvents.AnimationCounter counter = HeldVehicleEvents.idToCounter.get(player.getUniqueID());
                    if(counter != null)
                    {
                        float width = this.cachedEntity.getWidth() / 2;
                        GlStateManager.translated(0F, 1F - 1F * counter.getProgress(partialTicks), -0.5F * Math.sin(Math.PI * counter.getProgress(partialTicks)) - width * (1.0F - counter.getProgress(partialTicks)));
                    }
                    Vec3d heldOffset = this.cachedEntity.getProperties().getHeldOffset();
                    GlStateManager.translated(heldOffset.x * 0.0625D, heldOffset.y * 0.0625D, heldOffset.z * 0.0625D);
                    GlStateManager.rotatef(180F, 1, 0, 0);
                    GlStateManager.rotatef(-90F, 0, 1, 0);
                    GlStateManager.translated(0F, player.isSneaking() ? 0.3125F : 0.5625F, 0F);
                    EntityRenderer<VehicleEntity> render = (EntityRenderer<VehicleEntity>) Minecraft.getInstance().getRenderManager().getRenderer(this.cachedEntity);
                    render.doRender(this.cachedEntity, 0.0D, 0.0D, 0.0D, 0F, 0F);
                }
                GlStateManager.popMatrix();
            }
        }
        else
        {
            this.cachedType = null;
            this.cachedEntity = null;
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}