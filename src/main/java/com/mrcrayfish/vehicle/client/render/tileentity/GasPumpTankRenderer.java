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
    public void func_225616_a_(GasPumpTankTileEntity gasPump, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int overlay)
    {
        BlockState state = gasPump.getWorld().getBlockState(gasPump.getPos());
        if(state.getBlock() != ModBlocks.GAS_PUMP)
            return;

        matrixStack.func_227860_a_();

        Direction facing = state.get(BlockRotatedObject.DIRECTION);
        matrixStack.func_227861_a_(0.5, 0.5, 0.5);
        matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_(facing.getHorizontalIndex() * -90F - 90F));
        matrixStack.func_227861_a_(-0.5, -0.5, -0.5);
        float height = 11.0F * (gasPump.getFluidTank().getFluidAmount() / (float) gasPump.getFluidTank().getCapacity());
        if(height > 0) drawFluid(gasPump, matrixStack, renderTypeBuffer, 3.01F * 0.0625F, 3F * 0.0625F, 4F * 0.0625F, (10 - 0.02F) * 0.0625F, height * 0.0625F, (8 - 0.02F) * 0.0625F, light);

        matrixStack.func_227865_b_();
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

        IVertexBuilder buffer = renderTypeBuffer.getBuffer(RenderType.func_228645_f_());
        Matrix4f matrix = matrixStack.func_227866_c_().func_227870_a_();

        //back side
        buffer.func_227888_a_(matrix, x + width, y, z + depth).func_227885_a_(0.85F, 0.85F, 0.85F, 1.0F).func_225583_a_(maxU, minV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x + width, y, z).func_227885_a_(0.85F, 0.85F, 0.85F, 1.0F).func_225583_a_(minU, minV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x + width, y + height, z).func_227885_a_(0.85F, 0.85F, 0.85F, 1.0F).func_225583_a_(minU, maxV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x + width, y + height, z + depth).func_227885_a_(0.85F, 0.85F, 0.85F, 1.0F).func_225583_a_(maxU, maxV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();

        //front side
        buffer.func_227888_a_(matrix, x, y, z).func_227885_a_(0.85F, 0.85F, 0.85F, 1.0F).func_225583_a_(minU, minV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x, y, z + depth).func_227885_a_(0.85F, 0.85F, 0.85F, 1.0F).func_225583_a_(maxU, minV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x, y + height, z + depth).func_227885_a_(0.85F, 0.85F, 0.85F, 1.0F).func_225583_a_(maxU, maxV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x, y + height, z).func_227885_a_(0.85F, 0.85F, 0.85F, 1.0F).func_225583_a_(minU, maxV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();

        maxV = Math.min(minV + (sprite.getMaxV() - minV) * width, sprite.getMaxV());

        //top
        buffer.func_227888_a_(matrix, x, y + height, z).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).func_225583_a_(maxU, minV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x, y + height, z + depth).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).func_225583_a_(minU, minV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x + width, y + height, z + depth).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).func_225583_a_(minU, maxV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        buffer.func_227888_a_(matrix, x + width, y + height, z).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).func_225583_a_(maxU, maxV).func_227886_a_(light).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
    }
}
