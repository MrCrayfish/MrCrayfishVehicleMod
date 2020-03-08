package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.entity.trailer.EntityFluidTrailer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class RenderFluidTrailer extends AbstractRenderTrailer<EntityFluidTrailer>
{
    @Override
    public void render(EntityFluidTrailer entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.FLUID_TRAILER.getModel());
        this.renderWheel(entity, false, -11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks);
        this.renderWheel(entity, true, 11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks);

        double height = 9.9 * (entity.getTank().getFluidAmount() / (double) entity.getTank().getCapacity()) * 0.0625;
        this.drawFluid(entity.getTank(), -0.3875, -0.1875, -0.99, 0.7625, height, 1.67);
    }

    private void drawFluid(FluidTank tank, double x, double y, double z, double width, double height, double depth)
    {
        if(tank.getFluid() == null)
            return;

        ResourceLocation resource = tank.getFluid().getFluid().getStill();
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(resource.toString());
        if(sprite != null)
        {
            double minU = sprite.getMinU();
            double maxU = Math.min(minU + (sprite.getMaxU() - minU) * width, sprite.getMaxU());
            double minV = sprite.getMinV();
            double maxV = Math.min(minV + (sprite.getMaxV() - minV) * height, sprite.getMaxV());

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.disableLighting();

            //left side
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(x + width, y, z).tex(maxU, minV).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            buffer.pos(x, y, z).tex(minU, minV).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            buffer.pos(x, y + height, z).tex(minU, maxV).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z).tex(maxU, maxV).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            tessellator.draw();

            //right side
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(x, y, z + depth).tex(maxU, minV).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            buffer.pos(x + width, y, z + depth).tex(minU, minV).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z + depth).tex(minU, maxV).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            buffer.pos(x, y + height, z + depth).tex(maxU, maxV).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            tessellator.draw();

            maxU = Math.min(minU + (sprite.getMaxU() - minU) * depth, sprite.getMaxU());

            /*//back side
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(x + width, y, z + depth).tex(maxU, minV).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            buffer.pos(x + width, y, z).tex(minU, minV).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z).tex(minU, maxV).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z + depth).tex(maxU, maxV).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            tessellator.draw();*/

            maxV = Math.min(minV + (sprite.getMaxV() - minV) * width, sprite.getMaxV());

            //top
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(x, y + height, z).tex(maxU, minV).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            buffer.pos(x, y + height, z + depth).tex(minU, minV).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z + depth).tex(minU, maxV).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z).tex(maxU, maxV).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            tessellator.draw();
        }
    }
}
