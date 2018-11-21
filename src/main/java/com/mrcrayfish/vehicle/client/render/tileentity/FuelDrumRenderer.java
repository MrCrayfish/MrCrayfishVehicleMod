package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mrcrayfish.vehicle.tileentity.TileEntityFuelDrum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class FuelDrumRenderer extends TileEntitySpecialRenderer<TileEntityFuelDrum>
{
    @Override
    public void render(TileEntityFuelDrum te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        if(Minecraft.getMinecraft().player.isSneaking())
        {
            if(te.hasFluid() && this.rendererDispatcher.cameraHitResult != null && te.getPos().equals(this.rendererDispatcher.cameraHitResult.getBlockPos()))
            {
                this.setLightmapDisabled(true);
                drawFluidLabel(getFontRenderer(), te.getFluidTank(), (float) x + 0.5F, (float) y + 1.25F, (float) z + 0.5F);
                GlStateManager.depthFunc(GL11.GL_GREATER);
                drawFluidLabel(getFontRenderer(), te.getFluidTank(), (float) x + 0.5F, (float) y + 1.25F, (float) z + 0.5F);
                GlStateManager.depthFunc(GL11.GL_LEQUAL);
                this.setLightmapDisabled(false);
            }
        }
    }

    private void drawFluidLabel(FontRenderer fontRendererIn, FluidTank tank, float x, float y, float z)
    {
        if(tank.getFluid() == null)
            return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.rendererDispatcher.entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(this.rendererDispatcher.entityPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();

        double level = tank.getFluidAmount() / (double) tank.getCapacity();
        double width = 30;
        double fuelWidth = width * level;
        double remainingWidth = width - fuelWidth;
        double offsetWidth = width / 2.0;

        FluidStack stack = tank.getFluid();
        ResourceLocation resource = stack.getFluid().getStill();
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(resource.toString());
        if(sprite != null)
        {
            double minU = sprite.getMinU();
            double maxU = sprite.getMaxU();
            double minV = sprite.getMinV();
            double maxV = sprite.getMaxV();

            double deltaV = maxV - minV;
            maxV = minV + (deltaV * 4 * 0.0625);

            double deltaU = maxU - minU;
            maxU = minU + deltaU * level;

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(-offsetWidth - 1, -2.0, -0.01).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            buffer.pos(-offsetWidth - 1, 5.0, -0.01).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + width + 1, 5.0, -0.01).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + width + 1, -2.0, -0.01).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            tessellator.draw();

            GlStateManager.enableTexture2D();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-offsetWidth, -1.0, 0.0).tex(minU, maxV).endVertex();
            buffer.pos(-offsetWidth, 4.0, 0.0).tex(minU, minV).endVertex();
            buffer.pos(-offsetWidth + fuelWidth, 4.0, 0.0).tex(maxU, minV).endVertex();
            buffer.pos(-offsetWidth + fuelWidth, -1.0, 0.0).tex(maxU, maxV).endVertex();
            tessellator.draw();

            GlStateManager.disableTexture2D();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(-offsetWidth + fuelWidth, -1.0, 0.0).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + fuelWidth, 4.0, 0.0).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + fuelWidth + remainingWidth, 4.0, 0.0).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + fuelWidth + remainingWidth, -1.0, 0.0).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            tessellator.draw();
        }

        GlStateManager.enableTexture2D();

        GlStateManager.scale(0.5, 0.5, 0.5);
        String name = stack.getLocalizedName();
        int nameWidth = fontRendererIn.getStringWidth(name) / 2;
        fontRendererIn.drawString(stack.getLocalizedName(), -nameWidth, -14, -1);

        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
