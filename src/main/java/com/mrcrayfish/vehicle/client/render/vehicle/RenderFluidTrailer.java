package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.entity.trailer.FluidTrailerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
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
    public SpecialModel getBodyModel()
    {
        return SpecialModel.FLUID_TRAILER;
    }

    @Override
    public void render(FluidTrailerEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModel.FLUID_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, false, -11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, true, 11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks, light);

        float height = 9.9F * (entity.getTank().getFluidAmount() / (float) entity.getTank().getCapacity()) * 0.0625F;
        this.drawFluid(entity, entity.getTank(), matrixStack, renderTypeBuffer, -0.3875F, -0.1875F, -0.99F, 0.7625F, height, 1.67F, light);
    }

    private void drawFluid(FluidTrailerEntity entity, FluidTank tank, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float x, float y, float z, float width, float height, float depth, int light)
    {
        Fluid fluid = tank.getFluid().getFluid();
        if(fluid == Fluids.EMPTY) return;

        TextureAtlasSprite sprite = Minecraft.getInstance().func_228015_a_(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(fluid.getFluid().getAttributes().getStillTexture());

        int waterColor = fluid.getAttributes().getColor(entity.getEntityWorld(), entity.getPosition());
        float red = (float) (waterColor >> 16 & 255) / 255.0F;
        float green = (float) (waterColor >> 8 & 255) / 255.0F;
        float blue = (float) (waterColor & 255) / 255.0F;
        float minU = sprite.getMinU();
        float maxU = Math.min(minU + (sprite.getMaxU() - minU) * width, sprite.getMaxU());
        float minV = sprite.getMinV();
        float maxV = Math.min(minV + (sprite.getMaxV() - minV) * height, sprite.getMaxV());

        IVertexBuilder buffer = renderTypeBuffer.getBuffer(RenderType.func_228647_g_());
        Matrix4f matrix = matrixStack.func_227866_c_().func_227870_a_();

        //left side
        buffer.func_227888_a_(matrix, x + width, y, z).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_225583_a_(maxU, minV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x, y, z).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_225583_a_(minU, minV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x, y + height, z).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_225583_a_(minU, maxV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x + width, y + height, z).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_225583_a_(maxU, maxV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();

        buffer.func_227888_a_(matrix, x, y, z + depth).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_225583_a_(maxU, minV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x + width, y, z + depth).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_225583_a_(minU, minV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x + width, y + height, z + depth).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_225583_a_(minU, maxV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x, y + height, z + depth).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_225583_a_(maxU, maxV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();

        maxU = Math.min(minU + (sprite.getMaxU() - minU) * depth, sprite.getMaxU());
        maxV = Math.min(minV + (sprite.getMaxV() - minV) * width, sprite.getMaxV());

        buffer.func_227888_a_(matrix, x, y + height, z).func_227885_a_(red, green, blue, 1.0F).func_225583_a_(maxU, minV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x, y + height, z + depth).func_227885_a_(red, green, blue, 1.0F).func_225583_a_(minU, minV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x + width, y + height, z + depth).func_227885_a_(red, green, blue, 1.0F).func_225583_a_(minU, maxV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x + width, y + height, z).func_227885_a_(red, green, blue, 1.0F).func_225583_a_(maxU, maxV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
    }
}
