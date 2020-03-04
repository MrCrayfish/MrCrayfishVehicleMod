package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.ISpecialModel;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.entity.trailer.FluidTrailerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * Author: MrCrayfish
 */
public class RenderFluidTrailer extends AbstractRenderTrailer<FluidTrailerEntity>
{
    @Override
    public void render(FluidTrailerEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModels.FLUID_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, false, -11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, true, 11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks, light);

        float height = 9.9F * (entity.getTank().getFluidAmount() / (float) entity.getTank().getCapacity()) * 0.0625F;
        this.drawFluid(entity, entity.getTank(), matrixStack, renderTypeBuffer, -0.3875F, -0.1875F, -0.99F, 0.7625F, height, 1.67F, light);
    }

    private void drawFluid(FluidTrailerEntity entity, FluidTank tank, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float x, float y, float z, float width, float height, float depth, int light)
    {
        Fluid fluid = tank.getFluid().getFluid();
        if(fluid == Fluids.EMPTY) return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(fluid.getFluid().getAttributes().getStillTexture());

        int waterColor = fluid.getAttributes().getColor(entity.getEntityWorld(), entity.getPosition());
        float red = (float) (waterColor >> 16 & 255) / 255.0F;
        float green = (float) (waterColor >> 8 & 255) / 255.0F;
        float blue = (float) (waterColor & 255) / 255.0F;
        float minU = sprite.getMinU();
        float maxU = Math.min(minU + (sprite.getMaxU() - minU) * width, sprite.getMaxU());
        float minV = sprite.getMinV();
        float maxV = Math.min(minV + (sprite.getMaxV() - minV) * height, sprite.getMaxV());

        IVertexBuilder buffer = renderTypeBuffer.getBuffer(RenderType.translucentNoCrumbling());
        Matrix4f matrix = matrixStack.getLast().getPositionMatrix();

        //left side
        buffer.pos(matrix, x + width, y, z).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(maxU, minV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x, y, z).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(minU, minV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x, y + height, z).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(minU, maxV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(maxU, maxV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();

        buffer.pos(matrix, x, y, z + depth).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(maxU, minV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y, z + depth).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(minU, minV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z + depth).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(minU, maxV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x, y + height, z + depth).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(maxU, maxV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();

        maxU = Math.min(minU + (sprite.getMaxU() - minU) * depth, sprite.getMaxU());
        maxV = Math.min(minV + (sprite.getMaxV() - minV) * width, sprite.getMaxV());

        buffer.pos(matrix, x, y + height, z).color(red, green, blue, 1.0F).tex(maxU, minV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x, y + height, z + depth).color(red, green, blue, 1.0F).tex(minU, minV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z + depth).color(red, green, blue, 1.0F).tex(minU, maxV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z).color(red, green, blue, 1.0F).tex(maxU, maxV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
    }
}
