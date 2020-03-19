package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.block.BlockRotatedObject;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class GasPumpTankRenderer extends TileEntityRenderer<GasPumpTankTileEntity>
{
    @Override
    public void render(GasPumpTankTileEntity gasPump, double x, double y, double z, float partialTicks, int destroyStage)
    {
        BlockState state = gasPump.getWorld().getBlockState(gasPump.getPos());
        if(state.getBlock() != ModBlocks.GAS_PUMP.get())
            return;

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);

        Direction facing = state.get(BlockRotatedObject.DIRECTION);
        GlStateManager.translated(0.5, 0.5, 0.5);
        GlStateManager.rotatef(facing.getHorizontalIndex() * -90F - 90F, 0, 1, 0);
        GlStateManager.translated(-0.5, -0.5, -0.5);
        float height = 11.0F * (gasPump.getFluidTank().getFluidAmount() / (float) gasPump.getFluidTank().getCapacity());
        if(height > 0) drawFluid(gasPump, 3.01F * 0.0625F, 3F * 0.0625F, 4F * 0.0625F, (10 - 0.02F) * 0.0625F, height * 0.0625F, (8 - 0.02F) * 0.0625F);

        GlStateManager.popMatrix();
    }

    private void drawFluid(GasPumpTankTileEntity te, float x, float y, float z, float width, float height, float depth)
    {
        if(te.getFluidTank() == null || te.getFluidTank().getFluid() == null)
            return;

        ResourceLocation resource = te.getFluidTank().getFluid().getFluid().getAttributes().getStillTexture();
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureMap().getSprite(resource);
        if(sprite != null)
        {
            double minU = sprite.getMinU();
            double maxU = Math.min(minU + (sprite.getMaxU() - minU) * depth, sprite.getMaxU());
            double minV = sprite.getMinV();
            double maxV = Math.min(minV + (sprite.getMaxV() - minV) * height, sprite.getMaxV());

            Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

            int light = getWorld().getCombinedLight(te.getPos(), te.getFluidTank().getFluid().getFluid().getAttributes().getLuminosity());
            int lightX = light >> 0x10 & 0xFFFF;
            int lightY = light & 0xFFFF;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            //back side
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buffer.pos(x + width, y, z + depth).tex(maxU, minV).lightmap(lightX, lightY).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            buffer.pos(x + width, y, z).tex(minU, minV).lightmap(lightX, lightY).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z).tex(minU, maxV).lightmap(lightX, lightY).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z + depth).tex(maxU, maxV).lightmap(lightX, lightY).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            tessellator.draw();

            //front side
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buffer.pos(x, y, z + depth).tex(maxU, minV).lightmap(lightX, lightY).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            buffer.pos(x, y, z).tex(minU, minV).lightmap(lightX, lightY).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            buffer.pos(x, y + height, z).tex(minU, maxV).lightmap(lightX, lightY).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            buffer.pos(x, y + height, z + depth).tex(maxU, maxV).lightmap(lightX, lightY).color(0.85F, 0.85F, 0.85F, 1.0F).endVertex();
            tessellator.draw();

            maxV = Math.min(minV + (sprite.getMaxV() - minV) * width, sprite.getMaxV());

            //top
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buffer.pos(x, y + height, z).tex(maxU, minV).lightmap(lightX, lightY).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            buffer.pos(x, y + height, z + depth).tex(minU, minV).lightmap(lightX, lightY).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z + depth).tex(minU, maxV).lightmap(lightX, lightY).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            buffer.pos(x + width, y + height, z).tex(maxU, maxV).lightmap(lightX, lightY).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            tessellator.draw();
        }
    }
}
