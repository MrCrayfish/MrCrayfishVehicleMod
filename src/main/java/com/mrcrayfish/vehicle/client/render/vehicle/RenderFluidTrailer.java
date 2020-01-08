package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.entity.trailer.FluidTrailerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
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
    public SpecialModel getBodyModel()
    {
        return SpecialModel.FLUID_TRAILER;
    }

    @Override
    public void render(FluidTrailerEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModel.FLUID_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, false, -11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, true, 11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks);

        float height = 9.9F * (entity.getTank().getFluidAmount() / (float) entity.getTank().getCapacity()) * 0.0625F;
        this.drawFluid(entity.getTank(), matrixStack, -0.3875, -0.1875, -0.99, 0.7625F, height, 1.67F);
    }

    private void drawFluid(FluidTank tank, MatrixStack matrixStack, double x, double y, double z, float width, float height, float depth)
    {
        Fluid fluid = tank.getFluid().getFluid();
        if(fluid == Fluids.EMPTY) return;

        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        TextureAtlasSprite sprite = Minecraft.getInstance().func_228015_a_(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(fluid.getFluid().getAttributes().getStillTexture());

        float minU = sprite.getMinU();
        float maxU = Math.min(minU + (sprite.getMaxU() - minU) * width, sprite.getMaxU());
        float minV = sprite.getMinV();
        float maxV = Math.min(minV + (sprite.getMaxV() - minV) * height, sprite.getMaxV());

        RenderSystem.pushMatrix();
        RenderSystem.pushLightingAttributes();
        RenderSystem.multMatrix(matrixStack.func_227866_c_().func_227870_a_());
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        //left side
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.func_225582_a_(x + width, y, z).func_225583_a_(maxU, minV).func_227885_a_(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
        buffer.func_225582_a_(x, y, z).func_225583_a_(minU, minV).func_227885_a_(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
        buffer.func_225582_a_(x, y + height, z).func_225583_a_(minU, maxV).func_227885_a_(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
        buffer.func_225582_a_(x + width, y + height, z).func_225583_a_(maxU, maxV).func_227885_a_(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
        tessellator.draw();

        //right side
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.func_225582_a_(x, y, z + depth).func_225583_a_(maxU, minV).func_227885_a_(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
        buffer.func_225582_a_(x + width, y, z + depth).func_225583_a_(minU, minV).func_227885_a_(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
        buffer.func_225582_a_(x + width, y + height, z + depth).func_225583_a_(minU, maxV).func_227885_a_(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
        buffer.func_225582_a_(x, y + height, z + depth).func_225583_a_(maxU, maxV).func_227885_a_(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
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
        buffer.func_225582_a_(x, y + height, z).func_225583_a_(maxU, minV).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buffer.func_225582_a_(x, y + height, z + depth).func_225583_a_(minU, minV).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buffer.func_225582_a_(x + width, y + height, z + depth).func_225583_a_(minU, maxV).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buffer.func_225582_a_(x + width, y + height, z).func_225583_a_(maxU, maxV).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.popAttributes();
        RenderSystem.popMatrix();
    }
}
