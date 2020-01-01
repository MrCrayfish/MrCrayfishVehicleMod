package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.vehicle.block.BlockFluidExtractor;
import com.mrcrayfish.vehicle.tileentity.FluidExtractorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

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
    public void func_225616_a_(FluidExtractorTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer typeBuffer, int light, int p_225616_6_)
    {
        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(0.5, 0.5, 0.5);
        Direction direction = tileEntity.getBlockState().get(BlockFluidExtractor.DIRECTION);
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(direction.getHorizontalIndex() * -90F - 90F));
        matrixStack.func_227861_a_(-0.5, -0.5, -0.5);
        double height = 12.0 * (tileEntity.getFluidLevel() / (double) FluidExtractorTileEntity.TANK_CAPACITY);
        if(height > 0)
        {
            drawFluid(tileEntity, matrixStack, 10 * 0.0625, 2 * 0.0625, 0.01 * 0.0625, 5.99 * 0.0625, height * 0.0625, (16 - 0.02) * 0.0625, light);
        }
        matrixStack.func_227865_b_();
    }

    private void drawFluid(FluidExtractorTileEntity te, MatrixStack matrixStack, double x, double y, double z, double width, double height, double depth, int light)
    {
        Fluid fluid = te.getFluidStackTank().getFluid();
        if(fluid == Fluids.EMPTY) return;

        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        TextureAtlasSprite sprite = ForgeHooksClient.getFluidSprites(te.getWorld(), te.getPos(), fluid.getDefaultState())[0];
        float minU = sprite.getMinU();
        float maxU = (float) Math.min(minU + (sprite.getMaxU() - minU) * width, sprite.getMaxU());
        float minV = sprite.getMinV();
        float maxV = (float) Math.min(minV + (sprite.getMaxV() - minV) * height, sprite.getMaxV());
        int waterColor = fluid.getAttributes().getColor(te.getWorld(), te.getPos());
        float red = (float) (waterColor >> 16 & 255) / 255.0F;
        float green = (float) (waterColor >> 8 & 255) / 255.0F;
        float blue = (float) (waterColor & 255) / 255.0F;

        RenderSystem.pushMatrix();
        RenderSystem.pushLightingAttributes();
        RenderSystem.multMatrix(matrixStack.func_227866_c_().func_227870_a_());
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        //left side
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        buffer.func_225582_a_(x + width, y, z).func_225583_a_(maxU, minV).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_227886_a_(light).endVertex();
        buffer.func_225582_a_(x, y, z).func_225583_a_(minU, minV).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_227886_a_(light).endVertex();
        buffer.func_225582_a_(x, y + height, z).func_225583_a_(minU, maxV).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_227886_a_(light).endVertex();
        buffer.func_225582_a_(x + width, y + height, z).func_225583_a_(maxU, maxV).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_227886_a_(light).endVertex();
        tessellator.draw();

        //right side
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        buffer.func_225582_a_(x, y, z + depth).func_225583_a_(maxU, minV).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_227886_a_(light).endVertex();
        buffer.func_225582_a_(x + width, y, z + depth).func_225583_a_(minU, minV).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_227886_a_(light).endVertex();
        buffer.func_225582_a_(x + width, y + height, z + depth).func_225583_a_(minU, maxV).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_227886_a_(light).endVertex();
        buffer.func_225582_a_(x, y + height, z + depth).func_225583_a_(maxU, maxV).func_227885_a_(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).func_227886_a_(light).endVertex();
        tessellator.draw();

        maxU = (float) Math.min(minU + (sprite.getMaxU() - minU) * depth, sprite.getMaxU());

        //back side
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        buffer.func_225582_a_(x + width, y, z + depth).func_225583_a_(maxU, minV).func_227885_a_(red - 0.15F, green - 0.15F, blue - 0.15F, 1.0F).func_227886_a_(light).endVertex();
        buffer.func_225582_a_(x + width, y, z).func_225583_a_(minU, minV).func_227885_a_(red - 0.15F, green - 0.15F, blue - 0.15F, 1.0F).func_227886_a_(light).endVertex();
        buffer.func_225582_a_(x + width, y + height, z).func_225583_a_(minU, maxV).func_227885_a_(red - 0.15F, green - 0.15F, blue - 0.15F, 1.0F).func_227886_a_(light).endVertex();
        buffer.func_225582_a_(x + width, y + height, z + depth).func_225583_a_(maxU, maxV).func_227885_a_(red - 0.15F, green - 0.15F, blue - 0.15F, 1.0F).func_227886_a_(light).endVertex();
        tessellator.draw();

        maxV = (float) Math.min(minV + (sprite.getMaxV() - minV) * width, sprite.getMaxV());

        //top
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        buffer.func_225582_a_(x, y + height, z).func_225583_a_(maxU, minV).func_227885_a_(red, green, blue, 1.0F).func_227886_a_(light).endVertex();
        buffer.func_225582_a_(x, y + height, z + depth).func_225583_a_(minU, minV).func_227885_a_(red, green, blue, 1.0F).func_227886_a_(light).endVertex();
        buffer.func_225582_a_(x + width, y + height, z + depth).func_225583_a_(minU, maxV).func_227885_a_(red, green, blue, 1.0F).func_227886_a_(light).endVertex();
        buffer.func_225582_a_(x + width, y + height, z).func_225583_a_(maxU, maxV).func_227885_a_(red, green, blue, 1.0F).func_227886_a_(light).endVertex();
        tessellator.draw();

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.popAttributes();
        RenderSystem.popMatrix();
    }
}
