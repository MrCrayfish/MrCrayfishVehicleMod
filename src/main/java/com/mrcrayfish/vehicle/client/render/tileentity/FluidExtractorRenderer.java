package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.block.BlockFluidExtractor;
import com.mrcrayfish.vehicle.tileentity.FluidExtractorTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.ForgeHooksClient;

/**
 * Author: MrCrayfish
 */
public class FluidExtractorRenderer extends TileEntityRenderer<FluidExtractorTileEntity>
{
    public FluidExtractorRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(FluidExtractorTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer typeBuffer, int light, int p_225616_6_)
    {
        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);
        Direction direction = tileEntity.getBlockState().get(BlockFluidExtractor.DIRECTION);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(direction.getHorizontalIndex() * -90F - 90F));
        matrixStack.translate(-0.5, -0.5, -0.5);
        float height = (float) (12.0 * (tileEntity.getFluidLevel() / (double) tileEntity.getCapacity()));
        if(height > 0)
        {
            this.drawFluid(tileEntity, matrixStack, typeBuffer, 10F * 0.0625F, 2F * 0.0625F, 0.01F * 0.0625F, 5.99F * 0.0625F, height * 0.0625F, (16 - 0.02F) * 0.0625F);
        }
        matrixStack.pop();
    }

    private void drawFluid(FluidExtractorTileEntity te, MatrixStack matrixStack, IRenderTypeBuffer typeBuffer, float x, float y, float z, float width, float height, float depth)
    {
        Fluid fluid = te.getFluidStackTank().getFluid();
        if(fluid == Fluids.EMPTY) return;

        TextureAtlasSprite sprite = ForgeHooksClient.getFluidSprites(te.getWorld(), te.getPos(), fluid.getDefaultState())[0];
        float minU = sprite.getMinU();
        float maxU = Math.min(minU + (sprite.getMaxU() - minU) * width, sprite.getMaxU());
        float minV = sprite.getMinV();
        float maxV = Math.min(minV + (sprite.getMaxV() - minV) * height, sprite.getMaxV());
        int waterColor = fluid.getAttributes().getColor(te.getWorld(), te.getPos());
        float red = (float) (waterColor >> 16 & 255) / 255.0F;
        float green = (float) (waterColor >> 8 & 255) / 255.0F;
        float blue = (float) (waterColor & 255) / 255.0F;
        int light = this.getCombinedLight(te.getWorld(), te.getPos());

        IVertexBuilder buffer = typeBuffer.getBuffer(RenderType.getTranslucent());
        Matrix4f matrix = matrixStack.getLast().getMatrix();

        //left side
        buffer.pos(matrix, x + width, y, z).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(maxU, minV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x, y, z).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(minU, minV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x, y + height, z).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(minU, maxV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(maxU, maxV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();

        //right side
        buffer.pos(matrix, x, y, z + depth).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(maxU, minV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y, z + depth).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(minU, minV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z + depth).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(minU, maxV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x, y + height, z + depth).color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).tex(maxU, maxV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();

        maxU = (float) Math.min(minU + (sprite.getMaxU() - minU) * depth, sprite.getMaxU());

        //back side
        buffer.pos(matrix, x + width, y, z + depth).color(red - 0.15F, green - 0.15F, blue - 0.15F, 1.0F).tex(maxU, minV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y, z).color(red - 0.15F, green - 0.15F, blue - 0.15F, 1.0F).tex(minU, minV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z).color(red - 0.15F, green - 0.15F, blue - 0.15F, 1.0F).tex(minU, maxV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z + depth).color(red - 0.15F, green - 0.15F, blue - 0.15F, 1.0F).tex(maxU, maxV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();

        maxV = (float) Math.min(minV + (sprite.getMaxV() - minV) * width, sprite.getMaxV());

        //top
        buffer.pos(matrix, x, y + height, z).color(red, green, blue, 1.0F).tex(maxU, minV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x, y + height, z + depth).color(red, green, blue, 1.0F).tex(minU, minV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z + depth).color(red, green, blue, 1.0F).tex(minU, maxV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z).color(red, green, blue, 1.0F).tex(maxU, maxV).lightmap(light).normal(0.0F, 1.0F, 0.0F).endVertex();
    }

    private int getCombinedLight(ILightReader lightReader, BlockPos pos)
    {
        int i = WorldRenderer.getCombinedLight(lightReader, pos);
        int j = WorldRenderer.getCombinedLight(lightReader, pos.up());
        int k = i & 255;
        int l = j & 255;
        int i1 = i >> 16 & 255;
        int j1 = j >> 16 & 255;
        return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
    }
}
