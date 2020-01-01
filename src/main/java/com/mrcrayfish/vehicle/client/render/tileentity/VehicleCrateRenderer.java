package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.tileentity.VehicleCrateTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

/**
 * Author: MrCrayfish
 */
public class VehicleCrateRenderer extends TileEntityRenderer<VehicleCrateTileEntity>
{
    public VehicleCrateRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void func_225616_a_(VehicleCrateTileEntity vehicleCrateTileEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1)
    {
        /*IBlockState state = te.getWorld().getBlockState(te.getPos());
        if(state.getBlock() != ModBlocks.VEHICLE_CRATE)
            return;

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);

            Direction facing = state.getValue(BlockRotatedObject.DIRECTION);
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.rotate(facing.getHorizontalIndex() * -90F + 180F, 0, 1, 0);
            GlStateManager.translate(-0.5, -0.5, -0.5);

            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.pushMatrix();

            if(te.isOpened() && te.getTimer() > 150)
            {
                double progress = Math.min(1.0F, Math.max(0, (te.getTimer() - 150 + 5 * partialTicks)) / 50.0);
                GlStateManager.translate(0, (-4 * 0.0625) * progress, 0);
            }

            //Sides panels
            for(int i = 0; i < 4; i++)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.5, 0, 0.5);
                GlStateManager.rotate(90F * i, 0, 1, 0);
                GlStateManager.translate(0, 0, 8 * 0.0625);

                if(te.isOpened())
                {
                    double progress = Math.min(1.0, Math.max(0, te.getTimer() - (i * 20) + 5 * partialTicks) / 90.0);
                    double angle = (progress * progress) * 90F;
                    double rotation = 1.0 - Math.cos(Math.toRadians(angle));
                    GlStateManager.rotate((float) rotation * 90F, 1, 0, 0);
                }

                GlStateManager.translate(-0.5, 0, -0.5);
                GlStateManager.translate(0, 0, -2 * 0.0625);
                Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(SpecialModel.VEHICLE_CRATE.getModel(), 1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.popMatrix();
            }

            //Render top panel
            if(!te.isOpened())
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.5, 0.5, 0.5);
                GlStateManager.rotate(-90F, 1, 0, 0);
                GlStateManager.scale(1.001, 1.001, 1.001);
                GlStateManager.translate(-0.5, -0.5, -0.5);
                GlStateManager.translate(0, 0, (6 * 0.0625) * 0.998);
                Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(SpecialModel.VEHICLE_CRATE.getModel(), 1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.popMatrix();
            }

            //Render bottom panel
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.rotate(90F, 1, 0, 0);
            GlStateManager.scale(1.001, 1.001, 1.001);
            GlStateManager.translate(-0.5, -0.5, -0.5);
            GlStateManager.translate(0, 0, (6 * 0.0625) * 0.998);
            Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(SpecialModel.VEHICLE_CRATE.getModel(), 1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();

            GlStateManager.popMatrix();

            if(te.getEntity() != null && te.isOpened())
            {
                GlStateManager.enableLighting();
                GlStateManager.translate(0.5F, 0.0F, 0.5F);

                double progress = Math.min(1.0F, Math.max(0, (te.getTimer() - 150 + 5 * partialTicks)) / 100.0);
                Pair<Float, Float> scaleAndOffset = EntityRaytracer.getCrateScaleAndOffset(te.getEntity().getClass());
                float scaleStart = scaleAndOffset.getLeft();
                double scale = scaleStart + (1 - scaleStart) * progress;
                GlStateManager.translate(0, 0, scaleAndOffset.getRight() * (1 - progress) * scale);

                if(te.getTimer() >= 150)
                {
                    GlStateManager.translate(0, Math.sin(Math.PI * progress) * 5, 0);
                    GlStateManager.rotate((float) (720F * progress), 0, 1, 0);
                }

                GlStateManager.translate(0, (2 * 0.0625F) * (1.0F - progress), 0);

                GlStateManager.scale(scale, scale, scale);
                te.getEntity().setLocationAndAngles(x, y, z, 0.0F, 0.0F);
                Minecraft.getMinecraft().getRenderManager().renderEntity(te.getEntity(), 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
            }
        }
        GlStateManager.popMatrix();*/
    }
}
