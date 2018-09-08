package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mrcrayfish.vehicle.block.BlockRotatedObject;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidExtractor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class FluidExtractorRenderer extends TileEntitySpecialRenderer<TileEntityFluidExtractor>
{
    @Override
    public void render(TileEntityFluidExtractor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if(state.getBlock() != ModBlocks.REFINERY)
            return;

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);

            GlStateManager.disableCull();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            EnumFacing facing = state.getValue(BlockRotatedObject.FACING);
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.rotate(facing.getHorizontalIndex() * -90F - 90F, 0, 1, 0);
            GlStateManager.translate(-0.5, -0.5, -0.5);
            double height = 12.0 * (te.getFluidLevel() / (double) TileEntityFluidExtractor.TANK_CAPACITY);
            if(height > 0) drawFluid(te, 10 * 0.0625, 2 * 0.0625, 0.01 * 0.0625, 5.99 * 0.0625, height * 0.0625, (16 - 0.02) * 0.0625);

            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
        }
        GlStateManager.popMatrix();
    }

    private void drawFluid(TileEntityFluidExtractor te, double x, double y, double z, double width, double height, double depth)
    {
        if(te.getFluidStackTank() == null)
            return;

        ResourceLocation resource = te.getFluidStackTank().getFluid().getStill();
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(resource.toString());
        if(sprite != null)
        {
            double minU = sprite.getMinU();
            double maxU = Math.min(minU + (sprite.getMaxU() - minU) * width, sprite.getMaxU());
            double minV = sprite.getMinV();
            double maxV = Math.min(minV + (sprite.getMaxV() - minV) * height, sprite.getMaxV());

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            int light = getWorld().getCombinedLight(te.getPos(), ModFluids.FUELIUM.getLuminosity());
            int lightX = light >> 0x10 & 0xFFFF;
            int lightY = light & 0xFFFF;

            //left side
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buffer.pos(x + width, y, z).tex(maxU, minV).lightmap(lightX, lightY).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            buffer.pos(x, y, z).tex(minU, minV).lightmap(lightX, lightY).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            buffer.pos(x, y + height, z).tex(minU, maxV).lightmap(lightX, lightY).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z).tex(maxU, maxV).lightmap(lightX, lightY).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            tessellator.draw();

            //right side
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buffer.pos(x, y, z + depth).tex(maxU, minV).lightmap(lightX, lightY).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            buffer.pos(x + width, y, z + depth).tex(minU, minV).lightmap(lightX, lightY).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z + depth).tex(minU, maxV).lightmap(lightX, lightY).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            buffer.pos(x, y + height, z + depth).tex(maxU, maxV).lightmap(lightX, lightY).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            tessellator.draw();

            maxU = Math.min(minU + (sprite.getMaxU() - minU) * depth, sprite.getMaxU());

            //back side
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buffer.pos(x + width, y, z + depth).tex(maxU, minV).lightmap(lightX, lightY).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            buffer.pos(x + width, y, z).tex(minU, minV).lightmap(lightX, lightY).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z).tex(minU, maxV).lightmap(lightX, lightY).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z + depth).tex(maxU, maxV).lightmap(lightX, lightY).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            tessellator.draw();

            maxV = Math.min(minV + (sprite.getMaxV() - minV) * width, sprite.getMaxV());

            //top
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buffer.pos(x, y + height, z).tex(maxU, minV).lightmap(lightX, lightY).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            buffer.pos(x, y + height, z + depth).tex(minU, minV).lightmap(lightX, lightY).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z + depth).tex(minU, maxV).lightmap(lightX, lightY).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z).tex(maxU, maxV).lightmap(lightX, lightY).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            tessellator.draw();
        }
    }
}
