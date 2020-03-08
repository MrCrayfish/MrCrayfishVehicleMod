package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mrcrayfish.vehicle.block.BlockRotatedObject;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.TileEntityVehicleCrate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class VehicleCrateRenderer extends TileEntitySpecialRenderer<TileEntityVehicleCrate>
{
    @Override
    public void render(TileEntityVehicleCrate te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if(state.getBlock() != ModBlocks.VEHICLE_CRATE)
            return;

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);

            EnumFacing facing = state.getValue(BlockRotatedObject.FACING);
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
                Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(SpecialModels.VEHICLE_CRATE.getModel(), 1.0F, 1.0F, 1.0F, 1.0F);
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
                Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(SpecialModels.VEHICLE_CRATE.getModel(), 1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.popMatrix();
            }

            //Render bottom panel
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.rotate(90F, 1, 0, 0);
            GlStateManager.scale(1.001, 1.001, 1.001);
            GlStateManager.translate(-0.5, -0.5, -0.5);
            GlStateManager.translate(0, 0, (6 * 0.0625) * 0.998);
            Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(SpecialModels.VEHICLE_CRATE.getModel(), 1.0F, 1.0F, 1.0F, 1.0F);
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
        GlStateManager.popMatrix();
    }

    private void renderModel(IBakedModel model, int color, ItemStack stack)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
        for (EnumFacing enumfacing : EnumFacing.values())
        {
            this.renderQuads(bufferbuilder, model.getQuads(null, enumfacing, 0L), color, stack);
        }

        this.renderQuads(bufferbuilder, model.getQuads(null, null, 0L), color, stack);
        tessellator.draw();
    }

    private void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack)
    {
        boolean hasColor = color == -1 && !stack.isEmpty();
        int i = 0;
        for (int j = quads.size(); i < j; ++i)
        {
            BakedQuad quad = quads.get(i);
            int k = color;

            if (hasColor && quad.hasTintIndex())
            {
                k = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, quad.getTintIndex());

                if (EntityRenderer.anaglyphEnable)
                {
                    k = TextureUtil.anaglyphColor(k);
                }

                k = k | -16777216;
            }

            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, quad, k);
        }
    }
}
