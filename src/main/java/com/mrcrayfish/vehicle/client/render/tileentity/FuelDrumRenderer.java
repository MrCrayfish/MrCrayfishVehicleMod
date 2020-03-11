package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.tileentity.FuelDrumTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class FuelDrumRenderer extends TileEntityRenderer<FuelDrumTileEntity>
{
    @Override
    public void render(FuelDrumTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        if(Minecraft.getInstance().player.isSneaking())
        {
            if(tileEntityIn.hasFluid() && this.rendererDispatcher.cameraHitResult != null && this.rendererDispatcher.cameraHitResult.getType() == RayTraceResult.Type.BLOCK)
            {
                BlockRayTraceResult result = (BlockRayTraceResult) this.rendererDispatcher.cameraHitResult;
                if(result.getPos().equals(tileEntityIn.getPos()))
                {
                    this.drawFluidLabel(tileEntityIn, this.rendererDispatcher.fontRenderer, tileEntityIn.getFluidTank());
                }
            }
        }
        GlStateManager.popMatrix();
    }

    private void drawFluidLabel(FuelDrumTileEntity te, FontRenderer fontRendererIn, FluidTank tank)
    {
        if(tank.getFluid().isEmpty())
            return;

        GlStateManager.pushMatrix();
        GlStateManager.enableDepthTest();
        GlStateManager.translated(0.5, 1.25, 0.5);
        GlStateManager.rotatef(-Minecraft.getInstance().player.rotationYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(Minecraft.getInstance().player.rotationPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scalef(-0.025F, -0.025F, 0.025F);

        float level = tank.getFluidAmount() / (float) tank.getCapacity();
        double width = 30;
        double fuelWidth = width * level;
        double remainingWidth = width - fuelWidth;
        double offsetWidth = width / 2.0;

        FluidStack stack = tank.getFluid();
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureMap().getSprite(stack.getFluid().getAttributes().getStillTexture());
        if(sprite != null)
        {
            int waterColor = stack.getFluid().getAttributes().getColor(te.getWorld(), te.getPos());
            float red = (float) (waterColor >> 16 & 255) / 255.0F;
            float green = (float) (waterColor >> 8 & 255) / 255.0F;
            float blue = (float) (waterColor & 255) / 255.0F;

            float minU = sprite.getMinU();
            float maxU = sprite.getMaxU();
            float minV = sprite.getMinV();
            float maxV = sprite.getMaxV();

            float deltaV = maxV - minV;
            maxV = minV + (deltaV * 4F * 0.0625F);

            float deltaU = maxU - minU;
            maxU = minU + deltaU * level;

            GlStateManager.disableLighting();
            GlStateManager.disableTexture();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(-offsetWidth - 1, -2.0, -0.01).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            buffer.pos(-offsetWidth - 1, 5.0, -0.01).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + width + 1, 5.0, -0.01).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + width + 1, -2.0, -0.01).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            tessellator.draw();

            GlStateManager.enableTexture();
            Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.translated(0, 0, -0.05);

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(-offsetWidth, -1.0, 0.0).tex(minU, maxV).color(red, green, blue, 1.0F).endVertex();
            buffer.pos(-offsetWidth, 4.0, 0.0).tex(minU, minV).color(red, green, blue, 1.0F).endVertex();
            buffer.pos(-offsetWidth + fuelWidth, 4.0, 0.0).tex(maxU, minV).color(red, green, blue, 1.0F).endVertex();
            buffer.pos(-offsetWidth + fuelWidth, -1.0, 0.0).tex(maxU, maxV).color(red, green, blue, 1.0F).endVertex();
            tessellator.draw();

            GlStateManager.disableTexture();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(-offsetWidth + fuelWidth, -1.0, 0.0).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + fuelWidth, 4.0, 0.0).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + fuelWidth + remainingWidth, 4.0, 0.0).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + fuelWidth + remainingWidth, -1.0, 0.0).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            tessellator.draw();
        }

        GlStateManager.enableTexture();

        GlStateManager.scalef(0.5F, 0.5F, 0.5F);
        String name = stack.getDisplayName().getFormattedText();
        int nameWidth = fontRendererIn.getStringWidth(name) / 2;
        fontRendererIn.drawString(name, -nameWidth, -14, -1);

        GlStateManager.enableLighting();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableDepthTest();
        GlStateManager.popMatrix();
    }
}
