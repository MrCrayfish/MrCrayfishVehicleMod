package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.block.RotatedObjectBlock;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.client.ForgeHooksClient;

/**
 * Author: MrCrayfish
 */
public class GasPumpTankRenderer extends TileEntityRenderer<GasPumpTankTileEntity>
{
    public GasPumpTankRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(GasPumpTankTileEntity gasPump, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int overlay)
    {
        BlockState state = gasPump.getLevel().getBlockState(gasPump.getBlockPos());
        if(state.getBlock() != ModBlocks.GAS_PUMP.get())
            return;

        matrixStack.pushPose();

        Direction facing = state.getValue(RotatedObjectBlock.DIRECTION);
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(facing.get2DDataValue() * -90F - 90F));
        matrixStack.translate(-0.5, -0.5, -0.5);
        float height = 11.0F * (gasPump.getFluidTank().getFluidAmount() / (float) gasPump.getFluidTank().getCapacity());
        if(height > 0) this.drawFluid(gasPump, matrixStack, renderTypeBuffer, 2.01F * 0.0625F, 4F * 0.0625F, 5F * 0.0625F, (12 - 0.02F) * 0.0625F, height * 0.0625F, 6F * 0.0625F, light);

        matrixStack.popPose();
    }

    private void drawFluid(GasPumpTankTileEntity te, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float x, float y, float z, float width, float height, float depth, int light)
    {
        if(te.getFluidTank().isEmpty())
            return;

        TextureAtlasSprite sprite = ForgeHooksClient.getFluidSprites(te.getLevel(), te.getBlockPos(), te.getFluidTank().getFluid().getFluid().defaultFluidState())[0];
        int waterColor = te.getFluidTank().getFluid().getFluid().getAttributes().getColor(te.getLevel(), te.getBlockPos());
        float red = (float) (waterColor >> 16 & 255) / 255.0F;
        float green = (float) (waterColor >> 8 & 255) / 255.0F;
        float blue = (float) (waterColor & 255) / 255.0F;
        float minU = sprite.getU0();
        float maxU = Math.min(minU + (sprite.getU1() - minU) * depth, sprite.getU1());
        float minV = sprite.getV0();
        float maxV = Math.min(minV + (sprite.getV1() - minV) * height, sprite.getV1());

        IVertexBuilder buffer = renderTypeBuffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = matrixStack.last().pose();
        
        float side = 0.9F;

        //back side
        buffer.vertex(matrix, x + width, y, z + depth).color(red * side, green * side, blue * side, 1.0F).uv(maxU, minV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y, z).color(red * side, green * side, blue * side, 1.0F).uv(minU, minV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y + height, z).color(red * side, green * side, blue * side, 1.0F).uv(minU, maxV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y + height, z + depth).color(red * side, green * side, blue * side, 1.0F).uv(maxU, maxV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();

        //front side
        buffer.vertex(matrix, x, y, z).color(red * side, green * side, blue * side, 1.0F).uv(minU, minV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x, y, z + depth).color(red * side, green * side, blue * side, 1.0F).uv(maxU, minV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x, y + height, z + depth).color(red * side, green * side, blue * side, 1.0F).uv(maxU, maxV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x, y + height, z).color(red * side, green * side, blue * side, 1.0F).uv(minU, maxV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();

        maxV = Math.min(minV + (sprite.getV1() - minV) * width, sprite.getV1());

        //top
        buffer.vertex(matrix, x, y + height, z).color(red, green, blue, 1.0F).uv(maxU, minV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x, y + height, z + depth).color(red, green, blue, 1.0F).uv(minU, minV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y + height, z + depth).color(red, green, blue, 1.0F).uv(minU, maxV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y + height, z).color(red, green, blue, 1.0F).uv(maxU, maxV).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
    }
}
