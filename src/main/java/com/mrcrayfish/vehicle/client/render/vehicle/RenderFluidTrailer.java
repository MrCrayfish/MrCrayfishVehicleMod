package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.entity.trailer.FluidTrailerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class RenderFluidTrailer extends AbstractRenderTrailer<FluidTrailerEntity>
{
    @Override
    public void render(FluidTrailerEntity entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.FLUID_TRAILER.getModel());
        this.renderWheel(entity, false, -11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks);
        this.renderWheel(entity, true, 11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks);

        float height = 9.9F * (entity.getTank().getFluidAmount() / (float) entity.getTank().getCapacity()) * 0.0625F;
        this.drawFluid(entity, entity.getTank(), -0.3875F, -0.1875F, -0.99F, 0.7625F, height, 1.67F);
    }

    private void drawFluid(FluidTrailerEntity entity, FluidTank tank, float x, float y, float z, float width, float height, float depth)
    {
        Fluid fluid = tank.getFluid().getFluid();
        if(fluid == Fluids.EMPTY)
            return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureMap().getSprite(fluid.getFluid().getAttributes().getStillTexture());
        if(sprite != null)
        {
            int waterColor = fluid.getAttributes().getColor(entity.getEntityWorld(), entity.getPosition());
            float red = (float) (waterColor >> 16 & 255) / 255.0F;
            float green = (float) (waterColor >> 8 & 255) / 255.0F;
            float blue = (float) (waterColor & 255) / 255.0F;
            double minU = sprite.getMinU();
            double maxU = Math.min(minU + (sprite.getMaxU() - minU) * width, sprite.getMaxU());
            double minV = sprite.getMinV();
            double maxV = Math.min(minV + (sprite.getMaxV() - minV) * height, sprite.getMaxV());

            Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();

            //left side
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(x + width, y, z).tex(maxU, minV).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).endVertex();
            buffer.pos(x, y, z).tex(minU, minV).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).endVertex();
            buffer.pos(x, y + height, z).tex(minU, maxV).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z).tex(maxU, maxV).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).endVertex();
            tessellator.draw();

            //right side
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(x, y, z + depth).tex(maxU, minV).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).endVertex();
            buffer.pos(x + width, y, z + depth).tex(minU, minV).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z + depth).tex(minU, maxV).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).endVertex();
            buffer.pos(x, y + height, z + depth).tex(maxU, maxV).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).endVertex();
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
            buffer.pos(x, y + height, z).tex(maxU, minV).color(red, green, blue, 1.0F).endVertex();
            buffer.pos(x, y + height, z + depth).tex(minU, minV).color(red, green, blue, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z + depth).tex(minU, maxV).color(red, green, blue, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z).tex(maxU, maxV).color(red, green, blue, 1.0F).endVertex();
            tessellator.draw();

            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
        }
    }
}
