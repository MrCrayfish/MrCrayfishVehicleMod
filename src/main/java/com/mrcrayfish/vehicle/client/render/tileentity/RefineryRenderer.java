package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mrcrayfish.vehicle.block.BlockRotatedObject;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.tileentity.TileEntityRefinery;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class RefineryRenderer extends TileEntitySpecialRenderer<TileEntityRefinery>
{
    @Override
    public void render(TileEntityRefinery te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if(state.getBlock() != ModBlocks.REFINERY)
            return;

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.disableLighting();

            EnumFacing facing = state.getValue(BlockRotatedObject.FACING);
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.rotate(facing.getHorizontalIndex() * -90F - 90F, 0, 1, 0);
            GlStateManager.translate(-0.5, -0.5, -0.5);
            double height = 12.0 * (te.getFueliumLevel() / (double) TileEntityRefinery.TANK_CAPACITY);
            if(height > 0) drawFluid(10 * 0.0625, 2 * 0.0625, 0.01 * 0.0625, 5.99 * 0.0625, height * 0.0625, (16 - 0.02) * 0.0625);
            GlStateManager.enableLighting();
        }
        GlStateManager.popMatrix();
    }

    private void drawFluid(double x, double y, double z, double width, double height, double depth)
    {
        ResourceLocation resource = ModFluids.FUELIUM.getStill();
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(resource.toString());
        if(sprite != null)
        {
            double minU = sprite.getMinU();
            double maxU = Math.min(minU + (sprite.getMaxU() - minU) * width, sprite.getMaxU());
            double minV = sprite.getMinV();
            double maxV = Math.min(minV + (sprite.getMaxV() - minV) * height, sprite.getMaxV());

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            //left side
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x + width, y, z).tex(maxU, minV).endVertex();
            buffer.pos(x, y, z).tex(minU, minV).endVertex();
            buffer.pos(x, y + height, z).tex(minU, maxV).endVertex();
            buffer.pos(x + width, y + height, z).tex(maxU, maxV).endVertex();
            tessellator.draw();

            //right side
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x, y, z + depth).tex(maxU, minV).endVertex();
            buffer.pos(x + width, y, z + depth).tex(minU, minV).endVertex();
            buffer.pos(x + width, y + height, z + depth).tex(minU, maxV).endVertex();
            buffer.pos(x, y + height, z + depth).tex(maxU, maxV).endVertex();
            tessellator.draw();

            maxU = Math.min(minU + (sprite.getMaxU() - minU) * depth, sprite.getMaxU());

            //back side
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x + width, y, z + depth).tex(maxU, minV).endVertex();
            buffer.pos(x + width, y, z).tex(minU, minV).endVertex();
            buffer.pos(x + width, y + height, z).tex(minU, maxV).endVertex();
            buffer.pos(x + width, y + height, z + depth).tex(maxU, maxV).endVertex();
            tessellator.draw();

            maxV = Math.min(minV + (sprite.getMaxV() - minV) * width, sprite.getMaxV());

            //top
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x, y + height, z).tex(maxU, minV).endVertex();
            buffer.pos(x, y + height, z + depth).tex(minU, minV).endVertex();
            buffer.pos(x + width, y + height, z + depth).tex(minU, maxV).endVertex();
            buffer.pos(x + width, y + height, z).tex(maxU, maxV).endVertex();
            tessellator.draw();
        }
    }
}
