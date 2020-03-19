package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.block.BlockRotatedObject;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.VehicleCrateTileEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Author: MrCrayfish
 */
public class VehicleCrateRenderer extends TileEntityRenderer<VehicleCrateTileEntity>
{
    @Override
    public void render(VehicleCrateTileEntity crate, double x, double y, double z, float partialTicks, int destroyStage)
    {
        BlockState state = crate.getWorld().getBlockState(crate.getPos());
        if(state.getBlock() != ModBlocks.VEHICLE_CRATE.get())
            return;
        
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        Direction facing = state.get(BlockRotatedObject.DIRECTION);
        GlStateManager.translated(0.5, 0.5, 0.5);
        GlStateManager.rotatef(facing.getHorizontalIndex() * -90F + 180F, 0, 1, 0);
        GlStateManager.translated(-0.5, -0.5, -0.5);

        this.rendererDispatcher.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();

        if(crate.isOpened() && crate.getTimer() > 150)
        {
            double progress = Math.min(1.0F, Math.max(0, (crate.getTimer() - 150 + 5 * partialTicks)) / 50.0);
            GlStateManager.translated(0, (-4 * 0.0625) * progress, 0);
        }

        //Sides panels
        for(int i = 0; i < 4; i++)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translated(0.5, 0, 0.5);
            GlStateManager.rotatef(90F * i, 0, 1, 0);
            GlStateManager.translated(0, 0, 8 * 0.0625);

            if(crate.isOpened())
            {
                double progress = Math.min(1.0, Math.max(0, crate.getTimer() - (i * 20) + 5 * partialTicks) / 90.0);
                double angle = (progress * progress) * 90F;
                double rotation = 1.0 - Math.cos(Math.toRadians(angle));
                GlStateManager.rotated(rotation * 90F, 1, 0, 0);
            }
            GlStateManager.translated(0.0, 0.5, 0.0);
            GlStateManager.translated(0, 0, -2 * 0.0625);
            RenderUtil.renderColoredModel(SpecialModels.VEHICLE_CRATE.getModel(), ItemCameraTransforms.TransformType.NONE, false, -1);
            GlStateManager.popMatrix();
        }

        //Render top panel
        if(!crate.isOpened())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translated(0.5, 0.5, 0.5);
            GlStateManager.rotatef(-90F, 1, 0, 0);
            GlStateManager.scalef(1.005F, 1.005F, 1.005F);
            GlStateManager.translated(0, 0, (6 * 0.0625) * 0.998);
            RenderUtil.renderColoredModel(SpecialModels.VEHICLE_CRATE.getModel(), ItemCameraTransforms.TransformType.NONE, false, -1);
            GlStateManager.popMatrix();
        }

        //Render bottom panel
        GlStateManager.pushMatrix();
        GlStateManager.translated(0.5, 0.5, 0.5);
        GlStateManager.rotatef(90F, 1, 0, 0);
        GlStateManager.scalef(1.001F, 1.001F, 1.001F);
        GlStateManager.translated(0, 0, (6 * 0.0625) * 0.998);
        RenderUtil.renderColoredModel(SpecialModels.VEHICLE_CRATE.getModel(), ItemCameraTransforms.TransformType.NONE, false, -1);
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        if(crate.getEntity() != null && crate.isOpened())
        {
            GlStateManager.translated(0.5F, 0.0F, 0.5F);

            double progress = Math.min(1.0F, Math.max(0, (crate.getTimer() - 150 + 5 * partialTicks)) / 100.0);
            Pair<Float, Float> scaleAndOffset = EntityRaytracer.getCrateScaleAndOffset(crate.getEntity().getClass());
            float scaleStart = scaleAndOffset.getLeft();
            float scale = scaleStart + (1 - scaleStart) * (float) progress;
            GlStateManager.translated(0, 0, scaleAndOffset.getRight() * (1 - progress) * scale);

            if(crate.getTimer() >= 150)
            {
                GlStateManager.translated(0, Math.sin(Math.PI * progress) * 5, 0);
                GlStateManager.rotated(720F * progress, 0, 1, 0);
            }

            GlStateManager.translated(0, (2 * 0.0625F) * (1.0F - progress), 0);
            GlStateManager.scalef(scale, scale, scale);

            EntityRenderer<? extends Entity> renderer = Minecraft.getInstance().getRenderManager().getRenderer(crate.getEntity());
            renderer.doRender(crate.getEntity(), 0.0F, 0.0F, 0.0F, 0.0F, 0);
        }
        GlStateManager.popMatrix();
    }
}
