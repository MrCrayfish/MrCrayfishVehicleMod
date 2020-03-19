package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.block.BlockFluidExtractor;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.tileentity.FluidExtractorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class FluidExtractorRenderer extends TileEntityRenderer<FluidExtractorTileEntity>
{
    @Override
    public void render(FluidExtractorTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        GlStateManager.translated(0.5, 0.5, 0.5);
        Direction direction = tileEntityIn.getBlockState().get(BlockFluidExtractor.DIRECTION);
        GlStateManager.rotatef(direction.getHorizontalIndex() * -90F - 90F, 0, 1, 0);
        GlStateManager.translated(-0.5, -0.5, -0.5);
        float height = (float) (12.0 * (tileEntityIn.getFluidLevel() / (double) tileEntityIn.getCapacity()));
        if(height > 0)
        {
            this.drawFluid(tileEntityIn, 10F * 0.0625F, 2F * 0.0625F, 0.01F * 0.0625F, 5.99F * 0.0625F, height * 0.0625F, (16 - 0.02F) * 0.0625F);
        }
        GlStateManager.popMatrix();
    }

    private void drawFluid(FluidExtractorTileEntity te, float x, float y, float z, float width, float height, float depth)
    {
        Fluid fluid = te.getFluidStackTank().getFluid();
        if(fluid == Fluids.EMPTY) return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureMap().getSprite(fluid.getFluid().getAttributes().getStillTexture());
        if(sprite != null)
        {
            double minU = sprite.getMinU();
            double maxU = Math.min(minU + (sprite.getMaxU() - minU) * width, sprite.getMaxU());
            double minV = sprite.getMinV();
            double maxV = Math.min(minV + (sprite.getMaxV() - minV) * height, sprite.getMaxV());

            Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

            int light = getWorld().getCombinedLight(te.getPos(), fluid.getAttributes().getLuminosity());
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
