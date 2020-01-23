package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.block.BlockRotatedObject;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
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
        BlockState state = gasPump.getWorld().getBlockState(gasPump.getPos());
        if(state.getBlock() != ModBlocks.GAS_PUMP)
            return;

        matrixStack.push();

        Direction facing = state.get(BlockRotatedObject.DIRECTION);
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.rotate(Axis.POSITIVE_Y.func_229187_a_(facing.getHorizontalIndex() * -90F - 90F));
        matrixStack.translate(-0.5, -0.5, -0.5);
        float height = 11.0F * (gasPump.getFluidTank().getFluidAmount() / (float) gasPump.getFluidTank().getCapacity());
        if(height > 0) drawFluid(gasPump, matrixStack, renderTypeBuffer, 3.01F * 0.0625F, 3F * 0.0625F, 4F * 0.0625F, (10 - 0.02F) * 0.0625F, height * 0.0625F, (8 - 0.02F) * 0.0625F, light);

        matrixStack.pop();
    }

    private void drawFluid(GasPumpTankTileEntity te, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float x, float y, float z, float width, float height, float depth, int light)
    {
        if(te.getFluidTank().isEmpty())
            return;

        TextureAtlasSprite sprite = ForgeHooksClient.getFluidSprites(te.getWorld(), te.getPos(), te.getFluidTank().getFluid().getFluid().getDefaultState())[0];
        float minU = sprite.getMinU();
        float maxU = Math.min(minU + (sprite.getMaxU() - minU) * depth, sprite.getMaxU());
        float minV = sprite.getMinV();
        float maxV = Math.min(minV + (sprite.getMaxV() - minV) * height, sprite.getMaxV());

        IVertexBuilder buffer = renderTypeBuffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = matrixStack.getLast().getPositionMatrix();

        //back side
        buffer.pos(matrix, x + width, y, z + depth).color(0.85F, 0.85F, 0.85F, 1.0F).tex(maxU, minV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y, z).color(0.85F, 0.85F, 0.85F, 1.0F).tex(minU, minV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z).color(0.85F, 0.85F, 0.85F, 1.0F).tex(minU, maxV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z + depth).color(0.85F, 0.85F, 0.85F, 1.0F).tex(maxU, maxV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();

        //front side
        buffer.pos(matrix, x, y, z).color(0.85F, 0.85F, 0.85F, 1.0F).tex(minU, minV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x, y, z + depth).color(0.85F, 0.85F, 0.85F, 1.0F).tex(maxU, minV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x, y + height, z + depth).color(0.85F, 0.85F, 0.85F, 1.0F).tex(maxU, maxV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x, y + height, z).color(0.85F, 0.85F, 0.85F, 1.0F).tex(minU, maxV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();

        maxV = Math.min(minV + (sprite.getMaxV() - minV) * width, sprite.getMaxV());

        //top
        buffer.pos(matrix, x, y + height, z).color(1.0F, 1.0F, 1.0F, 1.0F).tex(maxU, minV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x, y + height, z + depth).color(1.0F, 1.0F, 1.0F, 1.0F).tex(minU, minV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z + depth).color(1.0F, 1.0F, 1.0F, 1.0F).tex(minU, maxV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(matrix, x + width, y + height, z).color(1.0F, 1.0F, 1.0F, 1.0F).tex(maxU, maxV).lightmap(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
    }
}
